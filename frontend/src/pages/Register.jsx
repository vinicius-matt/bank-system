import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { apiError } from '../api/client'
import AuthBrand from '../components/AuthBrand'
import Loader from '../components/Loader'
import Icon from '../components/Icons'

const soDigitos = (s) => (s || '').replace(/\D/g, '')

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState({ nome: '', email: '', cpf: '', celular: '', senha: '' })
  const [showPwd, setShowPwd] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const upd = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }))

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    const cpf = soDigitos(form.cpf)
    const celular = soDigitos(form.celular)
    if (form.senha.length < 6) { setError('A senha deve ter no mínimo 6 caracteres.'); return }
    if (cpf.length !== 11) { setError('CPF deve conter 11 dígitos.'); return }
    if (celular.length < 10) { setError('Celular deve conter DDD + número.'); return }

    setLoading(true)
    try {
      await register({ nome: form.nome.trim(), email: form.email.trim(), senha: form.senha, cpf, celular })
      navigate('/', { replace: true })
    } catch (err) {
      setError(apiError(err, 'Não foi possível criar a conta.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-wrap">
      <AuthBrand />
      <main className="auth-form-side">
        <div className="auth-card fade-in">
          <h2>Criar conta</h2>
          <p className="sub">Abra sua conta no Nimbus em menos de um minuto.</p>

          <form className="auth-form" onSubmit={submit}>
            {error && <div className="auth-error">{error}</div>}

            <div className="field">
              <label htmlFor="nome">Nome completo</label>
              <input id="nome" className="input" type="text" placeholder="Seu nome"
                value={form.nome} onChange={upd('nome')} required autoComplete="name" />
            </div>

            <div className="field">
              <label htmlFor="email">Email</label>
              <input id="email" className="input" type="email" placeholder="voce@email.com"
                value={form.email} onChange={upd('email')} required autoComplete="email" />
            </div>

            <div className="row gap-12">
              <div className="field" style={{ flex: 1 }}>
                <label htmlFor="cpf">CPF</label>
                <input id="cpf" className="input" inputMode="numeric" placeholder="Somente números"
                  value={form.cpf} onChange={upd('cpf')} required />
              </div>
              <div className="field" style={{ flex: 1 }}>
                <label htmlFor="celular">Celular</label>
                <input id="celular" className="input" inputMode="numeric" placeholder="DDD + número"
                  value={form.celular} onChange={upd('celular')} required />
              </div>
            </div>

            <div className="field pwd-field">
              <label htmlFor="senha">Senha</label>
              <input id="senha" className="input" type={showPwd ? 'text' : 'password'} placeholder="Mínimo 6 caracteres"
                value={form.senha} onChange={upd('senha')} required autoComplete="new-password" />
              <button type="button" className="toggle" onClick={() => setShowPwd((s) => !s)} aria-label="Mostrar senha">
                {showPwd ? <Icon.EyeOff width={18} /> : <Icon.Eye width={18} />}
              </button>
            </div>

            <button className="btn btn-primary btn-block" type="submit" disabled={loading} style={{ marginTop: 4 }}>
              {loading ? <Loader small /> : 'Criar conta'}
            </button>
          </form>

          <p className="auth-switch">
            Já tem conta? <Link to="/login">Entrar</Link>
          </p>
        </div>
      </main>
    </div>
  )
}
