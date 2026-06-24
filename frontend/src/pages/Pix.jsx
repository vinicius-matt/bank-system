import { useEffect, useMemo, useState } from 'react'
import { contaApi, pixApi } from '../api/services'
import { apiError } from '../api/client'
import { useToast } from '../context/ToastContext'
import { currency, maskAccount } from '../utils/format'
import { EmptyState } from '../components/Common'
import Loader from '../components/Loader'
import LoadingOverlay from '../components/LoadingOverlay'
import Modal from '../components/Modal'
import MoneyInput from '../components/MoneyInput'
import Icon from '../components/Icons'

const TIPOS = [
  { v: 'EMAIL', label: 'Email' },
  { v: 'CPF', label: 'CPF' },
  { v: 'CELULAR', label: 'Celular' },
  { v: 'ALEATORIA', label: 'Aleatória' },
]
const tipoLabel = (v) => TIPOS.find((t) => t.v === v)?.label || v

export default function Pix() {
  const toast = useToast()
  const [tab, setTab] = useState('transferir')
  const [contas, setContas] = useState([])
  const [chaves, setChaves] = useState([])
  const [loading, setLoading] = useState(true)
  const [openChave, setOpenChave] = useState(false)

  const load = async () => {
    try {
      const [cs, ks] = await Promise.all([contaApi.listar(), pixApi.minhasChaves()])
      setContas(cs.filter((c) => c.status === 'ATIVA'))
      setChaves(ks)
    } catch (err) {
      toast.error(apiError(err, 'Falha ao carregar dados do Pix.'))
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load() }, []) // eslint-disable-line

  if (loading) return <Loader full label="Carregando Pix…" />

  return (
    <div style={{ maxWidth: 620, margin: '0 auto' }}>
      <div className="page-head">
        <div>
          <h1 className="row gap-8"><Icon.Pix width={22} /> Pix</h1>
          <p>Transfira na hora com uma chave</p>
        </div>
      </div>

      <div className="segmented" style={{ marginBottom: 20 }}>
        <button className={tab === 'transferir' ? 'on' : ''} onClick={() => setTab('transferir')}>Transferir</button>
        <button className={tab === 'chaves' ? 'on' : ''} onClick={() => setTab('chaves')}>Minhas chaves</button>
      </div>

      {tab === 'transferir'
        ? <TransferirPix contas={contas} onDone={load} />
        : <Chaves chaves={chaves} contas={contas} onNova={() => setOpenChave(true)} onReload={load} />}

      <NovaChaveModal open={openChave} contas={contas} onClose={() => setOpenChave(false)}
        onCreated={() => { setOpenChave(false); load() }} />
    </div>
  )
}

function TransferirPix({ contas, onDone }) {
  const toast = useToast()
  const [origemId, setOrigemId] = useState(contas[0] ? String(contas[0].id) : '')
  const [chave, setChave] = useState('')
  const [valor, setValor] = useState(0)
  const [mensagem, setMensagem] = useState('')
  const [sending, setSending] = useState(false)

  const v = Number(valor)

  const submit = async (e) => {
    e.preventDefault()
    if (!origemId) { toast.error('Selecione a conta de origem.'); return }
    if (!chave.trim()) { toast.error('Informe a chave Pix de destino.'); return }
    if (!(v > 0)) { toast.error('Informe um valor válido.'); return }
    setSending(true)
    try {
      const r = await pixApi.transferir({ origemId: Number(origemId), chaveDestino: chave.trim(), valor: v, mensagem })
      toast.success(`Pix de ${currency(r.valor)} enviado!`)
      setChave(''); setValor(0); setMensagem('')
      onDone()
    } catch (err) {
      toast.error(apiError(err, 'Não foi possível concluir o Pix.'))
    } finally { setSending(false) }
  }

  if (contas.length === 0) {
    return (
      <div className="card card-pad">
        <EmptyState icon={Icon.Pix} title="Nenhuma conta ativa" subtitle="Crie uma conta para enviar Pix." />
      </div>
    )
  }

  return (
    <form className="card card-pad" onSubmit={submit} style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
      <LoadingOverlay show={sending} message="Enviando Pix…" hint="Isso pode levar alguns segundos." />
      <div className="field">
        <label>De</label>
        <select className="select" value={origemId} onChange={(e) => setOrigemId(e.target.value)} required>
          {contas.map((c) => (
            <option key={c.id} value={c.id}>{maskAccount(c.numero)} · {currency(c.saldo)}</option>
          ))}
        </select>
      </div>

      <div className="field">
        <label>Chave Pix de destino</label>
        <input className="input" placeholder="Email, CPF, celular ou chave aleatória"
          value={chave} onChange={(e) => setChave(e.target.value)} required />
      </div>

      <div className="field">
        <label>Valor</label>
        <MoneyInput value={valor} onChange={setValor} />
      </div>

      <div className="field">
        <label>Mensagem (opcional)</label>
        <input className="input" maxLength={120} placeholder="Ex.: divisão da conta"
          value={mensagem} onChange={(e) => setMensagem(e.target.value)} />
      </div>

      <button className="btn btn-primary btn-block" type="submit" disabled={sending}>
        {sending ? <Loader small /> : <><Icon.Pix width={17} /> Enviar Pix {v > 0 ? currency(v) : ''}</>}
      </button>
    </form>
  )
}

function Chaves({ chaves, onNova, onReload }) {
  const toast = useToast()

  const excluir = async (id) => {
    if (!window.confirm('Excluir esta chave Pix?')) return
    try {
      await pixApi.excluirChave(id)
      toast.success('Chave removida.')
      onReload()
    } catch (err) {
      toast.error(apiError(err, 'Não foi possível remover a chave.'))
    }
  }

  return (
    <div className="card card-pad">
      <div className="section-head">
        <h2>Minhas chaves</h2>
        <button className="btn btn-primary btn-sm" onClick={onNova}><Icon.Plus width={15} /> Nova chave</button>
      </div>

      {chaves.length === 0 ? (
        <EmptyState icon={Icon.Pix} title="Nenhuma chave cadastrada"
          subtitle="Cadastre uma chave para receber Pix." />
      ) : (
        <div className="list">
          {chaves.map((k) => (
            <div className="tx" key={k.id}>
              <div className="ic neutral"><Icon.Pix width={19} /></div>
              <div className="info">
                <div className="t row gap-8">
                  <span className="badge badge-brand">{tipoLabel(k.tipo)}</span>
                  <span style={{ wordBreak: 'break-all' }}>{k.valor}</span>
                </div>
                <div className="d">Conta {maskAccount(k.numeroConta)}</div>
              </div>
              <button className="btn btn-danger btn-sm" onClick={() => excluir(k.id)}>Excluir</button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

function NovaChaveModal({ open, contas, onClose, onCreated }) {
  const toast = useToast()
  const [tipo, setTipo] = useState('EMAIL')
  const [valor, setValor] = useState('')
  const [contaId, setContaId] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (open) { setTipo('EMAIL'); setValor(''); setContaId(contas[0] ? String(contas[0].id) : '') }
  }, [open, contas])

  const submit = async (e) => {
    e.preventDefault()
    if (!contaId) { toast.error('Selecione a conta.'); return }
    setSaving(true)
    try {
      await pixApi.criarChave({ tipo, contaId: Number(contaId), valor: tipo === 'ALEATORIA' ? null : valor })
      toast.success('Chave Pix criada.')
      onCreated()
    } catch (err) {
      toast.error(apiError(err, 'Não foi possível criar a chave.'))
    } finally { setSaving(false) }
  }

  return (
    <Modal open={open} onClose={onClose} title="Nova chave Pix" subtitle="Vincule uma chave a uma conta">
      <form className="auth-form" onSubmit={submit}>
        <div className="field">
          <label>Conta</label>
          <select className="select" value={contaId} onChange={(e) => setContaId(e.target.value)} required>
            {contas.map((c) => (
              <option key={c.id} value={c.id}>{maskAccount(c.numero)}</option>
            ))}
          </select>
        </div>

        <div className="field">
          <label>Tipo de chave</label>
          <div className="segmented" style={{ width: '100%', flexWrap: 'wrap' }}>
            {TIPOS.map((t) => (
              <button type="button" key={t.v} style={{ flex: 1 }}
                className={tipo === t.v ? 'on' : ''} onClick={() => setTipo(t.v)}>{t.label}</button>
            ))}
          </div>
        </div>

        {tipo !== 'ALEATORIA' && (
          <div className="field">
            <label>Valor da chave</label>
            <input className="input" value={valor} onChange={(e) => setValor(e.target.value)}
              placeholder={tipo === 'EMAIL' ? 'voce@email.com' : tipo === 'CPF' ? 'Somente números' : 'DDD + número'}
              required />
          </div>
        )}
        {tipo === 'ALEATORIA' && (
          <p className="faint" style={{ fontSize: '.84rem' }}>Uma chave aleatória será gerada automaticamente.</p>
        )}

        <button className="btn btn-primary btn-block" type="submit" disabled={saving}>
          {saving ? <Loader small /> : 'Criar chave'}
        </button>
      </form>
    </Modal>
  )
}
