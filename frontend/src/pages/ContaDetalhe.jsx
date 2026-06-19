import { useCallback, useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { contaApi, baixarBlob } from '../api/services'
import { apiError } from '../api/client'
import { useToast } from '../context/ToastContext'
import { currency, dateTime, maskAccount } from '../utils/format'
import { StatusBadge, TipoBadge, EmptyState, txMeta } from '../components/Common'
import Loader from '../components/Loader'
import Modal from '../components/Modal'
import MoneyInput from '../components/MoneyInput'
import Icon from '../components/Icons'

export default function ContaDetalhe() {
  const { id } = useParams()
  const navigate = useNavigate()
  const toast = useToast()

  const [conta, setConta] = useState(null)
  const [extrato, setExtrato] = useState([])
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [modal, setModal] = useState(null) // 'deposito' | 'saque' | 'limite'

  const load = useCallback(async () => {
    try {
      const [c, e] = await Promise.all([contaApi.buscar(id), contaApi.extrato(id).catch(() => [])])
      setConta(c)
      setExtrato([...e].sort((a, b) => new Date(b.data) - new Date(a.data)))
    } catch (err) {
      toast.error(apiError(err, 'Conta não encontrada.'))
      navigate('/contas')
    } finally { setLoading(false) }
  }, [id]) // eslint-disable-line

  useEffect(() => { load() }, [load])

  const act = async (fn, okMsg) => {
    setBusy(true)
    try { await fn(); toast.success(okMsg); await load() }
    catch (err) { toast.error(apiError(err)) }
    finally { setBusy(false) }
  }

  const baixar = async (formato) => {
    try {
      const blob = formato === 'pdf' ? await contaApi.extratoPdf(id) : await contaApi.extratoCsv(id)
      baixarBlob(blob, `extrato-conta-${conta?.numero || id}.${formato}`)
    } catch (err) {
      toast.error(apiError(err, 'Não foi possível gerar o arquivo.'))
    }
  }

  if (loading) return <Loader full label="Carregando conta…" />
  if (!conta) return null

  const encerrada = conta.status === 'INATIVA'
  const bloqueada = conta.status === 'BLOQUEADA'

  return (
    <>
      <button className="btn btn-ghost btn-sm" onClick={() => navigate('/contas')} style={{ marginBottom: 18 }}>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="m15 18-6-6 6-6"/></svg>
        Contas
      </button>

      <div className="detail-grid">
        <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          {/* Cartão de saldo */}
          <div className="balance-card">
            <div className="row between">
              <div className="lbl"><Icon.Card width={15} /> {maskAccount(conta.numero)}</div>
              <StatusBadge status={conta.status} />
            </div>
            <div className="amount">{currency(conta.saldo)}</div>
            <div className="foot">
              <div>
                <div className="chip-line">NIMBUS · {conta.tipo === 'POUPANCA' ? 'POUPANÇA' : 'CORRENTE'}</div>
                <div style={{ fontSize: '.84rem', opacity: .9, marginTop: 6 }}>
                  Limite (cheque especial): {currency(conta.limite)}
                </div>
              </div>
            </div>
          </div>

          {/* Ações rápidas */}
          <div className="card card-pad">
            <h2 className="h2" style={{ marginBottom: 14 }}>Operações</h2>
            <div className="action-grid">
              <button className="btn btn-ghost" disabled={busy || encerrada || bloqueada} onClick={() => setModal('deposito')}>
                <Icon.ArrowDown width={17} /> Depositar
              </button>
              <button className="btn btn-ghost" disabled={busy || encerrada || bloqueada} onClick={() => setModal('saque')}>
                <Icon.ArrowUp width={17} /> Sacar
              </button>
              <button className="btn btn-ghost" disabled={busy || encerrada} onClick={() => navigate('/transferir', { state: { origemId: conta.id } })}>
                <Icon.Send width={17} /> Transferir
              </button>
              <button className="btn btn-ghost" disabled={busy || encerrada} onClick={() => setModal('limite')}>
                <Icon.Trend width={17} /> Limite
              </button>
            </div>

            <div className="action-grid" style={{ marginTop: 10 }}>
              {bloqueada ? (
                <button className="btn btn-ghost" disabled={busy || encerrada} onClick={() => act(() => contaApi.ativar(conta.id), 'Conta ativada.')}>
                  <Icon.Unlock width={17} /> Ativar
                </button>
              ) : (
                <button className="btn btn-ghost" disabled={busy || encerrada} onClick={() => act(() => contaApi.bloquear(conta.id), 'Conta bloqueada.')}>
                  <Icon.Lock width={17} /> Bloquear
                </button>
              )}
              <button className="btn btn-danger" disabled={busy || encerrada}
                onClick={() => { if (window.confirm('Encerrar esta conta? A ação é definitiva.')) act(() => contaApi.encerrar(conta.id), 'Conta encerrada.') }}>
                <Icon.Power width={17} /> Encerrar
              </button>
            </div>
          </div>

          {/* Dados */}
          <div className="card card-pad">
            <h2 className="h2" style={{ marginBottom: 8 }}>Dados da conta</h2>
            <div className="kv"><span className="k">Número</span><span className="mono">{conta.numero}</span></div>
            <div className="kv"><span className="k">Tipo</span><TipoBadge tipo={conta.tipo} /></div>
            <div className="kv"><span className="k">Status</span><StatusBadge status={conta.status} /></div>
            <div className="kv"><span className="k">Saldo</span><span className="mono">{currency(conta.saldo)}</span></div>
            <div className="kv"><span className="k">Limite</span><span className="mono">{currency(conta.limite)}</span></div>
            <div className="kv"><span className="k">Cliente</span><span>#{conta.clienteId}</span></div>
          </div>
        </div>

        {/* Extrato */}
        <div className="card card-pad">
          <div className="section-head">
            <h2>Extrato</h2>
            <div className="row gap-8">
              <button className="btn btn-ghost btn-sm" onClick={() => baixar('pdf')} disabled={extrato.length === 0}>
                <Icon.Receipt width={15} /> PDF
              </button>
              <button className="btn btn-ghost btn-sm" onClick={() => baixar('csv')} disabled={extrato.length === 0}>
                CSV
              </button>
            </div>
          </div>
          {extrato.length === 0 ? (
            <EmptyState icon={Icon.Receipt} title="Sem movimentações" subtitle="Faça um depósito para começar." />
          ) : (
            <div className="list" style={{ maxHeight: 560, overflowY: 'auto' }}>
              {extrato.map((t) => {
                const m = txMeta(t.tipo)
                return (
                  <div className="tx" key={t.id}>
                    <div className={`ic ${m.cls}`}><m.icon width={19} /></div>
                    <div className="info">
                      <div className="t">{t.descricao || m.label}</div>
                      <div className="d">{dateTime(t.data)}</div>
                    </div>
                    <div className={`amt ${m.sign > 0 ? 'pos' : m.sign < 0 ? 'neg' : ''}`}>
                      {m.sign > 0 ? '+' : m.sign < 0 ? '−' : ''}{currency(t.valor)}
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </div>
      </div>

      <ValorModal
        open={modal === 'deposito'} onClose={() => setModal(null)} title="Depositar" cta="Confirmar depósito"
        onSubmit={(v) => act(() => contaApi.depositar(conta.id, v), 'Depósito realizado.').then(() => setModal(null))} />
      <ValorModal
        open={modal === 'saque'} onClose={() => setModal(null)} title="Sacar" cta="Confirmar saque"
        onSubmit={(v) => act(() => contaApi.sacar(conta.id, v), 'Saque realizado.').then(() => setModal(null))} />
      <ValorModal
        open={modal === 'limite'} onClose={() => setModal(null)} title="Ajustar limite"
        subtitle="Defina o novo valor do cheque especial" cta="Salvar limite" defaultValue={conta.limite}
        onSubmit={(v) => act(() => contaApi.alterarLimite(conta.id, v), 'Limite atualizado.').then(() => setModal(null))} />
    </>
  )
}

function ValorModal({ open, onClose, title, subtitle, cta, onSubmit, defaultValue }) {
  const toast = useToast()
  const [valor, setValor] = useState(0)
  const [saving, setSaving] = useState(false)

  useEffect(() => { if (open) setValor(defaultValue != null ? Number(defaultValue) : 0) }, [open, defaultValue])

  const submit = async (e) => {
    e.preventDefault()
    const v = Number(valor)
    if (!(v >= 0) || (title !== 'Ajustar limite' && v <= 0)) { toast.error('Informe um valor válido.'); return }
    setSaving(true)
    try { await onSubmit(v) } finally { setSaving(false) }
  }

  return (
    <Modal open={open} onClose={onClose} title={title} subtitle={subtitle}>
      <form className="auth-form" onSubmit={submit}>
        <MoneyInput value={valor} onChange={setValor} autoFocus className="input amount-input" />
        <button className="btn btn-primary btn-block" type="submit" disabled={saving}>
          {saving ? <Loader small /> : cta}
        </button>
      </form>
    </Modal>
  )
}
