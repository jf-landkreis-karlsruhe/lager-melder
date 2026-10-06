import pluginVue from 'eslint-plugin-vue'
import { defineConfigWithVueTs, vueTsConfigs } from '@vue/eslint-config-typescript'
import skipFormatting from '@vue/eslint-config-prettier/skip-formatting'

export default defineConfigWithVueTs(
  {
    name: 'app/files-to-lint',
    files: ['**/*.{ts,mts,tsx,vue}']
  },
  {
    name: 'app/files-to-ignore',
    ignores: ['**/dist/**', '**/coverage/**', '**/playwright-report/**', '**/test-results/**']
  },
  pluginVue.configs['flat/recommended'],
  vueTsConfigs.recommended,
  // formatting is Prettier's job (npm run format / format:check)
  skipFormatting,
  {
    name: 'app/rules',
    rules: {
      'vue/no-undef-components': [
        'error',
        {
          // Vuetify (v-*) and vue-router components are registered globally
          ignorePatterns: ['^v-.+$', '^router-.+$']
        }
      ]
    }
  }
)
