"use client";

import { client } from "@/lib/backend/client";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState, useRef } from "react";
import {
  getAccessToken,
  getUserIdFromToken,
  saveAccessTokenFromCookie,
} from "@/app/utils/auth";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Image from "next/image";
import {
  Menu,
  X,
  User,
  Map,
  Heart,
  Bookmark,
  MessageSquare,
  LogOut,
  ChevronDown,
} from "lucide-react";

export default function ClientLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  const router = useRouter();
  const pathname = usePathname();
  const [isLogin, setIsLogin] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [userId, setUserId] = useState<string | null>(null);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);
  const profileDropdownRef = useRef<HTMLDivElement>(null);

  const isLoginPage = pathname === "/member/login";
  const queryClient = new QueryClient();

  // 현재 경로가 네비게이션 항목과 일치하는지 확인
  const isActive = (path: string) => {
    return pathname.startsWith(path);
  };

  useEffect(() => {
    if (typeof window === "undefined") return;

    saveAccessTokenFromCookie();
    const storedToken = getAccessToken();

    if (storedToken) {
      setIsLogin(true);
      setIsLoading(true);
      setUserId(getUserIdFromToken());
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
          setUserId(getUserIdFromToken());
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
    setProfileDropdownOpen(false);
  };

  useEffect(() => {
    if (!isLoading) return;
    if (!isLogin && !isLoginPage) {
      router.push("/member/login");
    }
  }, [isLoading, isLogin, isLoginPage, router]);

  // 경로 변경 시 모바일 메뉴와 프로필 드롭다운 닫기
  useEffect(() => {
    setMobileMenuOpen(false);
    setProfileDropdownOpen(false);
  }, [pathname]);

  // 외부 클릭 시 드롭다운 메뉴 닫기를 위한 개선된 핸들러
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        profileDropdownRef.current &&
        !profileDropdownRef.current.contains(e.target as Node)
      ) {
        setProfileDropdownOpen(false);
      }
    };

    // 이벤트 리스너 추가
    if (profileDropdownOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    // 클린업 함수
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [profileDropdownOpen]);

  if (!isLoading) {
    return (
      <div className="flex flex-col justify-center items-center h-screen bg-gray-50">
        <div className="w-12 h-12 border-4 border-gray-200 border-t-blue-500 rounded-full animate-spin"></div>
        <p className="mt-4 text-gray-600 font-medium text-lg animate-pulse">
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

  const navItems = [
    {
      href: "/rankings/places",
      label: "인기 장소",
      icon: <User className="w-6 h-6" />,
    },
    {
      href: "/rankings/regions",
      label: "인기 지역",
      icon: <Map className="w-6 h-6" />,
    },
    {
      href: "/places/map",
      label: "지도 검색",
      icon: <Map className="w-6 h-6" />,
    },
    {
      href: userId ? `/member/follow/${userId}` : "/member/follow",
      label: "팔로잉/팔로우",
      icon: <User className="w-6 h-6" />,
    },
    {
      href: "/posts/liked",
      label: "좋아요",
      icon: <Heart className="w-6 h-6" />,
    },
    {
      href: "/posts/scrapped",
      label: "스크랩",
      icon: <Bookmark className="w-6 h-6" />,
    },
    {
      href: "/posts/following",
      label: "팔로잉 게시물",
      icon: <MessageSquare className="w-6 h-6" />,
    },
    {
      href: "/chat/rooms",
      label: "채팅",
      icon: <MessageSquare className="w-6 h-6" />,
    },
  ];

  return (
    <QueryClientProvider client={queryClient}>
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-200">
        {/* 헤더 */}
        <header className="bg-white shadow-sm flex justify-between items-center px-4 h-14 fixed top-0 left-0 w-full z-50">
          <div className="flex items-center">
            {/* 모바일 메뉴 버튼 */}
            <button
              onClick={(e) => {
                e.stopPropagation();
                setMobileMenuOpen(!mobileMenuOpen);
              }}
              className="mr-2 text-gray-600 lg:hidden focus:outline-none"
            >
              <Menu className="w-6 h-6" />
            </button>

            {/* 로고 */}
            <Link href="/" className="flex items-center">
              <Image
                src="/Logo.png"
                alt="Logo"
                width={100}
                height={30}
                priority
                className="h-7 w-auto"
              />
            </Link>
          </div>

          {/* 사용자 메뉴 */}
          <div className="relative" ref={profileDropdownRef}>
            {isLogin ? (
              <div>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    setProfileDropdownOpen(!profileDropdownOpen);
                  }}
                  className="flex items-center space-x-1 text-gray-700 hover:text-blue-600 focus:outline-none"
                >
                  <span className="hidden md:inline">내 프로필</span>
                  <ChevronDown className="w-4 h-4" />
                </button>

                {/* 드롭다운 */}
                {profileDropdownOpen && (
                  <div className="absolute right-0 mt-2 w-48 py-2 bg-white rounded-md shadow-lg z-10 border border-gray-100">
                    <Link
                      href="#"
                      onClick={goToMyPage}
                      className="block px-4 py-2 text-gray-700 hover:bg-blue-50 hover:text-blue-600"
                    >
                      <div className="flex items-center">
                        <User className="w-4 h-4 mr-2" />
                        마이페이지
                      </div>
                    </Link>
                    <Link
                      href="/member/logout"
                      className="block px-4 py-2 text-gray-700 hover:bg-blue-50 hover:text-blue-600"
                    >
                      <div className="flex items-center">
                        <LogOut className="w-4 h-4 mr-2" />
                        로그아웃
                      </div>
                    </Link>
                  </div>
                )}
              </div>
            ) : (
              <Link
                href="/member/login"
                className="text-blue-600 hover:text-blue-800 font-medium"
              >
                로그인
              </Link>
            )}
          </div>
        </header>

        {/* 모바일 사이드바 오버레이 */}
        {mobileMenuOpen && (
          <div
            className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
            onClick={() => setMobileMenuOpen(false)}
          />
        )}

        <div className="flex flex-1 pt-14">
          {/* 사이드바 */}
          <aside
            // 수정된 클래스
            className={`fixed top-14 bottom-0 left-0 transform ${
              mobileMenuOpen ? "translate-x-0" : "-translate-x-full"
            } lg:translate-x-0 w-48 bg-white border-r border-gray-200 transition-transform duration-300 ease-in-out lg:static lg:h-auto z-30`}
          >
            <div className="lg:hidden absolute top-4 right-4">
              <button
                onClick={() => setMobileMenuOpen(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            <nav className="flex flex-col space-y-2">
              {navItems.map((item) => (
                <Link
                  key={item.href}
                  href={item.href}
                  className="flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors hover:bg-gray-100"
                >
                  {item.icon}
                  <span className="text-gray-700">{item.label}</span>
                </Link>
              ))}
            </nav>
          </aside>

          {/* 메인 컨텐츠 */}
          <main className="flex-1 p-3 pb-16">{children}</main>
        </div>
      </div>
    </QueryClientProvider>
  );
}
