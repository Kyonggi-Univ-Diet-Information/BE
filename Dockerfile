# 1단계: 빌드 환경 (라즈베리파이에 자바가 없어도 여기서 알아서 빌드합니다)
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
# 프로젝트의 모든 코드를 도커 컨테이너 안으로 복사
COPY . .
# gradlew 실행 권한 부여 및 테스트를 제외한 빌드 진행
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 2단계: 실행 환경 (빌드된 파일만 가져와서 가볍게 실행)
FROM eclipse-temurin:17-jre
WORKDIR /app
# 1단계에서 만들어진 jar 파일만 복사해옴
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

# 스프링 부트 실행
ENTRYPOINT ["java", "-jar", "app.jar"]