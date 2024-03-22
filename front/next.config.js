/** @type {import('next').NextConfig} */
const {createVanillaExtractPlugin} = require('@vanilla-extract/next-plugin');
const path = require("path");
const withVanillaExtract = createVanillaExtractPlugin();

const nextConfig = {
    reactStrictMode: true,
    swcMinify: true,
    async rewrites() {
        return [
            {
                source: "/api/:path*",
                destination: "https://marrymo.site/api/:path*",
            },
            {
                source: "/:path*",
                destination: "https://openapi.naver.com/:path*",
            }
        ];
    },
    images: {
        domains: [
          'marrymo-bucket.s3.amazonaws.com',
          'shopping-phinf.pstatic.net' // 여기에 추가
        ], // 여기에 도메인 추가

    },
    webpack: (config, {isServer}) => {
        config.resolve.alias['@'] = path.join(__dirname, 'src');
        config.resolve.alias['#'] = path.join(__dirname, 'public');
        config.module.rules.push({
            test: /\.svg$/,
            use: ['@svgr/webpack'],
        });

        return config;
    },
}

module.exports = withVanillaExtract(nextConfig);
