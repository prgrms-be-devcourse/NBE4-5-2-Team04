import PostList from "@/components/posts/results/ClientPostList";
import {client} from "@/lib/backend/client";

export default function LikedPostsPage() {
    const fetchLikedPosts = async ({ pageParam = 0 }) => {
        const { data, error } = await client.GET("/api/posts/liked", {
            params: {
                query: {
                    pageable:{
                        page:pageParam,
                        size:5,
                    }
                },
            },
        });
        if (error) {
            throw new Error("좋아요한 글을 불러오는 데 실패했습니다.");
        }

        return data;
    };

    // @ts-ignore
    return <PostList queryKey="likedPosts" fetchFunction={fetchLikedPosts} />;
}