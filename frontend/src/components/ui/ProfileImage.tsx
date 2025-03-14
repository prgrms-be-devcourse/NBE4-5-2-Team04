import Image from "next/image";

interface ProfileImageProps
  extends Omit<React.ComponentProps<typeof Image>, "src"> {
  src?: string;
}

export default function ProfileImage({
  src,
  alt,
  width = 120,
  height = 120,
  ...props
}: ProfileImageProps) {
  const defaultUrl = "/default-profile.png";

  const updatedSrc = src?.startsWith("blob:")
    ? src
    : src && src !== defaultUrl
    ? `${process.env.NEXT_PUBLIC_BASE_URL}${src}`
    : defaultUrl;

  return (
    <div
      style={{
        width: `${width}px`,
        height: `${height}px`,
        position: "relative",
      }}
    >
      <Image
        src={updatedSrc}
        alt={alt}
        fill
        sizes={`(max-width: ${width}px) 100vw, ${width}px`}
        style={{
          borderRadius: "50%",
          objectFit: "cover",
        }}
        {...props}
      />
    </div>
  );
}
