import pluginJs from "@eslint/js";
import pluginReactConfig from "eslint-plugin-react/configs/recommended.js";

export default [
  {
    files: ["**/*.js"],
    languageOptions: { 
        sourceType: "commonjs" 
    },
    env: {
      browser: true,
      node: true
    }
  },
  pluginJs.configs.recommended,
  pluginReactConfig,
];
