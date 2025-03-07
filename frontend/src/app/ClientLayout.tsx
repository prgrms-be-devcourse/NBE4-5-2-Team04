"use client";

import client from "@/lib/backend/client";
import Link from "next/link";
import { usePathname, useSearchParams } from "next/navigation";
import {
  LoginMemberContext,
  useLoginMember,
} from "@/app/stores/auth/loginMemberStore";
import { useRouter } from "next/navigation";
import { useEffect, useRef, useState } from "react";
import { components } from "@/lib/backend/schema";

export default function ClinetLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  type Member = components["schemas"]["MemberDTO"];

  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const [authChecked, setAuthChecked] = useState(false);

  const {
    setLoginMember,
    isLogin,
    loginMember,
    removeLoginMember,
    isLoginMemberPending,
    isAdmin,
    setNoLoginMember,
  } = useLoginMember();

  const loginMemberContextValue = {
    loginMember,
    setLoginMember,
    removeLoginMember,
    isLogin,
    isLoginMemberPending,
    isAdmin,
    setNoLoginMember,
  };

  // 로그인이 필요 없는 경로들
  const isLoginPage = pathname === "/member/login";

  const isFetching = useRef(false);

  useEffect(() => {
    const checkLoginStatus = async () => {
      if (isFetching.current) return;
      isFetching.current = true;

      try {
        const response = await client.GET("/api/members/me", {
          credentials: "include",
        });

        if (response.data?.data) {
          setLoginMember(response.data.data);
        } else {
          setNoLoginMember();
        }
      } catch (_error) {
        setNoLoginMember();
      } finally {
        setAuthChecked(true);
        isFetching.current = false;
      }
    };

    checkLoginStatus();
  }, []);

  // 인증 후 리다이렉트 처리
  useEffect(() => {
    if (authChecked) {
      if (isLogin && isLoginPage) {
        router.replace("/");
      }
    }
  }, [authChecked, isLogin, isLoginPage, router, searchParams]);

  async function handleLogout(e: React.MouseEvent<HTMLAnchorElement>) {
    e.preventDefault();
    try {
      await client
        .DELETE("/api/members/logout", {
          credentials: "include",
        })
        .catch((_err) => console.log("로그아웃 요청 중 에러:", _err));
    } catch (_error) {
      console.log("로그아웃 실패:");
    }

    removeLoginMember();
    router.replace("/member/login");
  }

  return (
    <LoginMemberContext.Provider value={loginMemberContextValue}>
      {/* 로그인 페이지가 아닐 때만 헤더 표시, 임시 뷰입니다.*/}
      {!isLoginPage && (
        <header className="flex justify-end gap-3 px-4">
          <Link href="/">메인</Link>
          <Link href="/about">소개</Link>
          <Link href="/post/list">글 목록</Link>
          {isLogin && <Link href="/post/write">글 작성</Link>}
          {!isLogin && <Link href="/member/login">로그인</Link>}
          {isLogin && (
            <Link href="" onClick={handleLogout}>
              로그아웃
            </Link>
          )}
          {isLogin && <Link href="/member/me">내정보</Link>}
        </header>
      )}
      {children}
    </LoginMemberContext.Provider>
  );
}
