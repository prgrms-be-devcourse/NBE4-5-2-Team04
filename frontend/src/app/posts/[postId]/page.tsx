import {client} from "@/lib/backend/client";
import {cookies} from "next/headers";
import ClientPostDetail from "@/app/posts/[postId]/ClientPostDetail";


export default async function PostDetailPage({params}: {
    params: { postId: number };
}) {
    const {postId} = await params;

    // API 호출 (백엔드에서 SSR 데이터를 가져옴)
    const res = await client.GET("/api/posts/{postId}", {
        params: {
            path: {
                postId,
            },
        },
        headers: {
            cookie: (await cookies()).toString(),
        },
    });
    if (!res) {
        return {notFound: true};
    }
    const post = res.data!.data!;

    return <ClientPostDetail post={post}/>;
}

