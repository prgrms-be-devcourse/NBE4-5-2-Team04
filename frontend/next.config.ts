import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  images: {
    remotePatterns: [
      {
        protocol: "http",
        hostname: "localhost",
        port: "8080", //
        pathname: "/uploads/**",
      },
      {
        protocol: "http",
        hostname: "pahtymytravelpathy.duckdns.org",
        port: "8080", //
        pathname: "/uploads/**",
      },
    ],
  },
};

export default nextConfig;
