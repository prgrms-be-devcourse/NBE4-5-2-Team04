import { client } from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { cookies } from "next/headers";
import { components } from "@/lib/backend/schema";

export default async function Page({
  params,
}: {
  params: {
    memberId: number;
  };
}) {
  const pageable: components["schemas"]["Pageable"] = {
    page: 1,
    size: 10,
    sort: ["createdAt,desc"],
  };

  const { memberId } = await params;

  const responseMember = await client.GET("/api/members/{memberId}", {
    params: {
      path: { memberId },
    },
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  // const responsePost = await client.GET("/api/posts/member/{memberId}", {
  //   params: {
  //     path: { memberId },
  //   },
  //   headers: {
  //     cookie: (await cookies()).toString(),
  //   },
  // });

  if (responseMember.error) {
    return <div>{responseMember.error.msg}</div>;
  }

  // if (responsePost.error) {
  //   return <div>{responsePost.error.msg}</div>;
  // }

  const rsDataMember = responseMember.data;
  // const rsDataPost = responsePost.data;

  const profileData = rsDataMember.data;
  // const postData = rsDataPost?.data;

  return (
    <ClientPage
      profileData={{
        ...profileData,
      }}
      // postData={{
      //   ...postData,
      // }}
      memberId={memberId}
    />
  );
}
