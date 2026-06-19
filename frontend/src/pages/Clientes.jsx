import { useEffect, useMemo, useState } from 'react'
import { clienteApi } from '../api/services'
import { apiError } from '../api/client'
import { useToast } from '../context/ToastContext'
import { initials } from '../utils/format'
import { EmptyState } from '../components/Common'
import { useAuth } from '../context/AuthContext'
import Loader from '../components/Loader'
import Modal from '../components/Modal'
import Icon from '../components/Icons'

export default function Clientes() {
  const toast = useToast()
  const { isAdmin } = useAuth()
  const [clientes, setClientes] = useState([])
  const [loading, setLoading] = useState(true)
  const [q, setQ] = useState('')
  const [open, setOpen] = useState(false)

  const load = async () => {
    try { setClientes(await clienteApi.listar({ size: 200 })) }
    catch (err) { toast.error(apiError(err, 'Falha ao carregar clientes.')) }
    finally { setLoading(false) }
  }
  useEffect(() => { if (isAdmin) load(); else setLoading(false) }, [isAdmin]) // eslint-disable-line

  const filtered = useMemo(() => {
    if (!q) return clientes
    const s = q.toLowerCase()
    return clientes.filter((c) => (c.nome || '').toLowerCase().includes(s) || (c.email || '').toLowerCase().includes(s))
  }, [clientes, q])

  if (loading) return <Loader full label="Carregando clientes…" />

  if (!isAdmin) {
    return (
      <div className="card">
        <EmptyState icon={Icon.Shield} title="Área restrita"
          subtitle="A gestão de clientes está disponível apenas para administradores." />
      </div>
    )
  }

  return (
    <>
      <div className="page-head">
        <div>
          <h1>Clientes</h1>
          <p>{clientes.length} {clientes.length === 1 ? 'cliente cadastrado' : 'clientes cadastrados'}</p>
        </div>
        <div className="row gap-12">
          <div className="search-box hide-mobile">
            <Icon.Search width={17} />
            <input className="input" placeholder="Buscar por nome ou email" value={q} onChange={(e) => setQ(e.target.value)} />
          </div>
          <button className="btn btn-primary" onClick={() => setOpen(true)}><Icon.Plus width={17} /> Novo cliente</button>
        </div>
      </div>

      <div className="card">
        {filtered.length === 0 ? (
          <EmptyState icon={Icon.Users} title="Nenhum cliente"
            subtitle="Cadastre o primeiro cliente para abrir contas."
            action={<button className="btn btn-primary" onClick={() => setOpen(true)}><Icon.Plus width={16} /> Cadastrar</button>} />
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table className="data-table">
              <thead>
                <tr><th>ID</th><th>Cliente</th><th className="hide-mobile">Email</th><th>Celular</th></tr>
              </thead>
              <tbody>
                {filtered.map((c, i) => (
                  <tr key={c.id ?? `${c.email || 'c'}-${i}`}>
                    <td className="mono faint">#{c.id}</td>
                    <td>
                      <div className="row gap-12">
                        <span className="avatar" style={{ width: 34, height: 34, fontSize: '.78rem' }}>{initials(c.nome)}</span>
                        <span style={{ fontWeight: 600 }}>{c.nome}</span>
                      </div>
                    </td>
                    <td className="hide-mobile muted">{c.email || '—'}</td>
                    <td className="mono">{c.celular || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <CreateClienteModal open={open} onClose={() => setOpen(false)} onCreated={() => { setOpen(false); load() }} />
    </>
  )
}

function CreateClienteModal({ open, onClose, onCreated }) {
  const toast = useToast()
  const [form, setForm] = useState({ nome: '', cpf: '', email: '', celular: '' })
  const [saving, setSaving] = useState(false)

  useEffect(() => { if (open) setForm({ nome: '', cpf: '', email: '', celular: '' }) }, [open])

  const upd = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }))

  const submit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const created = await clienteApi.criar(form)
      toast.success(created?.id ? `Cliente criado (ID #${created.id}).` : 'Cliente criado.')
      onCreated()
    } catch (err) {
      toast.error(apiError(err, 'Não foi possível cadastrar o cliente.'))
    } finally { setSaving(false) }
  }

  return (
    <Modal open={open} onClose={onClose} title="Novo cliente" subtitle="Cadastre os dados do titular">
      <form className="auth-form" onSubmit={submit}>
        <div className="field">
          <label>Nome completo</label>
          <input className="input" value={form.nome} onChange={upd('nome')} placeholder="Maria da Silva" required />
        </div>
        <div className="field">
          <label>CPF</label>
          <input className="input" value={form.cpf} onChange={upd('cpf')} placeholder="000.000.000-00" required />
        </div>
        <div className="field">
          <label>Email</label>
          <input className="input" type="email" value={form.email} onChange={upd('email')} placeholder="maria@email.com" required />
        </div>
        <div className="field">
          <label>Celular</label>
          <input className="input" value={form.celular} onChange={upd('celular')} placeholder="(11) 90000-0000" required />
        </div>
        <button className="btn btn-primary btn-block" type="submit" disabled={saving}>
          {saving ? <Loader small /> : 'Cadastrar cliente'}
        </button>
      </form>
    </Modal>
  )
}
