import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig, configDefaults } from 'vitest/config'
import viteConfig from './vite.config'

// Dates are rendered in German local time; make tests independent of the machine's time zone.
process.env.TZ = 'Europe/Berlin'

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude, 'e2e/**'],
      root: fileURLToPath(new URL('./', import.meta.url)),
      setupFiles: ['./vitest.setup.ts'],
      // Vuetify ships CSS imports inside its components, so it must be processed by Vite
      server: { deps: { inline: ['vuetify'] } },
      coverage: {
        provider: 'v8',
        include: ['src/**/*.{ts,vue}'],
        reporter: ['text-summary', 'html']
      }
    }
  })
)
