"use client";

import client from "@/lib/backend/client";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import {
  saveAccessTokenFromCookie,
  getAccessToken,
  getUserIdFromToken,
} from "@/app/utils/auth";

export default function ClientLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  const router = useRouter();
  const pathname = usePathname();
  const [isLogin, setIsLogin] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const isLoginPage = pathname === "/member/login";

  // 로그인 상태 확인
  useEffect(() => {
    if (typeof window === "undefined") return;

    saveAccessTokenFromCookie();
    const storedToken = getAccessToken();

    if (storedToken) {
      setIsLogin(true);
      setIsLoading(true);
      return;
    }

    if (!isLogin && !storedToken) {
      setIsLoading(true);
      return;
    }

    // 로그인이 되지 않은 상태이고, 토큰이 없는경우 localStorage에 accessToken을 저장
    const checkLoginStatus = async () => {
      try {
        const response = await client.GET("/api/members/me", {
          credentials: "include",
        });

        if (response.data?.data) {
          saveAccessTokenFromCookie();
          setIsLogin(true);
        } else {
        }
      } catch (_error) {
        console.error("로그인 확인 실패", _error);
      } finally {
        setIsLoading(true);
      }
    };

    checkLoginStatus();
  }, []);

  // `마이페이지` 이동 함수 분리 (클릭 시 최신 userId를 가져와 이동)
  const goToMyPage = (e: React.MouseEvent) => {
    e.preventDefault();
    const userId = getUserIdFromToken();
    if (userId) {
      router.push(`/member/${userId}`);
    }
  };

  // 로딩이 끝났을때 체크
  useEffect(() => {
    if (!isLoading) return;

    if (!isLogin && !isLoginPage) {
      router.push("/member/login");
    }
  }, [isLoading, isLogin, isLoginPage, router]);

  if (!isLoading) {
    return (
      <div className="flex flex-col justify-center items-center h-screen">
        <div className="w-12 h-12 border-4 border-gray-300 border-t-blue-500 rounded-full animate-spin"></div>
        <p className="mt-4 text-gray-500 text-lg animate-pulse">
          잠시만 기다려 주세요...
        </p>
      </div>
    );
  }

  return (
    <>
      {!isLoginPage && (
        <header className="flex justify-end gap-3 px-4">
          <Link href="/">메인</Link>
          {isLogin ? (
            <>
              {/* 토큰에서 가져온 ID를 사용하고, 최신 데이터가 있으면 업데이트 */}
              <Link href="#" onClick={goToMyPage}>
                마이페이지
              </Link>
              <Link href="/member/logout">로그아웃</Link>{" "}
            </>
          ) : (
            <Link href="/member/login">로그인</Link>
          )}
        </header>
      )}
      {children}
    </>
  );
}
