import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    // the browser of the Playwright MCP runs in Docker and reaches the dev server via host.docker.internal
    allowedHosts: ['host.docker.internal'],
    // Used when VITE_BACKEND_URL is not set (npm run dev:agent): the app calls /api on its own origin and Vite forwards
    // it to the backend. This way a browser in a container only needs to reach the dev server (see AGENTS.md).
    proxy: {
      '/api': {
        target: process.env.BACKEND_PROXY_TARGET ?? 'http://127.0.0.1:8080',
        // the browser sees same-origin requests; drop the Origin header so the backend's CORS check does not apply
        configure: (proxy) => proxy.on('proxyReq', (proxyReq) => proxyReq.removeHeader('origin'))
      }
    }
  }
})
