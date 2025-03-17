"use client";

import PostContent from "./PostContent";
import PostComments from "./PostComments";
import {components} from "@/lib/backend/schema";
import {Button} from "@/components/ui/button";
import {getUserIdFromToken} from "@/app/utils/auth";
import Link from "next/link";
import {client} from "@/lib/backend/client";
import {useRouter} from "next/navigation";

export default function ClientPostDetail({
                                             post,
                                             initialComments,
                                         }: {
    post: components["schemas"]["PostDetailResponseDTO"];
    initialComments: components["schemas"]["ListCommentResponseDTO"][];
}) {
    const myId = getUserIdFromToken();
    const router = useRouter();
    const handleDelete = async () => {
        if (confirm("삭제하겠습니까?")) {
            const res = await client.DELETE("/api/posts/{postId}", {
                params: {path: {postId: post.id!}},
                credentials: "include",
            })

            if (res.data!.code === "200") {
                router.push("/");
            } else {
                alert(res.data!.msg);
            }
        }
    }

    return (
        <div className="max-w-5xl w-full mx-auto py-10 px-6">
            {myId === post.authorDTO!.memberId! && <div className={"flex gap-3"}>
                <Link href={"/posts/edit/" + post.id}>
                    <Button variant={"secondary"}>수정</Button>
                </Link>
                <Button onClick={handleDelete} variant={"destructive"}>삭제</Button>
            </div>}
            <PostContent post={post}/>
            <PostComments postId={post.id!} initialComments={initialComments}/>
        </div>
    );
}
