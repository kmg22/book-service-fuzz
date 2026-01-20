# 1. Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
# Jazzer 퍼징 테스트를 포함하여 빌드 (실패 시 배포 중단)
RUN ./gradlew build -Djazzer.duration=30 --no-daemon

# 2. Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
EXPOSE 9000
COPY --from=build /app/build/libs/*.jar app.jar
# 실행 시 프로파일을 지정하여 secret 설정 적용
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]