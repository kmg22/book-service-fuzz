# 1. Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test --no-daemon

# 2. Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
EXPOSE 9010
COPY --from=build /app/build/libs/*[!plain].jar app.jar
# 실행 시 프로파일을 지정하여 secret 설정 적용
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]