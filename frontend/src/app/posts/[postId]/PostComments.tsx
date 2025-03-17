"use client";

import {useState} from "react";
import {Button} from "@/components/ui/button";
import {components} from "@/lib/backend/schema";
import {client} from "@/lib/backend/client";

export default function PostComments({
                                         postId,
                                         initialComments,
                                     }: {
    postId: number;
    initialComments: components["schemas"]["ListCommentResponseDTO"][];
}) {
    const [comments, setComments] = useState(initialComments);
    const [comment, setComment] = useState("");
    const [replyContent, setReplyContent] = useState<Record<number, string>>({});
    const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
    const [editContent, setEditContent] = useState("");
    const [replyTo, setReplyTo] = useState<number | null>(null);

    const handleCommentSubmit = async (parentId: number | null = null) => {
        const content = parentId ? replyContent[parentId] : comment;
        if (!content?.trim()) return;

        try {
            const res = await client.POST("/api/posts/{postId}/comments", {
                params: {path: {postId}},
                body: {content, parentId: parentId ?? undefined},
                credentials: "include",
            });

            const newComment = res?.data?.data;
            if (!newComment) return;

            setComments((prev) => addComment(prev, newComment, parentId));

            if (parentId) {
                setReplyContent((prev) => ({...prev, [parentId]: ""}));
                setReplyTo(null);
            } else {
                setComment("");
            }
        } catch (error) {
            alert("댓글 작성 중 오류가 발생했습니다.");
        }
    };

    const addComment = (
        comments: components["schemas"]["ListCommentResponseDTO"][],
        newComment: components["schemas"]["ListCommentResponseDTO"],
        parentId: number | null
    ): components["schemas"]["ListCommentResponseDTO"][] => {
        if (!parentId) {
            return [...comments, {...newComment, children: []}];
        }

        return comments.map((c) =>
            c.id === parentId
                ? {
                    ...c,
                    children: [...(c.children ?? []), {...newComment, children: []}],
                }
                : {...c, children: addComment(c.children ?? [], newComment, parentId)}
        );
    };

    const handleDeleteComment = async (commentId: number) => {
        const res = await client.DELETE("/api/comments/{commentId}", {
            params: {path: {commentId}},
            credentials: "include",
        });

        if (res.response.status === 403) {
            alert("댓글 삭제 권한이 없습니다.");
            return;
        }

        if (res?.data?.code !== "200") {
            alert("댓글 삭제에 실패했습니다.");
            return;
        }

        setComments((prev) => removeComment(prev, commentId));
    };

    const removeComment = (
        comments: components["schemas"]["ListCommentResponseDTO"][],
        commentId: number
    ): components["schemas"]["ListCommentResponseDTO"][] => {
        return comments
            .filter((c) => c.id !== commentId)
            .map((c) => ({
                ...c,
                children: removeComment(c.children ?? [], commentId),
            }));
    };

    const handleUpdateComment = async (commentId: number) => {
        if (!editContent.trim()) return;

        const res = await client.PUT("/api/comments/{commentId}", {
            params: {path: {commentId}},
            body: {content: editContent},
            credentials: "include",
        });

        if (res.response.status === 403) {
            alert("댓글 수정 권한이 없습니다.");
            setEditingCommentId(null);
            return;
        }

        const updatedComment = res?.data?.data;
        if (!updatedComment) return;

        setComments((prev) =>
            prev.map((comment) =>
                comment.id === commentId
                    ? {...comment, content: updatedComment.content}
                    : comment
            )
        );

        setEditingCommentId(null);
        setEditContent("");
    };

    return (
        <div className="mt-6">
            <div className="flex space-x-2">
                <input
                    type="text"
                    placeholder="댓글 입력"
                    className="border w-full p-2 rounded-md"
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                />
                <Button onClick={() => handleCommentSubmit()}>작성</Button>
            </div>

            <div className="mt-6">
                {comments.map((comment) => (
                    <div
                        key={comment.id ?? 0}
                        className={`border p-4 rounded-md mt-4 ${
                            comment.parentId ? "ml-6" : ""
                        }`}
                    >
                        <p className="font-semibold mb-2">
                            {comment.nickname ?? "알 수 없음"}
                        </p>

                        {editingCommentId === (comment.id ?? 0) ? (
                            <div className="flex flex-col">
                                <input
                                    type="text"
                                    value={editContent}
                                    onChange={(e) => setEditContent(e.target.value)}
                                    className="border w-full p-2 rounded-md min-h-[36px] transition-all duration-200"
                                />
                                <div className="flex space-x-2 mt-3">
                                    <Button onClick={() => handleUpdateComment(comment.id ?? 0)}>
                                        저장
                                    </Button>
                                    <Button
                                        onClick={() => {
                                            setEditingCommentId(null);
                                            setEditContent("");
                                        }}
                                    >
                                        취소
                                    </Button>
                                </div>
                            </div>
                        ) : (
                            <>
                                <p className="mb-3">{comment.content ?? ""}</p>
                                <div className="flex space-x-2">
                                    <Button
                                        onClick={() => {
                                            setEditingCommentId(comment.id ?? 0);
                                            setEditContent(comment.content ?? "");
                                        }}
                                    >
                                        수정
                                    </Button>
                                    <Button onClick={() => handleDeleteComment(comment.id ?? 0)}>
                                        삭제
                                    </Button>
                                    <Button onClick={() => setReplyTo(comment.id ?? 0)}>
                                        대댓글
                                    </Button>
                                </div>
                            </>
                        )}

                        {replyTo === (comment.id ?? 0) && (
                            <div className="mt-4 flex space-x-2">
                                <input
                                    type="text"
                                    placeholder="대댓글 입력"
                                    className="border w-full p-2 rounded-md"
                                    value={replyContent[comment.id ?? 0] ?? ""}
                                    onChange={(e) =>
                                        setReplyContent((prev) => ({
                                            ...prev,
                                            [comment.id ?? 0]: e.target.value,
                                        }))
                                    }
                                />
                                <Button onClick={() => handleCommentSubmit(comment.id ?? 0)}>
                                    작성
                                </Button>
                                <Button onClick={() => setReplyTo(null)}>취소</Button>
                            </div>
                        )}

                        {(comment.children ?? []).length > 0 && (
                            <div className="ml-2 border-l-1 border-gray-600 pl-4 mt-4">
                                {(comment.children ?? []).map((child) => (
                                    <div key={child.id} className="mb-4 pb-2">
                                        <p className="font-semibold text-sm mb-1">
                                            {child.nickname}
                                        </p>
                                        <p className="text-sm">{child.content}</p>
                                        <div className="flex space-x-2 mt-3">
                                            <Button
                                                onClick={() => handleUpdateComment(child.id ?? 0)}
                                            >
                                                수정
                                            </Button>
                                            <Button
                                                onClick={() => handleDeleteComment(child.id ?? 0)}
                                            >
                                                삭제
                                            </Button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}
