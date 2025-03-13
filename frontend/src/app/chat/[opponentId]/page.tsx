import ClientChatPage from "@/app/chat/[opponentId]/ClientChatPage";


export default async function ChatPage({params}: {
    params: { opponentId: number };
}) {
    const {opponentId} = await params;

    return <ClientChatPage opponentId={opponentId}/>;
}