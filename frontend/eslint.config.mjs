import { dirname } from "path";
import { fileURLToPath } from "url";
import { FlatCompat } from "@eslint/eslintrc";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const compat = new FlatCompat({
  baseDirectory: __dirname,
});

const eslintConfig = [
  ...compat.extends("next/core-web-vitals", "next/typescript"),
  {
    rules: {
      "@typescript-eslint/no-unused-vars": "warn", // 사용되지 않은 변수 → 경고
      "react-hooks/exhaustive-deps": "warn", // useEffect 의존성 검사 → 경고
      "@typescript-eslint/no-explicit-any": "warn", // any 타입 사용 → 경고
      "prefer-const": "warn", // let → const 강제 규칙 완화
    },
  },
];

export default eslintConfig;
