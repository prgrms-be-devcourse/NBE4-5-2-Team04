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

interface MemberDTO {
    id: number;
    nickname: string;
    profileImageUrl?: string;
}

interface ChatMessageResponseDTO {
    id: number;
    sender: MemberDTO;
    content: string;
    isRead: boolean;
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
    const scrollRef = useRef<HTMLDivElement | null>(null);

    // ✅ 스크롤을 가장 아래로 이동하는 함수
    const scrollToBottom = () => {
        if (scrollRef.current) {
            requestAnimationFrame(() => {
                scrollRef.current!.scrollTop = scrollRef.current!.scrollHeight;
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
                const roomId = res.data.data.id;
                setChatRoomId(roomId);
                setMessages(res.data.data.messages.map((msg: ChatMessageResponseDTO) => ({
                    ...msg,
                    sender: {
                        id: msg.sender.id,
                        nickname: msg.sender.nickname,
                        profileImageUrl: msg.sender.profileImageUrl,
                    },
                })));
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

        console.log("📡 WebSocket 구독 시작:", chatRoomId);

        const subscription = stompClient.subscribe(`/queue/chatroom/${chatRoomId}`, (msg) => {
            try {
                const receivedMessage: ChatMessageResponseDTO = JSON.parse(msg.body);
                console.log("📩 받은 메시지:", receivedMessage);
                setMessages((prev) => [...prev, receivedMessage]);
                setTimeout(scrollToBottom, 100);
            } catch (error) {
                console.error("📩 메시지 파싱 오류:", error);
            }
        });

        return () => {
            console.log("🛑 WebSocket 구독 취소:", chatRoomId);
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

            console.log("📤 보낸 메시지:", res.data.data); // ✅ 메시지 전송 로그 추가

            stompClient.publish({
                destination: `/app/chat/${chatRoomId}`,
                body: JSON.stringify(res.data.data),
            });

            setMessages((prev) => {
                const updatedMessages = [...prev, res.data.data];
                return updatedMessages;
            });

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
            <ScrollArea ref={scrollRef} className="h-80 border p-2 overflow-y-auto">
                {messages.map((msg, index) => (
                    <div key={index}
                         className={`flex ${msg.sender.id === me ? "justify-end" : "justify-start"} mb-2`}>
                        <div
                            className={`p-2 rounded-lg max-w-xs ${msg.sender.id === me ? "bg-blue-500 dark:bg-blue-700 " : "bg-gray-200 dark:bg-gray-800 "}`}>
                            <strong>{msg.sender.id === me ? "나" : msg.sender.nickname}:</strong> {msg.content}
                            <span
                                className="text-xs block text-gray-500 dark:text-gray-400">{new Date(msg.createdAt).toLocaleTimeString()}</span>
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