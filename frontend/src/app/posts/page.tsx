import ClientPostList from "./ClientSearchablePostList";

export default function PostListPage() {
    return <ClientPostList queryKey={"posts"} apiEndpoint={"/api/posts"}/>
}