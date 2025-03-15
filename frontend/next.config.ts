import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: "http",
        hostname: "localhost",
        port: "8080",
        pathname: "/uploads/**",
      },
      {
        protocol: "http",
        hostname: "pahtymytravelpathy.duckdns.org",
        port: "8080",
        pathname: "/uploads/**",
      },
    ],
  },
  typescript: {
    ignoreBuildErrors: true, // TypeScript 오류가 있어도 빌드 강행
  },
};

export default nextConfig;
