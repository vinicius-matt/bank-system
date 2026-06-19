import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
export const TOKEN_KEY = 'nimbus.token'
export const REFRESH_KEY = 'nimbus.refresh'

const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

// Injeta o access token (JWT) em toda requisição
api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Handler chamado quando a sessão não pode mais ser recuperada
let onUnauthorized = null
export const setUnauthorizedHandler = (fn) => { onUnauthorized = fn }

const isAuthUrl = (url = '') =>
  url.includes('/auth/login') || url.includes('/auth/register') || url.includes('/auth/refresh')

// Controle de refresh concorrente: enfileira requisições enquanto renova
let refreshing = null

async function renovarToken() {
  const refreshToken = localStorage.getItem(REFRESH_KEY)
  if (!refreshToken) throw new Error('Sem refresh token')
  // Chamada "crua" (sem interceptors) para evitar loop
  const { data } = await axios.post(`${BASE_URL}/auth/refresh`, { refreshToken })
  localStorage.setItem(TOKEN_KEY, data.token)
  if (data.refreshToken) localStorage.setItem(REFRESH_KEY, data.refreshToken)
  return data.token
}

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const status = error?.response?.status
    const original = error?.config || {}
    const url = original.url || ''

    if (status === 401 && !original._retry && !isAuthUrl(url) && localStorage.getItem(REFRESH_KEY)) {
      original._retry = true
      try {
        if (!refreshing) refreshing = renovarToken().finally(() => { refreshing = null })
        const novoToken = await refreshing
        original.headers = original.headers || {}
        original.headers.Authorization = `Bearer ${novoToken}`
        return api(original)
      } catch (e) {
        if (onUnauthorized) onUnauthorized()
        return Promise.reject(error)
      }
    }

    if ((status === 401 || status === 403) && !isAuthUrl(url) && !localStorage.getItem(REFRESH_KEY)) {
      if (onUnauthorized) onUnauthorized()
    }
    return Promise.reject(error)
  }
)

// Extrai mensagem amigável de erro do GlobalExceptionHandler
export const apiError = (err, fallback = 'Algo deu errado. Tente novamente.') => {
  const data = err?.response?.data
  if (typeof data === 'string') return data
  return data?.message || data?.error || err?.message || fallback
}

export default api
