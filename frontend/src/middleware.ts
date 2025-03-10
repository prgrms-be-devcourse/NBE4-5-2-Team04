import { NextResponse, type NextRequest } from "next/server";
import client from "./lib/backend/client";
import { cookies } from "next/headers";
import { RequestCookie } from "next/dist/compiled/@edge-runtime/cookies";

// 인증이 필요하지 않은 공개 경로 정의 - 좀 더 확장
const PUBLIC_ROUTES = [
  "/member/login",
  "/member/logout",
  "/member/logout",
  "/static",
  "/_next",
  "/favicon.ico",
  "/images", // 이미지 경로
  "/css", // CSS 경로
  "/js", // JavaScript 경로
  "/fonts", // 폰트 경로
  "/public", // public 폴더
  // 필요에 따라 더 많은 공개 경로 추가
];

export async function middleware(request: NextRequest) {
  const path = request.nextUrl.pathname;

  if (path.startsWith("/member/login") || path.startsWith("/member/logout")) {
    return NextResponse.next();
  }

  const myCookies = await cookies();
  const accessToken = myCookies.get("accessToken");
  const refreshToken = myCookies.get("refreshToken");

  // 액세스 토큰, 리프레쉬 토큰이 없으면 로그인페이지로
  if (!accessToken && !refreshToken) {
    return redirectToLogin(request);
  }

  // 액세스 토큰이 있으면 검증
  if (accessToken) {
    const { isLogin, isExpired } = parseAccessToken(accessToken);

    // 만료되지 않은 유효한 토큰이면 통과
    if (isLogin && !isExpired) {
      return NextResponse.next();
    }

    // 만료된 토큰이고 리프레시 토큰이 있으면 갱신 시도
    if (isLogin && isExpired && refreshToken) {
      try {
        return await refreshAccessToken(request);
      } catch (error) {
        return logout(request);
      }
    } else {
      return redirectToLogin(request);
    }
  }

  // 액세스 토큰은 없지만 리프레시 토큰이 있는 경우
  if (!accessToken && refreshToken) {
    try {
      return await refreshAccessToken(request);
    } catch (error) {
      return redirectToLogin(request);
    }
  }

  // 그 외 모든 경우는 로그인 페이지로 리다이렉트
  return redirectToLogin(request);
}

// 로그인 페이지로 리다이렉션하는 함수
function redirectToLogin(request: NextRequest) {
  const url = request.nextUrl.clone();
  url.pathname = "/member/login";
  return NextResponse.redirect(url);
}

function logout(request: NextRequest) {
  const url = request.nextUrl.clone();
  url.pathname = "/member/logout";
  return NextResponse.redirect(url);
}

async function refreshAccessToken(request: NextRequest) {
  try {
    const nextResponse = NextResponse.next();

    // 리프레시 토큰을 사용하여 새 액세스 토큰 요청
    const response = await client.GET("/api/members/refresh", {
      headers: {
        cookie: (await cookies()).toString(),
      },
    });

    if (!response.response.ok) {
      return logout(request);
    }

    const springCookie = response.response.headers.getSetCookie();
    if (springCookie) {
      nextResponse.headers.set("set-cookie", String(springCookie));
    }
    return nextResponse;
  } catch (error) {
    return redirectToLogin(request);
  }
}

function parseAccessToken(accessToken: RequestCookie | undefined) {
  let isExpired = true;
  let payload = null;

  if (accessToken) {
    try {
      const tokenParts = accessToken.value.split(".");

      // 토큰이 최소 3부분으로 구성되어 있는지 확인
      if (tokenParts.length < 3) {
        throw new Error("유효하지 않은 토큰 형식");
      }

      // base64 페이로드 안전하게 디코딩
      // 특수 문자 변환 및 패딩 처리
      const base64Payload = tokenParts[1].replace(/-/g, "+").replace(/_/g, "/");
      const paddedBase64 = base64Payload.padEnd(
        base64Payload.length + ((4 - (base64Payload.length % 4)) % 4),
        "="
      );

      payload = JSON.parse(Buffer.from(paddedBase64, "base64").toString());

      // exp 필드가 존재하고 숫자인지 확인
      if (payload && typeof payload.exp === "number") {
        const expTimestamp = payload.exp * 1000; // 초 단위를 밀리초로 변환
        isExpired = Date.now() > expTimestamp;
      } else {
        // exp 필드가 없는 경우 만료된 것으로 간주
        isExpired = true;
      }
    } catch (e) {
      // 유효하지 않은 토큰은 로그인되지 않은 상태로 처리
      payload = null;
    }
  }

  const isLogin = payload !== null;
  return { isLogin, isExpired, payload };
}

function isPublicRoute(pathname: string): boolean {
  // 인증 없이 항상 접근 가능한 공개 경로인지 확인
  return PUBLIC_ROUTES.some(
    (route) => pathname === route || pathname.startsWith(`${route}/`)
  );
}

export const config = {
  // 정적 파일, 이미지 및 API 경로를 제외한 매처 - 더 명확하게 정의
  matcher: [
    // 특정 경로만 미들웨어 적용
    "/((?!_next/static|_next/image|favicon\\.ico|public|images|css|js|fonts|api|.+\\..+).*)",
  ],
};
