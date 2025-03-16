# 빌드 환경 (Next.js 빌드)
FROM node:22-alpine AS builder
WORKDIR /app

# package.json 복사 및 의존성 설치
COPY package*.json ./

# 기존 의존성 설치
RUN npm install --only=production

# 소스 코드 복사 및 Next.js 빌드
COPY . .
RUN npm run build

# ───────────────────────────────────────────────────────────────────────
# 2 실행 환경 (불필요한 파일 제거)
FROM node:22-alpine
WORKDIR /app

# 빌드된 결과물만 복사
COPY --from=builder /app/.next .next
COPY --from=builder /app/public public
COPY --from=builder /app/package.json package.json

# node_modules 복사하는 대신 새로 설치
RUN npm install --only=production

# Next.js 실행
EXPOSE 3000
CMD ["npm", "start"]
