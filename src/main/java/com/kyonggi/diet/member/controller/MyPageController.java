package com.kyonggi.diet.member.controller;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.MyPageControllerDocs;
import com.kyonggi.diet.member.DTO.MyPageDTO;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import com.kyonggi.diet.member.service.MyPageService;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
@Tag(name = "마이 페이지 API", description = "마이페이지에 대한 API 입니다.")
public class MyPageController implements MyPageControllerDocs {

    private final JwtTokenUtil jwtTokenUtil;
    private final MyPageService myPageService;
    private final CustomMembersDetailService customMembersDetailService;

    // ------------------- [1] 내 기본 정보 -------------------
    @GetMapping("/info")
    public ResponseEntity<?> getMyInfo(@RequestHeader("Authorization") String token) {
        String email = extractEmail(token);
        if (email == null)
            return unauthorized();

        MyPageDTO dto = myPageService.getMyPage(email);
        return ResponseEntity.ok(dto);
    }

    // ------------------- [2] 내가 쓴 리뷰 (페이징) -------------------
    @GetMapping("/reviews")
    public ResponseEntity<?> getMyReviews(
            @RequestHeader("Authorization") String token,
            @RequestParam("type") RestaurantType type,
            @RequestParam(defaultValue = "0") int page) {

        String email = extractEmail(token);
        if (email == null)
            return unauthorized();

        Page<ReviewDTO> result = myPageService.getMyReviews(email, type, page);
        return ResponseEntity.ok(result);
    }

    // ------------------- [3] 내가 좋아요한 리뷰 (페이징) -------------------
    @GetMapping("/favorites")
    public ResponseEntity<?> getMyFavoriteReviews(
            @RequestHeader("Authorization") String token,
            @RequestParam("type") RestaurantType type,
            @RequestParam(defaultValue = "0") int page) {

        String email = extractEmail(token);
        if (email == null)
            return unauthorized();

        Page<ReviewDTO> result = myPageService.getMyFavoriteReviews(email, type, page);
        return ResponseEntity.ok(result);
    }

    // ------------------- [토큰 검증 공통 로직] -------------------
    private String extractEmail(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        try {
            UserDetails userDetails = customMembersDetailService.loadUserByUsername(email);
            boolean valid = jwtTokenUtil.validateToken(jwt, userDetails);
            return valid ? email : null;
        } catch (Exception e) {
            return null;
        }
    }

    private ResponseEntity<String> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
}