"use client";

import {useEffect, useRef, useState} from "react";
import axios from "axios";
import SockJS from "sockjs-client";
import {Client} from "@stomp/stompjs";
import {getUserIdFromToken} from "@/app/utils/auth";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Card} from "@/components/ui/card";
import {ScrollArea} from "@/components/ui/scroll-area";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";

interface MemberDTO {
    id: number;
    nickname: string;
    profileImageUrl?: string;
}

interface ChatMessageResponseDTO {
    id: number;
    sender: MemberDTO;
    content: string;
    createdAt: string;
}

const me = getUserIdFromToken();

interface ClientChatPageProps {
    opponentId?: number;
}

const ClientChatPage = ({opponentId}: ClientChatPageProps) => {
    const [messages, setMessages] = useState<ChatMessageResponseDTO[]>([]);
    const [message, setMessage] = useState("");
    const [stompClient, setStompClient] = useState<Client | null>(null);
    const [chatRoomId, setChatRoomId] = useState<number | null>(null);
    const [roomMembers, setRoomMembers] = useState<Set<MemberDTO> | null>(null);
    const scrollRef = useRef<HTMLDivElement | null>(null);

    // ✅ 스크롤을 가장 아래로 이동하는 함수
    const scrollToBottom = () => {
        console.log(scrollRef.current?.children[1]);
        if (scrollRef.current?.children[1]) {
            const scrollableElement = scrollRef.current?.children[1] as HTMLDivElement;
            requestAnimationFrame(() => {
                scrollableElement.scrollTop = scrollableElement.scrollHeight;
            });
        }
    };

    // 채팅방 조회 또는 생성 후 메시지와 함께 불러오기
    const fetchChatRoom = async () => {
        try {
            const res = await axios.get(`http://localhost:8080/api/chat/room/${opponentId}`, {
                withCredentials: true,
            });

            console.log(res.data);
            if (res.data.data) {
                setChatRoomId(res.data.data.id);
                setRoomMembers(res.data.data.members);
                setMessages(
                    res.data.data.messages
                        .map((msg: ChatMessageResponseDTO) => ({
                            ...msg,
                            sender: {
                                id: msg.sender.id,
                                nickname: msg.sender.nickname,
                                profileImageUrl: msg.sender.profileImageUrl,
                            },
                        }))
                );
            }
        } catch (error) {
            console.error("채팅방 가져오기 실패", error);
        }
    };

    useEffect(() => {
        if (opponentId) {
            fetchChatRoom();
        }

        const socket = new SockJS("http://localhost:8080/ws");
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log("✅ WebSocket 연결 성공");
                setStompClient(client);
            },
            onDisconnect: () => {
                console.log("❌ WebSocket 연결 해제");
            },
        });

        client.activate();
        setStompClient(client);
        return () => {
            client.deactivate();
        };
    }, [opponentId]);

    // ✅ WebSocket 구독에서 받아온 메시지를 정확하게 반영
    useEffect(() => {
        if (!stompClient || !stompClient.connected || !chatRoomId) return;

        console.log("📡 Subscribing to chat room:", chatRoomId);

        const subscription = stompClient.subscribe(`/queue/chatroom/${chatRoomId}`, (msg) => {
            try {
                const receivedMessage: ChatMessageResponseDTO = JSON.parse(msg.body);

                setMessages((prev) => {
                    if (prev.some((m) => m.id === receivedMessage.id)) return prev;
                    return [...prev, receivedMessage].sort(
                        (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
                    );
                });

                setTimeout(scrollToBottom, 100);
            } catch (error) {
                console.error("📩 Message parse error:", error);
            }
        });

        return () => {
            console.log("🛑 Unsubscribing from chat room:", chatRoomId);
            subscription.unsubscribe();
        };
    }, [stompClient, chatRoomId]);

    // ✅ 메시지 전송 (콘솔 로그 추가)
    const sendMessage = async () => {
        if (!message.trim() || !stompClient || !stompClient.connected || !chatRoomId || !opponentId) return;

        const chatMessage = {
            chatRoomId,
            content: message,
        };

        try {
            const res = await axios.post(
                "http://localhost:8080/api/chat/send",
                chatMessage,
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                    withCredentials: true,
                }
            );

            console.log("📤 Sent message:", res.data.data);
            setMessage("");
            setTimeout(scrollToBottom, 100);
        } catch (error) {
            console.error("메시지 전송 실패", error);
        }
    };

    // ✅ messages 상태가 변경될 때마다 자동 스크롤 적용
    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    return (
        <Card className="w-full max-w-2xl mx-auto p-4 shadow-md">
            {roomMembers && (
                <h1 className="text-lg font-bold mb-4 text-center">
                    {Array.from(roomMembers)
                        .filter(member => member.id !== me)
                        .map(member => member.nickname)
                        .join(", ")}
                </h1>
            )}
            <ScrollArea ref={scrollRef} className="h-80 border p-2 overflow-y-auto">
                {messages.map((msg) => (
                    <div key={msg.id}
                         className={`flex ${msg.sender.id === me ? "justify-end" : "justify-start"} mb-2 items-start`}>
                        {msg.sender.id !== me && (
                            <Avatar className="mr-2">
                                {msg.sender.profileImageUrl ? (
                                    <AvatarImage src={msg.sender.profileImageUrl} alt={msg.sender.nickname}/>
                                ) : (
                                    <AvatarFallback>{msg.sender.nickname[0]}</AvatarFallback>
                                )}
                            </Avatar>
                        )}
                        <div
                            className={`p-2 rounded-lg max-w-xs ${msg.sender.id === me ? "bg-blue-500 dark:bg-blue-700 " : "bg-gray-200 dark:bg-gray-800 "}`}>
                            <span>{msg.content}</span>
                            <span className="text-xs block text-gray-500 dark:text-gray-400">
                                {new Date(msg.createdAt).toLocaleTimeString()}
                            </span>
                        </div>
                    </div>
                ))}
            </ScrollArea>
            <div className="flex items-center mt-4">
                <Input
                    type="text"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === "Enter") {
                            e.preventDefault();
                            sendMessage();
                        }
                    }}
                    placeholder="메시지 입력..."
                    className="flex-1"
                    aria-label="메시지 입력"
                />
                <Button onClick={sendMessage} disabled={!message.trim()} className="ml-2">전송</Button>
            </div>
        </Card>
    );
};

export default ClientChatPage;