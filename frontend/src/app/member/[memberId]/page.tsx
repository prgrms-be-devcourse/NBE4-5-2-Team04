import { useState, useEffect } from "react";
import { client } from "@/lib/backend/client";
import PageContent from "@/app/member/[memberId]/PageContent";
import { cookies } from "next/headers";
import { components } from "@/lib/backend/schema";
import PostList from "@/components/posts/results/ClientPostList";

export default async function Page({
  params,
}: {
  params: {
    memberId: number;
  };
}) {
  const { memberId } = params;

  // 초기 프로필 데이터 불러오기
  const responseMember = await client.GET("/api/members/{memberId}", {
    params: {
      path: { memberId },
    },
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  if (responseMember.error) {
    return <div>{responseMember.error.msg}</div>;
  }

  const initialProfileData = responseMember.data.data;

  return (
    <PageContent initialProfileData={initialProfileData} memberId={memberId} />
  );
}
