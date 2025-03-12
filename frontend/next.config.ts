import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: "http",
        hostname: "localhost",
        port: "8080",
        pathname: "/profiles/**",
      },
    ],
    domains: ["localhost"],
  },
};

export default nextConfig;
