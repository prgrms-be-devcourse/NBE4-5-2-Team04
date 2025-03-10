"use client";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { removeAccessToken } from "@/app/utils/auth";
import client from "@/lib/backend/client";

export default function LogoutPage() {
  const router = useRouter();

  useEffect(() => {
    async function logout() {
      try {
        await client.DELETE("/api/members/logout", { credentials: "include" });
      } catch (error) {
        console.error("로그아웃 실패:", error);
      }
      removeAccessToken();
      localStorage.clear();
      router.replace("/member/login");
    }

    logout();
  }, [router]);

  return (
    <div className="flex flex-col justify-center items-center h-screen">
      <div className="w-12 h-12 border-4 border-gray-300 border-t-blue-500 rounded-full animate-spin"></div>
      <p className="mt-4 text-gray-500 text-lg animate-pulse">
        잠시만 기다려 주세요...
      </p>
    </div>
  );
}
