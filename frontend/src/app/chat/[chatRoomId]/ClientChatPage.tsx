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
    chatRoomId: string;
}

const ClientChatPage = ({chatRoomId}: ClientChatPageProps) => {
    const [messages, setMessages] = useState<ChatMessageResponseDTO[]>([]);
    const [message, setMessage] = useState("");
    const [stompClient, setStompClient] = useState<Client | null>(null);
    const scrollRef = useRef<HTMLDivElement | null>(null);

    const scrollToBottom = () => {
        requestAnimationFrame(() => {
            if (scrollRef.current?.children[1]) {
                const scrollableElement = scrollRef.current.children[1] as HTMLDivElement;
                scrollableElement.scrollTop = scrollableElement.scrollHeight;
            }
        });
    };

    const fetchChatMessages = async () => {
        if (!chatRoomId) return;
        try {
            const res = await axios.get(`${process.env.NEXT_PUBLIC_BASE_URL}/api/chat/room/${chatRoomId}`, {
                params: {page: 0},
                withCredentials: true,
            });

            if (res.data) {
                console.log(res.data.data.content);
                setMessages(res.data.data.content);
                scrollToBottom();
            }
        } catch (error) {
            console.error("채팅 메시지 가져오기 실패", error);
        }
    };

    useEffect(() => {
        if (!chatRoomId) return;
        fetchChatMessages();

        const socket = new SockJS(`${process.env.NEXT_PUBLIC_BASE_URL}/ws`);
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log("✅ WebSocket 연결 성공");
                if (!stompClient) {
                    setStompClient(client);
                }
            },
            onDisconnect: () => {
                console.log("❌ WebSocket 연결 해제");
            },
        });

        client.activate();
        return () => {
            client.deactivate();
        };
    }, [chatRoomId]);

    const sendMessage = async () => {
        if (!message.trim() || !stompClient || !stompClient.connected || !chatRoomId) return;

        const chatMessage = {
            chatRoomId,
            content: message,
        };

        try {
            await axios.post(`${process.env.NEXT_PUBLIC_BASE_URL}/api/chat/send`, chatMessage, {
                headers: {"Content-Type": "application/json"},
                withCredentials: true,
            });

            setMessage("");
            scrollToBottom();
        } catch (error) {
            console.error("메시지 전송 실패", error);
        }
    };

// WebSocket 을 통해 메시지를 받을 때 중복 체크
    useEffect(() => {
        if (!stompClient || !stompClient.connected || !chatRoomId) return;

        console.log("📡 Subscribing to chat room:", chatRoomId);

        const subscription = stompClient.subscribe(`/queue/chatroom/${chatRoomId}`, (msg) => {
            try {
                const receivedMessage: ChatMessageResponseDTO = JSON.parse(msg.body);

                // 중복 메시지 체크
                setMessages((prev) => {
                    if (!prev.some((m) => m.id === receivedMessage.id)) {
                        return [...prev, receivedMessage];
                    }
                    return prev;
                });

                scrollToBottom();
            } catch (error) {
                console.error("📩 Message parse error:", error);
            }
        });

        return () => {
            subscription.unsubscribe();
        };
    }, [stompClient, chatRoomId]);

    return (
        <Card className="w-full max-w-2xl mx-auto p-4 shadow-md">
            <ScrollArea ref={scrollRef} className="h-80 border p-2 overflow-y-auto">
                {messages.map((msg) => (
                    <div key={msg.id}
                         className={`flex ${msg.sender.id === me ? "justify-end" : "justify-start"} mb-2 items-start`}>
                        {msg.sender.id !== me && (
                            <Avatar className="mr-2">
                                {msg.sender.profileImageUrl ? (
                                    <AvatarImage
                                        src={`${process.env.NEXT_PUBLIC_BASE_URL}${msg.sender.profileImageUrl}`}
                                        alt={msg.sender.nickname}/>
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
                <Input type="text" value={message} onChange={(e) => setMessage(e.target.value)}
                       onKeyDown={(e) => e.key === "Enter" && sendMessage()} placeholder="메시지 입력..." className="flex-1"
                       aria-label="메시지 입력"/>
                <Button onClick={sendMessage} disabled={!message.trim()} className="ml-2">전송</Button>
            </div>
        </Card>
    );
};

export default ClientChatPage;