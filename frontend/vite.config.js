import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Porta 3000 para casar com o CORS do backend (Spring Security).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    host: true,
  },
})
