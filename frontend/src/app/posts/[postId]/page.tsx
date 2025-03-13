import { client } from "@/lib/backend/client";
import { cookies } from "next/headers";
import ClientPostDetail from "./ClientPostDetail";

export default async function PostDetailPage({
  params,
}: {
  params: Promise<{ postId: string }>;
}) {
  const { postId } = await params;

  if (!postId) {
    console.error("params.postId가 없음:", postId);
    return <div>잘못된 요청입니다.</div>;
  }

  const numPostId = Number(postId);
  if (isNaN(numPostId)) {
    console.error("postId 변환 실패:", postId);
    return <div>잘못된 게시글 ID입니다.</div>;
  }

  let cookieString = "";
  try {
    const cookieHeader = await cookies();
    cookieString = cookieHeader.toString();
  } catch (error) {
    console.error("쿠키 가져오기 실패:", error);
  }

  let postRes, commentsRes;
  try {
    [postRes, commentsRes] = await Promise.all([
      client.GET("/api/posts/{postId}", {
        params: { path: { postId: numPostId } },
        headers: { cookie: cookieString },
      }),
      client.GET("/api/posts/{postId}/comments", {
        params: { path: { postId: numPostId } },
        headers: { cookie: cookieString },
      }),
    ]);
  } catch (error) {
    console.error("API 요청 실패:", error);
    return <div>데이터를 불러오는 중 오류가 발생했습니다.</div>;
  }

  if (!postRes?.data || !postRes.data.data) {
    console.error("게시글 데이터가 없음:", postRes);
    return <div>게시글을 불러올 수 없습니다.</div>;
  }

  return (
    <ClientPostDetail
      post={postRes.data.data}
      initialComments={commentsRes?.data?.data || []}
    />
  );
}
