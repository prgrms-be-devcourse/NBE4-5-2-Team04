import PostList from "@/components/posts/results/ClientPostList";

export default function LikedPostsPage() {
    return <PostList queryKey="likedPosts" apiEndpoint="/api/posts/following"/>;
}
