import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { contaApi } from '../api/services'
import { apiError } from '../api/client'
import { useToast } from '../context/ToastContext'
import { currency, maskAccount } from '../utils/format'
import { StatusBadge } from '../components/Common'
import Loader from '../components/Loader'
import MoneyInput from '../components/MoneyInput'
import Icon from '../components/Icons'

export default function Transferir() {
  const navigate = useNavigate()
  const location = useLocation()
  const toast = useToast()

  const [contas, setContas] = useState([])
  const [loading, setLoading] = useState(true)
  const [origemId, setOrigemId] = useState(location.state?.origemId ? String(location.state.origemId) : '')
  const [destinoId, setDestinoId] = useState('')
  const [valor, setValor] = useState(0)
  const [mensagem, setMensagem] = useState('')
  const [sending, setSending] = useState(false)

  useEffect(() => {
    contaApi.listar()
      .then((list) => setContas(list.filter((c) => c.status === 'ATIVA')))
      .catch((err) => toast.error(apiError(err, 'Falha ao carregar contas.')))
      .finally(() => setLoading(false))
  }, []) // eslint-disable-line

  const origem = contas.find((c) => String(c.id) === String(origemId))
  const v = Number(valor)
  const saldoDisponivel = origem ? Number(origem.saldo) + Number(origem.limite || 0) : 0

  const submit = async (e) => {
    e.preventDefault()
    if (!origemId || !destinoId) { toast.error('Selecione as contas de origem e destino.'); return }
    if (origemId === destinoId) { toast.error('Origem e destino devem ser diferentes.'); return }
    if (!(v > 0)) { toast.error('Informe um valor válido.'); return }
    setSending(true)
    try {
      await contaApi.transferir({ origemId: Number(origemId), destinoId: Number(destinoId), valor: v, mensagem })
      toast.success(`Transferência de ${currency(v)} concluída.`)
      navigate(`/contas/${origemId}`)
    } catch (err) {
      toast.error(apiError(err, 'Não foi possível concluir a transferência.'))
    } finally { setSending(false) }
  }

  if (loading) return <Loader full label="Carregando contas…" />

  return (
    <div style={{ maxWidth: 560, margin: '0 auto' }}>
      <div className="page-head">
        <div>
          <h1>Transferir</h1>
          <p>Movimente dinheiro entre contas ativas</p>
        </div>
      </div>

      {contas.length < 1 ? (
        <div className="card card-pad">
          <div className="empty">
            <div className="eic"><Icon.Send width={26} /></div>
            <div>
              <div style={{ fontWeight: 600, color: 'var(--text)' }}>Você precisa de uma conta ativa</div>
              <div style={{ fontSize: '.86rem', marginTop: 4 }}>Crie uma conta para enviar transferências.</div>
            </div>
            <button className="btn btn-primary" onClick={() => navigate('/contas')}>Ir para Contas</button>
          </div>
        </div>
      ) : (
        <form className="card card-pad" onSubmit={submit} style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
          <div className="field">
            <label>De</label>
            <select className="select" value={origemId} onChange={(e) => setOrigemId(e.target.value)} required>
              <option value="">Selecione a conta de origem</option>
              {contas.map((c) => (
                <option key={c.id} value={c.id}>{maskAccount(c.numero)} · {currency(c.saldo)}</option>
              ))}
            </select>
            {origem && <span className="faint" style={{ fontSize: '.8rem' }}>Disponível com limite: {currency(saldoDisponivel)}</span>}
          </div>

          <div className="row" style={{ justifyContent: 'center', color: 'var(--text-faint)' }}>
            <div className="ic neutral" style={{ width: 38, height: 38, borderRadius: 12, display: 'grid', placeItems: 'center', background: 'var(--brand-soft)', color: '#b7a6ff' }}>
              <Icon.ArrowDown width={18} />
            </div>
          </div>

          <div className="field">
            <label>Para (ID da conta de destino)</label>
            <div className="input-prefix">
              <span>#</span>
              <input className="input" type="number" min="1" placeholder="Ex.: 12"
                value={destinoId} onChange={(e) => setDestinoId(e.target.value)} required />
            </div>
            <span className="faint" style={{ fontSize: '.8rem' }}>
              Informe o ID da conta que vai receber — pode ser sua ou de outra pessoa.
            </span>
            {contas.filter((c) => String(c.id) !== String(origemId)).length > 0 && (
              <div className="row gap-8" style={{ flexWrap: 'wrap', marginTop: 6 }}>
                <span className="faint" style={{ fontSize: '.78rem' }}>Minhas contas:</span>
                {contas.filter((c) => String(c.id) !== String(origemId)).map((c) => (
                  <button type="button" key={c.id} className="badge badge-neutral"
                    style={{ cursor: 'pointer' }} onClick={() => setDestinoId(String(c.id))}>
                    {maskAccount(c.numero)}
                  </button>
                ))}
              </div>
            )}
          </div>

          <div className="field">
            <label>Valor</label>
            <MoneyInput value={valor} onChange={setValor} />
          </div>

          <div className="field">
            <label>Mensagem (opcional)</label>
            <input className="input" type="text" maxLength={120} placeholder="Ex.: aluguel, presente…"
              value={mensagem} onChange={(e) => setMensagem(e.target.value)} />
          </div>

          <button className="btn btn-primary btn-block" type="submit" disabled={sending}>
            {sending ? <Loader small /> : <><Icon.Send width={17} /> Transferir {v > 0 ? currency(v) : ''}</>}
          </button>
        </form>
      )}
    </div>
  )
}
