import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { apiError } from '../api/client'
import AuthBrand from '../components/AuthBrand'
import Loader from '../components/Loader'
import Icon from '../components/Icons'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = location.state?.from?.pathname || '/'

  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [showPwd, setShowPwd] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(email.trim(), senha)
      navigate(from, { replace: true })
    } catch (err) {
      setError(apiError(err, 'Não foi possível entrar. Verifique suas credenciais.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-wrap">
      <AuthBrand />
      <main className="auth-form-side">
        <div className="auth-card fade-in">
          <h2>Bem-vindo de volta</h2>
          <p className="sub">Acesse sua conta para continuar.</p>

          <form className="auth-form" onSubmit={submit}>
            {error && <div className="auth-error">{error}</div>}

            <div className="field">
              <label htmlFor="email">Email</label>
              <input id="email" className="input" type="email" placeholder="voce@email.com"
                value={email} onChange={(e) => setEmail(e.target.value)} required autoComplete="email" />
            </div>

            <div className="field pwd-field">
              <label htmlFor="senha">Senha</label>
              <input id="senha" className="input" type={showPwd ? 'text' : 'password'} placeholder="••••••••"
                value={senha} onChange={(e) => setSenha(e.target.value)} required autoComplete="current-password" />
              <button type="button" className="toggle" onClick={() => setShowPwd((s) => !s)} aria-label="Mostrar senha">
                {showPwd ? <Icon.EyeOff width={18} /> : <Icon.Eye width={18} />}
              </button>
            </div>

            <button className="btn btn-primary btn-block" type="submit" disabled={loading} style={{ marginTop: 4 }}>
              {loading ? <Loader small /> : 'Entrar'}
            </button>
          </form>

          <div className="demo-hint">
            Conta de demonstração: <b>admin@bank.com</b> · senha <b>admin123</b>
          </div>

          <p className="auth-switch">
            Não tem conta? <Link to="/registrar">Crie agora</Link>
          </p>
        </div>
      </main>
    </div>
  )
}
