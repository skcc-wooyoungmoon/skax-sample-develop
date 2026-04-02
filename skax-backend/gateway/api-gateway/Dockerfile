ARG ACR_URL=docker.io/library
# [1단계] Build Stage: OS 일관성을 위해 noble 선택
#FROM ${ACR_URL}/eclipse-temurin:21-jdk-noble AS build
FROM eclipse-temurin:21-jdk-noble AS build
WORKDIR /app

# Gradle Wrapper 및 설정 파일 복사 (캐싱 활용)
COPY gradlew .
COPY gradle gradle
COPY gradle.properties .
COPY build.gradle settings.gradle ./
# Windows 체크아웃 시 CRLF·실행 비트 누락으로 ./gradlew 가 127 실패하는 것 방지
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 복사 및 빌드
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# [2단계] Run Stage: 최신 LTS 실행 환경
#FROM ${ACR_URL}/eclipse-temurin:21-jre-noble
FROM eclipse-temurin:21-jre-noble
WORKDIR /app

# 시간대 설정
ENV TZ=Asia/Seoul
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 보안을 위한 비관리자 계정 생성 (Ubuntu 표준 방식)
RUN useradd -ms /bin/bash springuser
USER springuser

# 빌드 결과물만 복사
COPY --from=build /app/build/libs/*.jar app.jar

# JVM 최적화 옵션 (Java 21 가상 스레드 환경 고려)
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]