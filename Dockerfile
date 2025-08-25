# ===== Build Stage =====
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /src

# gradlew + gradle wrapper 먼저 복사(캐시용)
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

# ⚠️ CRLF → LF로 변환 + 실행권한 부여
# (Windows에서 커밋된 gradlew를 안전하게 처리)
RUN apt-get update && apt-get install -y dos2unix \
 && dos2unix gradlew \
 && chmod +x gradlew \
 && rm -rf /var/lib/apt/lists/*

# 의존성 캐시 (실패 무시해 캐시 유지)
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 후 빌드
COPY src ./src
RUN ./gradlew clean bootJar -x test --no-daemon


# ===== Runtime =====
FROM eclipse-temurin:17-jre-jammy
RUN groupadd -r app && useradd -r -g app app
WORKDIR /app
COPY --from=build /src/build/libs/*.jar /app/app.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Seoul -XX:MaxRAMPercentage=75 -XX:+UseContainerSupport"
EXPOSE 8080
USER app
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
