import Image from "next/image";

interface ProfileImageProps
  extends Omit<React.ComponentProps<typeof Image>, "src"> {
  src?: string;
}

export default function ProfileImage({
  src,
  alt,
  ...props
}: ProfileImageProps) {
  // 🔥 blob: URL일 경우에는 ?timestamp 추가 안 함 (미리보기 때문)
  const updatedSrc = src?.startsWith("blob:") ? src : `${src}?t=${Date.now()}`;

  return <Image src={updatedSrc} alt={alt || "프로필 이미지"} {...props} />;
}
