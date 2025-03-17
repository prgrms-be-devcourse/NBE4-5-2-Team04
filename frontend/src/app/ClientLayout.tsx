"use client";

import { client } from "@/lib/backend/client";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import {
  getAccessToken,
  getUserIdFromToken,
  saveAccessTokenFromCookie,
} from "@/app/utils/auth";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Image from "next/image";

export default function ClientLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  const router = useRouter();
  const pathname = usePathname();
  const [isLogin, setIsLogin] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [userId, setUserId] = useState<string | null>(null); // 추가

  const isLoginPage = pathname === "/member/login";
  const queryClient = new QueryClient();

  useEffect(() => {
    if (typeof window === "undefined") return;

    saveAccessTokenFromCookie();
    const storedToken = getAccessToken();

    if (storedToken) {
      setIsLogin(true);
      setIsLoading(true);
      setUserId(getUserIdFromToken()); // 사용자 ID 설정
      return;
    }

    if (!isLogin && !storedToken) {
      setIsLoading(true);
      return;
    }

    const checkLoginStatus = async () => {
      try {
        const response = await client.GET("/api/members/me", {
          credentials: "include",
        });
        if (response.data?.data) {
          saveAccessTokenFromCookie();
          setIsLogin(true);
          setUserId(getUserIdFromToken()); // 사용자 ID 설정
        }
      } catch (_error) {
        console.error("로그인 확인 실패", _error);
      } finally {
        setIsLoading(true);
      }
    };

    checkLoginStatus();
  }, []);

  const goToMyPage = (e: React.MouseEvent) => {
    e.preventDefault();
    if (userId) {
      router.push(`/member/${userId}`);
    }
  };

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

  if (isLoginPage) {
    return (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    );
  }

  return (
    <QueryClientProvider client={queryClient}>
      <div className="flex flex-col">
        {/* 헤더 (상단 고정) */}
        <header className="bg-gray-300 flex justify-between items-center px-4 h-16 border-b border-gray-400 w-full fixed top-0 left-0 z-50">
          {/* 로고 */}
          <Link href="/" className="flex items-center">
            <Image
              src="/Logo.png"
              alt="Logo"
              width={100}
              height={30}
              priority
            />
          </Link>

          {/* 로그인 상태 */}
          <div>
            {isLogin ? (
              <>
                <Link href="#" onClick={goToMyPage} className="mr-3">
                  마이페이지
                </Link>
                <Link href="/member/logout">로그아웃</Link>
              </>
            ) : (
              <Link href="/member/login">로그인</Link>
            )}
          </div>
        </header>

        <div className="flex">
          {/* 사이드바 (왼쪽 고정) */}
          <aside className="bg-gray-300 w-48 h-[calc(100vh-64px)] flex flex-col border-r border-gray-400 fixed top-16 left-0 z-40">
            <nav className="flex flex-col space-y-4 p-4">
              <Link
                href="/rankings/places"
                className="block hover:bg-gray-400 p-2 rounded"
              >
                인기 장소
              </Link>
              <Link
                href="/rankings/regions"
                className="block hover:bg-gray-400 p-2 rounded"
              >
                인기 지역
              </Link>
              <Link
                href="/places/map"
                className="block hover:bg-gray-400 p-2 rounded"
              >
                지도 검색
              </Link>
              <Link
                href={userId ? `/member/follow/${userId}` : "/member/follow"}
                className="block hover:bg-gray-400 p-2 rounded"
              >
                팔로잉/팔로우
              </Link>
              <Link
                href="/posts/liked"
                className="block hover:bg-gray-400 p-2 rounded"
              >
                좋아요
              </Link>
              <Link
                href="/posts/scrapped"
                className="block hover:bg-gray-400 p-2 rounded"
              >
                스크랩
              </Link>
              <Link
                href="/posts/following"
                className="block hover:bg-gray-400 p-2 rounded"
              >
                팔로잉 게시물
              </Link>
              <Link
                href="/posts/liked"
                className="block hover:bg-gray-400 p-2 rounded"
              >
                장소 게시물
              </Link>
              <Link
                href="/chat/rooms"
                className="block hover:bg-gray-400 p-2 rounded"
              >
                채팅
              </Link>
            </nav>
          </aside>

          {/* 메인 레이아웃 */}
          <main className="flex-1 bg-white p-4 pt-16 ml-48">{children}</main>
        </div>
      </div>
    </QueryClientProvider>
  );
}
