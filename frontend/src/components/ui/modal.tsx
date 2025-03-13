"use client";

import { ReactNode } from "react";
import { X } from "lucide-react";

interface ModalProps {
  children: ReactNode;
  onClose: () => void;
  title?: string;
}

export function Modal({ children, onClose, title }: ModalProps) {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
      <div className="bg-white rounded-lg shadow-lg w-96 p-4 relative">
        {/* 닫기 버튼 */}
        <button className="absolute top-2 right-2 text-gray-500 hover:text-black" onClick={onClose}>
          <X size={20} />
        </button>

        {/* 제목 */}
        {title && <h2 className="text-lg font-semibold text-center">{title}</h2>}

        {/* 본문 내용 */}
        <div className="mt-4">{children}</div>
      </div>
    </div>
  );
}