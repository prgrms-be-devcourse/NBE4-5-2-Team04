import {client} from "@/lib/backend/client";
import {cookies} from "next/headers";
import Link from "next/link";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";

export default async function ChatPage() {
    const res = await client.GET(`/api/chat/rooms`, {
        headers: {
            cookie: (await cookies()).toString(),
        },
    });
    const rooms = res.data!.data;

    return (
        <div className="bg-muted h-screen overflow-hidden w-1/3">
            <div className="flex flex-col gap-3 max-h-[80vh] overflow-y-auto p-4 rounded-lg shadow-md">
                {rooms.length === 0 ? (
                    <p className="text-center text-gray-500">채팅 없음</p>
                ) : (
                    rooms.map(room => (
                        <div key={room.id} className="p-3 flex items-center gap-3">
                            <Avatar className="mr-2">
                                {room.opponent.profileImageUrl ? (
                                    <AvatarImage
                                        src={`${process.env.NEXT_PUBLIC_BASE_URL}${room.opponent.profileImageUrl}`}
                                        alt={room.opponent.nickname}
                                    />
                                ) : (
                                    <AvatarFallback>{room.opponent.nickname[0]}</AvatarFallback>
                                )}
                            </Avatar>
                            <Link href={`/chat/${room.id}`}
                                  className="text-lg font-medium text-primary hover:underline">
                                {room.opponent.nickname}
                            </Link>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}