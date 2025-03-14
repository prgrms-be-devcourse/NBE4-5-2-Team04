"use client";

import PostContent from "./PostContent";
import PostComments from "./PostComments";
import { components } from "@/lib/backend/schema";

export default function ClientPostDetail({
  post,
  initialComments,
}: {
  post: components["schemas"]["PostDetailResponseDTO"];
  initialComments: components["schemas"]["ListCommentResponseDTO"][];
}) {
  return (
    <div className="max-w-5xl w-full mx-auto py-10 px-6">
      <PostContent post={post} />
      <PostComments postId={post.id!} initialComments={initialComments} />
    </div>
  );
}
