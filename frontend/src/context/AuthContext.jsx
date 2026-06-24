import { createContext, useContext, useEffect, useMemo, useState, useCallback } from 'react'
import api, { TOKEN_KEY, REFRESH_KEY, setUnauthorizedHandler } from '../api/client'
import { authApi } from '../api/services'

const AuthContext = createContext(null)
const USER_KEY = 'nimbus.user'

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY))
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem(USER_KEY)) } catch { return null }
  })
  const [booting, setBooting] = useState(true)

  const persist = useCallback((auth) => {
    const u = {
      id: auth.usuarioId,
      clienteId: auth.clienteId,
      nome: auth.nome,
      email: auth.email,
      role: auth.role,
    }
    localStorage.setItem(TOKEN_KEY, auth.token)
    if (auth.refreshToken) localStorage.setItem(REFRESH_KEY, auth.refreshToken)
    localStorage.setItem(USER_KEY, JSON.stringify(u))
    setToken(auth.token)
    setUser(u)
  }, [])

  const clearSession = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_KEY)
    localStorage.removeItem(USER_KEY)
    setToken(null)
    setUser(null)
  }, [])

  const logout = useCallback(async () => {
    const refreshToken = localStorage.getItem(REFRESH_KEY)
    if (refreshToken) {
      try { await authApi.logout(refreshToken) } catch { /* ignora */ }
    }
    clearSession()
  }, [clearSession])

  const login = useCallback(async (email, senha) => {
    const auth = await authApi.login({ email, senha })
    persist(auth)
    return auth
  }, [persist])

  const register = useCallback(async (payload) => {
    const auth = await authApi.register(payload)
    persist(auth)
    return auth
  }, [persist])

  // 401 sem refresh válido -> encerra sessão localmente
  useEffect(() => {
    setUnauthorizedHandler(() => clearSession())
  }, [clearSession])

  // Valida o token no boot
  useEffect(() => {
    let active = true
    const verify = async () => {
      if (!token) { setBooting(false); return }
      try {
        const me = await authApi.me()
        if (active) setUser((prev) => ({ ...prev, ...me }))
      } catch {
        if (active) clearSession()
      } finally {
        if (active) setBooting(false)
      }
    }
    verify()
    return () => { active = false }
  }, [])

  const value = useMemo(
    () => ({
      token,
      user,
      isAuthenticated: !!token,
      isAdmin: user?.role === 'ADMIN',
      booting,
      login,
      register,
      logout,
    }),
    [token, user, booting, login, register, logout]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth deve ser usado dentro de <AuthProvider>')
  return ctx
}

export { api }
