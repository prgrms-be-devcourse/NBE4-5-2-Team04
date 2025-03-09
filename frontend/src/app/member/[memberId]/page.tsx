import {client} from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { cookies } from "next/headers";

export default async function Page({
  params,
}: {
  params: {
    memberId: number;
  };
}) {
  const { memberId } = await params;

  const response = await client.GET("/api/members/{memberId}", {
    params: {
      path: { memberId },
    },
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  if (response.error) {
    return <div>{response.error.msg}</div>;
  }

  const rsData = response.data;

  const profileData = rsData.data;

  return <ClientPage profileData={profileData} memberId={memberId} />;
}
