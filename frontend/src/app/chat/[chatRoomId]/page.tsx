import ClientChatPage from "@/app/chat/[chatRoomId]/ClientChatPage";


export default async function ChatPage({params}: {
    params: { chatRoomId: string };
}) {
    const {chatRoomId} = await params;

    return <ClientChatPage chatRoomId={chatRoomId}/>;
}