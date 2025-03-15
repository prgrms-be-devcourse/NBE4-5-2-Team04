import type { Metadata } from "next";
import "./globals.css";
import ClientLayout from "./ClientLayout";

export const metadata: Metadata = {
  title: "Pathy",
  description: "여행 SNS을 같이 즐겨봐요",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="min-h-[100dvh] flex flex-col">
        <ClientLayout>{children}</ClientLayout>
      </body>
    </html>
  );
}
