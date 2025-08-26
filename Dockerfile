FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# JAR 복사
COPY build/libs/diet-0.0.1-SNAPSHOT.jar app.jar

# 실행 (중요: -jar 붙여야 함)
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app/app.jar"]
