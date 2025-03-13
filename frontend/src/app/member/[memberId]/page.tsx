import { client } from "@/lib/backend/client";
import PageContent from "@/app/member/[memberId]/PageContent";
import { cookies } from "next/headers";

export default async function Page({
  params,
}: {
  params: Promise<{ memberId: string }>; // 비동기적으로 가져오기
}) {
  const resolvedParams = await params; // `params`를 `await`하여 동기적으로 처리
  const memberId = parseInt(resolvedParams.memberId, 10);

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
