import PostList from "@/components/posts/results/ClientPostList";

export default async function LikedPostsPage({params,}: {
    params: { memberId: string };
}) {
    const {memberId} = await params;
    return <PostList queryKey="likedPosts" memberId={memberId} apiEndpoint="/api/posts/member/{memberId}"/>;
}
