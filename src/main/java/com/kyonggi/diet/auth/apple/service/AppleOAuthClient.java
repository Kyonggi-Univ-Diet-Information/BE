package com.kyonggi.diet.auth.apple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyonggi.diet.auth.apple.dto.AppleDto;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.management.openmbean.InvalidKeyException;
import java.io.*;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleOAuthClient {

    private static final String APPLE_AUTH_URL = "https://appleid.apple.com";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";
    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";
    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_REVOKE_URL = "https://appleid.apple.com/auth/revoke";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${social-login.provider.apple.team-id}")
    private String teamId;

    @Value("${social-login.provider.apple.key-id}")
    private String keyId;

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String clientId;

    @Value("${social-login.provider.apple.redirect-uri}")
    private String redirectUri;

    @Value("${social-login.provider.apple.key-path}")
    private String keyPath;

    /**
     * Apple authorize URL 생성
     */
    public String buildAuthorizeUrl(String state, String nonce) {

        return UriComponentsBuilder
                .fromHttpUrl(APPLE_AUTH_URL + "/auth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code id_token")
                .queryParam("scope", "name email")
                .queryParam("response_mode", "form_post")
                .queryParam("state", state)
                .queryParam("nonce", nonce)
                .build()
                .encode()
                .toUriString();
    }


    public AppleDto getAppleInfo(String code, String expectedNonce) throws Exception {
        String clientSecret = createClientSecret();
        String tokenResponseJson = requestToken(code, clientSecret);

        JsonNode tokenNode = objectMapper.readTree(tokenResponseJson);

        String accessToken = textOrNull(tokenNode.get("access_token"));
        String idToken = textOrNull(tokenNode.get("id_token"));
        if (idToken == null) throw new IllegalStateException("id_token missing");

        SignedJWT signedJWT = SignedJWT.parse(idToken);

        // Apple 공개키 서명 검증
        if (!verifyAppleIdTokenSignature(signedJWT)) {
            throw new IllegalStateException("Invalid id_token signature");
        }
        JWTClaimsSet claims = JWTClaimsSet.parse(signedJWT.getJWTClaimsSet().toJSONObject());

        // issuer 검증
        if (!APPLE_ISSUER.equals(claims.getIssuer())) {
            throw new IllegalStateException("Invalid issuer");
        }

        // audience 검증
        if (!clientId.equals(claims.getAudience().get(0))) {
            throw new IllegalStateException("Invalid audience");
        }

        // 만료 시간 검증
        if (claims.getExpirationTime() == null || new Date().after(claims.getExpirationTime())) {
            throw new IllegalStateException("id_token expired");
        }

        // nonce 검증
        String idTokenNonce = claims.getStringClaim("nonce");
        if (!expectedNonce.equals(idTokenNonce)) {
            throw new IllegalStateException("Invalid nonce");
        }

        String userId = claims.getSubject();
        String email = safeStringClaim(claims, "email");

        return AppleDto.builder()
                .sub(userId)
                .token(accessToken)
                .email(email)
                .build();
    }

    /**
     * Apple 공개키로 JWT 서명 검증
     */
    private boolean verifyAppleIdTokenSignature(SignedJWT signedJWT) {
        try {
            String jwksJson = new RestTemplate().getForObject(APPLE_JWKS_URL, String.class);
            JWKSet jwkSet = JWKSet.parse(jwksJson);

            JWK matchedKey = jwkSet.getKeys().stream()
                    .filter(k -> k.getKeyID().equals(signedJWT.getHeader().getKeyID()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No matching Apple public key found"));
            JWSVerifier verifier;

            if (matchedKey instanceof ECKey ecKey) {
                verifier = new ECDSAVerifier(ecKey);
            } else if (matchedKey instanceof RSAKey rsaKey) {
                verifier = new RSASSAVerifier(rsaKey);
            } else {
                throw new IllegalStateException("Unsupported key type: " + matchedKey.getKeyType());
            }

            return signedJWT.verify(verifier);
        } catch (Exception e) {
            log.error("Apple id_token verification failed", e);
            return false;
        }
    }




    /**
     * Apple Token API 호출
     */
    private String requestToken(String code, String clientSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                APPLE_TOKEN_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Apple token response not 2xx: " + response.getStatusCode());
        }

        return response.getBody();
    }

    /**
     * client_secret 생성 (ES256)
     */
    private String createClientSecret() throws Exception {
        Date now = new Date();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .keyID(keyId)
                .type(JOSEObjectType.JWT)
                .build();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(teamId)
                .subject(clientId)
                .audience(APPLE_AUTH_URL)
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + 3600_000))
                .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        try {
            ECPrivateKey ecPrivateKey = loadEcPrivateKey();
            JWSSigner jwsSigner = new ECDSASigner(ecPrivateKey);
            jwt.sign(jwsSigner);
            return jwt.serialize();
        } catch (InvalidKeyException | JOSEException e) {
            throw new Exception("Failed create client secret");
        }
    }

    /**
     *  p8 private key 로딩 (jar-safe)
     */
    private ECPrivateKey loadEcPrivateKey() throws Exception {
        byte[] pkcs8 = readPkcs8FromP8(keyPath);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return (ECPrivateKey) kf.generatePrivate(spec);
    }

    /**
     * p8 파일에서 PKCS8 바이트 추출
     */
    private byte[] readPkcs8FromP8(String classpathOrPath) throws Exception {
        ClassPathResource resource = new ClassPathResource(classpathOrPath);

        if (!resource.exists()) {
            return readPkcs8FromFileSystem(classpathOrPath);
        }

        try (InputStream is = resource.getInputStream();
             PemReader pemReader = new PemReader(new InputStreamReader(is))) {

            PemObject pemObject = pemReader.readPemObject();
            if (pemObject == null) throw new IllegalStateException("p8 pemObject is null");

            return pemObject.getContent();
        }
    }

    private byte[] readPkcs8FromFileSystem(String path) throws Exception {
        try (InputStream is = new java.io.FileInputStream(path);
             PemReader pemReader = new PemReader(new InputStreamReader(is))) {

            PemObject pemObject = pemReader.readPemObject();
            if (pemObject == null) throw new IllegalStateException("p8 pemObject is null");
            return pemObject.getContent();
        }
    }

    private String safeStringClaim(JWTClaimsSet claims, String key) {
        try {
            Object v = claims.getClaim(key);
            return v == null ? null : String.valueOf(v);
        } catch (Exception e) {
            return null;
        }
    }

    private String textOrNull(JsonNode node) {
        return node == null ? null : node.asText(null);
    }

    /**
     * Apple 최초 로그인 시 name은 id_token에 항상 안 옴.
     * Apple이 POST로 주는 user JSON에서 name이 올 수 있음.
     */
    public String extractNameFromUser(String userJson) {
        if (userJson == null || userJson.isBlank()) return null;

        try {
            JsonNode node = objectMapper.readTree(userJson);
            JsonNode nameNode = node.get("name");
            if (nameNode == null) return null;

            String first = nameNode.path("firstName").asText("");
            String last = nameNode.path("lastName").asText("");
            String merged = (last + first).trim();
            return merged.isBlank() ? null : merged;
        } catch (Exception e) {
            return null;
        }
    }


}