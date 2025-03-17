import {client} from "@/lib/backend/client";
import {cookies} from "next/headers";
import {redirect} from "next/navigation";

export default async function FindChatRoomId({params}: {
    params: { opponentId: number };
}) {
    const {opponentId} = await params;


    const res = await client.GET(`/api/chat/rooms/find/{opponentId}`, {
        headers: {
            cookie: (await cookies()).toString(),
        },
        params: {path: {opponentId}},
    });
    const chatRoomId = res.data!.data;
    redirect(`/chat/${chatRoomId}`);
}