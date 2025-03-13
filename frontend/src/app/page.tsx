import Image from "next/image";
import { redirect } from "next/navigation";

export default function Home() {
  redirect("/posts"); // 자동으로 /posts로 이동
  return null; // 불필요한 렌더링 방지
}
