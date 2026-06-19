import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { clienteApi, contaApi } from '../api/services'
import { apiError } from '../api/client'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { initials, currency, maskCpf } from '../utils/format'
import { StatusBadge, TipoBadge } from '../components/Common'
import Loader from '../components/Loader'
import Icon from '../components/Icons'

const soDigitos = (s) => (s || '').replace(/\D/g, '')

export default function Perfil() {
  const { user } = useAuth()
  const toast = useToast()
  const navigate = useNavigate()

  const [perfil, setPerfil] = useState(null)
  const [contas, setContas] = useState([])
  const [form, setForm] = useState({ nome: '', email: '', celular: '' })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    Promise.all([
      clienteApi.meuPerfil(),
      contaApi.listar().catch(() => []),
    ])
      .then(([p, cs]) => {
        setPerfil(p)
        setForm({ nome: p.nome || '', email: p.email || '', celular: p.celular || '' })
        setContas(Array.isArray(cs) ? cs : [])
      })
      .catch((err) => {
        // 404 = login sem perfil de titular (ex.: conta admin/operador). Não é erro.
        if (err?.response?.status !== 404) {
          toast.error(apiError(err, 'Não foi possível carregar seu perfil.'))
        }
      })
      .finally(() => setLoading(false))
  }, []) // eslint-disable-line

  const upd = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }))

  const alterado =
    perfil && (form.nome !== (perfil.nome || '') ||
      form.email !== (perfil.email || '') ||
      form.celular !== (perfil.celular || ''))

  const resumo = useMemo(() => {
    const ativas = contas.filter((c) => c.status === 'ATIVA').length
    const patrimonio = contas.reduce((s, c) => s + Number(c.saldo || 0), 0)
    return { total: contas.length, ativas, patrimonio }
  }, [contas])

  const salvar = async (e) => {
    e.preventDefault()
    const payload = {
      nome: form.nome.trim(),
      email: form.email.trim(),
      celular: soDigitos(form.celular),
    }
    if (!payload.nome) { toast.error('Informe seu nome.'); return }
    if (payload.celular && (payload.celular.length < 10 || payload.celular.length > 11)) {
      toast.error('Celular deve ter DDD + número.'); return
    }
    setSaving(true)
    try {
      const atualizado = await clienteApi.atualizarMeuPerfil(payload)
      setPerfil(atualizado)
      setForm({ nome: atualizado.nome || '', email: atualizado.email || '', celular: atualizado.celular || '' })
      toast.success('Perfil atualizado.')
    } catch (err) {
      toast.error(apiError(err, 'Não foi possível salvar as alterações.'))
    } finally { setSaving(false) }
  }

  if (loading) return <Loader full label="Carregando seu perfil…" />

  if (!perfil) {
    return (
      <div className="card card-pad">
        <div className="empty">
          <div className="eic"><Icon.Shield width={26} /></div>
          <div>
            <div style={{ fontWeight: 600, color: 'var(--text)' }}>Sem perfil de titular</div>
            <div style={{ fontSize: '.86rem', marginTop: 4, maxWidth: 360 }}>
              Este login é de um operador/administrador e não está vinculado a um cliente.
              Perfis ficam disponíveis para contas criadas pelo cadastro de cliente.
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div style={{ maxWidth: 580, margin: '0 auto' }}>
      {/* Cabeçalho do perfil */}
      <div className="card card-pad" style={{ marginBottom: 20 }}>
        <div className="row gap-16">
          <span className="avatar" style={{ width: 64, height: 64, fontSize: '1.3rem' }}>
            {initials(perfil.nome || perfil.email)}
          </span>
          <div style={{ minWidth: 0 }}>
            <div className="h2">{perfil.nome}</div>
            <div className="muted" style={{ fontSize: '.9rem' }}>{perfil.email}</div>
            <div className="row gap-8" style={{ marginTop: 8, flexWrap: 'wrap' }}>
              <span className="badge badge-brand">Cliente #{perfil.id}</span>
              <span className="badge badge-neutral">{user?.role === 'ADMIN' ? 'Administrador' : 'Titular'}</span>
            </div>
          </div>
        </div>

        {/* Dados rápidos */}
        <div style={{ marginTop: 18 }}>
          <div className="kv"><span className="k">CPF</span><span className="mono">{maskCpf(perfil.cpf)}</span></div>
          <div className="kv"><span className="k">Contas</span><span>{resumo.total} ({resumo.ativas} ativa{resumo.ativas === 1 ? '' : 's'})</span></div>
          <div className="kv"><span className="k">Patrimônio</span><span className="mono">{currency(resumo.patrimonio)}</span></div>
        </div>
      </div>

      {/* Minhas contas */}
      <div className="card card-pad" style={{ marginBottom: 20 }}>
        <div className="section-head">
          <h2>Minhas contas</h2>
          <button className="btn btn-ghost btn-sm" onClick={() => navigate('/contas')}>Ver todas</button>
        </div>
        {contas.length === 0 ? (
          <p className="muted" style={{ fontSize: '.9rem' }}>Você ainda não possui contas.</p>
        ) : (
          <div className="list">
            {contas.map((c) => (
              <div className="tx" key={c.id} style={{ cursor: 'pointer' }} onClick={() => navigate(`/contas/${c.id}`)}>
                <div className="ic neutral"><Icon.Card width={19} /></div>
                <div className="info">
                  <div className="t row gap-8 mono">Nº {c.numero} <TipoBadge tipo={c.tipo} /></div>
                  <div className="d"><StatusBadge status={c.status} /></div>
                </div>
                <div className="amt mono">{currency(c.saldo)}</div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Formulário de edição */}
      <form className="card card-pad" onSubmit={salvar} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
        <h2 className="h2">Dados pessoais</h2>

        <div className="field">
          <label htmlFor="nome">Nome completo</label>
          <input id="nome" className="input" value={form.nome} onChange={upd('nome')} required />
        </div>

        <div className="field">
          <label htmlFor="email">Email</label>
          <input id="email" className="input" type="email" value={form.email} onChange={upd('email')} required />
        </div>

        <div className="field">
          <label htmlFor="celular">Celular</label>
          <input id="celular" className="input" inputMode="numeric" placeholder="DDD + número"
            value={form.celular} onChange={upd('celular')} />
        </div>

        <div className="field">
          <label>CPF</label>
          <input className="input" value={maskCpf(perfil.cpf)} disabled
            style={{ opacity: .7, cursor: 'not-allowed' }} />
          <span className="faint" style={{ fontSize: '.78rem' }}>O CPF não pode ser alterado.</span>
        </div>

        <button className="btn btn-primary btn-block" type="submit" disabled={saving || !alterado}>
          {saving ? <Loader small /> : 'Salvar alterações'}
        </button>
      </form>
    </div>
  )
}
