import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { apiError } from '../api/client'
import { maskCpfInput, maskPhoneInput, soDigitos } from '../utils/format'
import AuthBrand from '../components/AuthBrand'
import Loader from '../components/Loader'
import Icon from '../components/Icons'

const validators = {
  nome: (v) => (!v.trim() ? 'Informe seu nome completo.' : ''),
  email: (v) => (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(v.trim()) ? 'Email inválido.' : ''),
  cpf: (v) => (soDigitos(v).length !== 11 ? 'CPF deve ter 11 dígitos.' : ''),
  celular: (v) => (soDigitos(v).length < 10 ? 'Informe DDD + número.' : ''),
  senha: (v) => (v.length < 6 ? 'Mínimo de 6 caracteres.' : ''),
}

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState({ nome: '', email: '', cpf: '', celular: '', senha: '' })
  const [errors, setErrors] = useState({})
  const [showPwd, setShowPwd] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const setField = (k, value) => {
    setForm((f) => ({ ...f, [k]: value }))
    // limpa o erro do campo assim que o usuário corrige
    if (errors[k]) setErrors((prev) => ({ ...prev, [k]: validators[k](value) }))
  }

  const upd = (k) => (e) => setField(k, e.target.value)
  const updCpf = (e) => setField('cpf', maskCpfInput(e.target.value))
  const updCelular = (e) => setField('celular', maskPhoneInput(e.target.value))
  const validateField = (k) => () => setErrors((prev) => ({ ...prev, [k]: validators[k](form[k]) }))

  const submit = async (e) => {
    e.preventDefault()
    setError('')

    const novoErros = {}
    for (const k of Object.keys(validators)) {
      const msg = validators[k](form[k])
      if (msg) novoErros[k] = msg
    }
    setErrors(novoErros)
    if (Object.keys(novoErros).length > 0) return

    setLoading(true)
    try {
      await register({
        nome: form.nome.trim(),
        email: form.email.trim(),
        senha: form.senha,
        cpf: soDigitos(form.cpf),
        celular: soDigitos(form.celular),
      })
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

          <form className="auth-form" onSubmit={submit} noValidate>
            {error && <div className="auth-error">{error}</div>}

            <div className="field">
              <label htmlFor="nome">Nome completo</label>
              <input id="nome" className={`input ${errors.nome ? 'input-erro' : ''}`} type="text" placeholder="Seu nome"
                value={form.nome} onChange={upd('nome')} onBlur={validateField('nome')} autoComplete="name" />
              {errors.nome && <span className="field-erro">{errors.nome}</span>}
            </div>

            <div className="field">
              <label htmlFor="email">Email</label>
              <input id="email" className={`input ${errors.email ? 'input-erro' : ''}`} type="email" placeholder="voce@email.com"
                value={form.email} onChange={upd('email')} onBlur={validateField('email')} autoComplete="email" />
              {errors.email && <span className="field-erro">{errors.email}</span>}
            </div>

            <div className="row gap-12">
              <div className="field" style={{ flex: 1 }}>
                <label htmlFor="cpf">CPF</label>
                <input id="cpf" className={`input ${errors.cpf ? 'input-erro' : ''}`} inputMode="numeric" placeholder="000.000.000-00"
                  value={form.cpf} onChange={updCpf} onBlur={validateField('cpf')} />
                {errors.cpf && <span className="field-erro">{errors.cpf}</span>}
              </div>
              <div className="field" style={{ flex: 1 }}>
                <label htmlFor="celular">Celular</label>
                <input id="celular" className={`input ${errors.celular ? 'input-erro' : ''}`} inputMode="numeric" placeholder="(00) 00000-0000"
                  value={form.celular} onChange={updCelular} onBlur={validateField('celular')} />
                {errors.celular && <span className="field-erro">{errors.celular}</span>}
              </div>
            </div>

            <div className="field pwd-field">
              <label htmlFor="senha">Senha</label>
              <input id="senha" className={`input ${errors.senha ? 'input-erro' : ''}`} type={showPwd ? 'text' : 'password'} placeholder="Mínimo 6 caracteres"
                value={form.senha} onChange={upd('senha')} onBlur={validateField('senha')} autoComplete="new-password" />
              <button type="button" className="toggle" onClick={() => setShowPwd((s) => !s)} aria-label="Mostrar senha">
                {showPwd ? <Icon.EyeOff width={18} /> : <Icon.Eye width={18} />}
              </button>
              {errors.senha && <span className="field-erro">{errors.senha}</span>}
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
