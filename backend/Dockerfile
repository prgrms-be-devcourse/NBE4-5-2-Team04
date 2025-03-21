# 1단계: 빌드 환경 (Amazon Corretto + Node.js 설치)
FROM amazoncorretto:23 AS builder
WORKDIR /app

# 필수 패키지 설치 (findutils 포함)
RUN yum install -y findutils

# Gradle Wrapper 및 설정 파일 복사
COPY gradlew gradlew.bat ./
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Gradle 의존성 다운로드
RUN ./gradlew dependencies --no-daemon

# 전체 프로젝트 복사 후 빌드 실행
COPY . .
RUN ./gradlew build -x test --no-daemon

# 2단계: 실행 환경 (Amazon Corretto 23 사용)
FROM amazoncorretto:23 AS final
WORKDIR /app

# 실행 환경에 Node.js 및 npm 설치 (npx 사용을 위한 준비)
RUN yum install -y curl --skip-broken \
    && curl -fsSL https://rpm.nodesource.com/setup_22.x | bash - \
    && yum install -y nodejs \
    && npm install -g npm

# 필요한 패키지 설치 (npx가 필요한 패키지들 설치)
RUN npm install punycode@2.3.1 openapi-typescript@7.6.1 typescript@5.8.2

# Gradle 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 불필요한 패키지 삭제
RUN rm -rf /app/node_modules

# 실행 환경에서 Java 애플리케이션 실행
ENTRYPOINT ["java", "-Xms64m", "-Xmx128m", "-jar", "app.jar"]


## 최적화

# 1단계: 빌드 환경 (Amazon Corretto + Gradle + Node.js)
# FROM amazoncorretto:23-alpine AS builder
# WORKDIR /app

# # 필수 패키지 설치 (findutils 포함)
# RUN apk add --no-cache findutils curl nodejs npm

# # Gradle Wrapper 및 설정 파일 복사
# COPY gradlew gradlew.bat ./  
# COPY gradle gradle
# COPY build.gradle settings.gradle ./

# # Gradle 의존성 다운로드 (캐싱 최적화)
# RUN ./gradlew dependencies --no-daemon

# # 전체 프로젝트 복사 후 빌드 실행
# COPY . .
# RUN ./gradlew build -x test --no-daemon

# # TypeScript 및 필요한 패키지 설치 (빌드 환경에서)
# RUN npm install punycode@2.3.1 openapi-typescript@7.6.1 typescript@5.8.2 --omit=dev --no-audit --no-fund

# ---

# # 2단계: 실행 환경 (Amazon Corretto 23 Alpine)
# FROM amazoncorretto:23-alpine AS final
# WORKDIR /app

# # 실행 환경에 Node.js 및 npm 설치
# RUN apk add --no-cache curl nodejs npm

# # Gradle 빌드된 JAR 파일 복사
# COPY --from=builder /app/build/libs/*.jar app.jar

# # 실행 환경에서 필요한 TypeScript 관련 패키지 설치
# COPY package*.json ./
# RUN npm install --omit=dev --no-audit --no-fund

# # 실행 환경에서 Java 애플리케이션 실행
# ENTRYPOINT ["java", "-Xms64m", "-Xmx128m", "-jar", "app.jar"]
