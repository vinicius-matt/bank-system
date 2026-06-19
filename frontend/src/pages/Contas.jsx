import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { contaApi } from '../api/services'
import { apiError } from '../api/client'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { currency, maskAccount } from '../utils/format'
import { StatusBadge, TipoBadge, EmptyState } from '../components/Common'
import Loader from '../components/Loader'
import Modal from '../components/Modal'
import Icon from '../components/Icons'

export default function Contas() {
  const navigate = useNavigate()
  const toast = useToast()
  const { isAdmin } = useAuth()
  const [contas, setContas] = useState([])
  const [indisponiveis, setIndisponiveis] = useState([])
  const [loading, setLoading] = useState(true)
  const [q, setQ] = useState('')
  const [filter, setFilter] = useState('TODAS')
  const [openCreate, setOpenCreate] = useState(false)

  const load = async () => {
    try {
      const [cs, ind] = await Promise.all([
        contaApi.listar(),
        isAdmin ? Promise.resolve([]) : contaApi.indisponiveis().catch(() => []),
      ])
      setContas(cs)
      setIndisponiveis(ind)
    } catch (err) {
      toast.error(apiError(err, 'Falha ao carregar contas.'))
    } finally { setLoading(false) }
  }
  useEffect(() => { load() }, []) // eslint-disable-line

  const filtered = useMemo(() => {
    return contas.filter((c) => {
      const okFilter = filter === 'TODAS' || c.status === filter
      const okQ = !q || String(c.numero).includes(q) || String(c.clienteId).includes(q)
      return okFilter && okQ
    })
  }, [contas, q, filter])

  if (loading) return <Loader full label="Carregando contas…" />

  return (
    <>
      <div className="page-head">
        <div>
          <h1>Contas</h1>
          <p>{contas.length} {contas.length === 1 ? 'conta cadastrada' : 'contas cadastradas'}</p>
        </div>
        <div className="row gap-12">
          <div className="search-box hide-mobile">
            <Icon.Search width={17} />
            <input className="input" placeholder="Buscar nº ou cliente" value={q} onChange={(e) => setQ(e.target.value)} />
          </div>
          <button className="btn btn-primary" onClick={() => setOpenCreate(true)}><Icon.Plus width={17} /> Nova conta</button>
        </div>
      </div>

      {/* Filtros por status: apenas para ADMIN (usuário comum só vê ativas) */}
      {isAdmin && (
        <div className="segmented" style={{ marginBottom: 20 }}>
          {['TODAS', 'ATIVA', 'BLOQUEADA', 'INATIVA'].map((f) => (
            <button key={f} className={filter === f ? 'on' : ''} onClick={() => setFilter(f)}>
              {f === 'TODAS' ? 'Todas' : f === 'ATIVA' ? 'Ativas' : f === 'BLOQUEADA' ? 'Bloqueadas' : 'Encerradas'}
            </button>
          ))}
        </div>
      )}

      {/* Aviso para usuário comum: contas indisponíveis (bloqueadas/encerradas) */}
      {!isAdmin && indisponiveis.length > 0 && (
        <div className="notice" style={{ marginBottom: 20, flexDirection: 'column', alignItems: 'stretch' }}>
          <div className="row gap-12" style={{ marginBottom: 4 }}>
            <span className="notice-ic"><Icon.Lock width={18} /></span>
            <div>
              <div style={{ fontWeight: 600 }}>
                {indisponiveis.length} conta{indisponiveis.length > 1 ? 's' : ''} indisponíve{indisponiveis.length > 1 ? 'is' : 'l'}
              </div>
              <div className="faint" style={{ fontSize: '.82rem' }}>
                Contas bloqueadas ou encerradas são geridas pelo banco. Para reativar, fale com o suporte.
              </div>
            </div>
          </div>
          <div className="list" style={{ marginTop: 6 }}>
            {indisponiveis.map((c) => (
              <div className="tx" key={c.id}>
                <div className="ic neutral"><Icon.Card width={18} /></div>
                <div className="info">
                  <div className="t row gap-8 mono">Nº {c.numero} <TipoBadge tipo={c.tipo} /></div>
                  <div className="d row gap-8">
                    <StatusBadge status={c.status} />
                    <span className="faint">{c.status === 'BLOQUEADA' ? 'Sua conta está bloqueada' : 'Sua conta está encerrada'}</span>
                  </div>
                </div>
                <div className="amt mono">{currency(c.saldo)}</div>
              </div>
            ))}
          </div>
        </div>
      )}

      {filtered.length === 0 ? (
        <div className="card"><EmptyState title="Nenhuma conta encontrada"
          subtitle="Ajuste o filtro ou crie uma nova conta."
          action={<button className="btn btn-primary" onClick={() => setOpenCreate(true)}><Icon.Plus width={16} /> Criar conta</button>} />
        </div>
      ) : (
        <div className="grid-cards">
          {filtered.map((c) => (
            <div className="acct" key={c.id} onClick={() => navigate(`/contas/${c.id}`)}>
              <div className="top">
                <div className="brand-strip" />
                <StatusBadge status={c.status} />
              </div>
              <div className="num">{maskAccount(c.numero)}</div>
              <div className="bal mono">{currency(c.saldo)}</div>
              <div className="meta">
                <TipoBadge tipo={c.tipo} />
                <span className="faint" style={{ fontSize: '.8rem' }}>Cliente #{c.clienteId}</span>
              </div>
            </div>
          ))}
        </div>
      )}

      <CreateContaModal open={openCreate} onClose={() => setOpenCreate(false)} onCreated={() => { setOpenCreate(false); load() }} />
    </>
  )
}

function CreateContaModal({ open, onClose, onCreated }) {
  const toast = useToast()
  const [tipo, setTipo] = useState('CORRENTE')
  const [saving, setSaving] = useState(false)

  useEffect(() => { if (open) setTipo('CORRENTE') }, [open])

  const submit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      // O dono é o usuário logado (modelo 1:1) — não é preciso informar cliente.
      await contaApi.criar({ tipoConta: tipo })
      toast.success('Conta criada com sucesso.')
      onCreated()
    } catch (err) {
      toast.error(apiError(err, 'Não foi possível criar a conta.'))
    } finally { setSaving(false) }
  }

  return (
    <Modal open={open} onClose={onClose} title="Nova conta" subtitle="Abra uma nova conta no seu nome">
      <form className="auth-form" onSubmit={submit}>
        <div className="field">
          <label>Tipo de conta</label>
          <div className="segmented" style={{ width: '100%' }}>
            <button type="button" style={{ flex: 1 }} className={tipo === 'CORRENTE' ? 'on' : ''} onClick={() => setTipo('CORRENTE')}>Corrente</button>
            <button type="button" style={{ flex: 1 }} className={tipo === 'POUPANCA' ? 'on' : ''} onClick={() => setTipo('POUPANCA')}>Poupança</button>
          </div>
        </div>

        <button className="btn btn-primary btn-block" type="submit" disabled={saving}>
          {saving ? <Loader small /> : 'Criar conta'}
        </button>
      </form>
    </Modal>
  )
}
