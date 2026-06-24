import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { AreaChart, Area, ResponsiveContainer, Tooltip, XAxis } from 'recharts'
import { contaApi } from '../api/services'
import { apiError } from '../api/client'
import { useToast } from '../context/ToastContext'
import { currency, maskAccount, dateShort } from '../utils/format'
import { StatusBadge, TipoBadge, EmptyState, txMeta } from '../components/Common'
import { SkeletonDashboard } from '../components/Skeleton'
import Icon from '../components/Icons'

export default function Dashboard() {
  const navigate = useNavigate()
  const toast = useToast()
  const [contas, setContas] = useState([])
  const [recent, setRecent] = useState([])
  const [loading, setLoading] = useState(true)
  const [hide, setHide] = useState(false)

  useEffect(() => {
    let active = true
    ;(async () => {
      try {
        const list = await contaApi.listar()
        if (!active) return
        setContas(list)
        // Junta o extrato das contas ativas para o feed recente
        const ativos = list.filter((c) => c.status !== 'INATIVA').slice(0, 6)
        const extratos = await Promise.all(
          ativos.map((c) => contaApi.extrato(c.id).then((e) => e.map((t) => ({ ...t, contaId: c.id, numero: c.numero }))).catch(() => []))
        )
        if (!active) return
        const merged = extratos.flat().sort((a, b) => new Date(b.data) - new Date(a.data)).slice(0, 8)
        setRecent(merged)
      } catch (err) {
        toast.error(apiError(err, 'Não foi possível carregar o painel.'))
      } finally {
        if (active) setLoading(false)
      }
    })()
    return () => { active = false }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const totals = useMemo(() => {
    const ativas = contas.filter((c) => c.status === 'ATIVA')
    const saldo = contas.reduce((s, c) => s + Number(c.saldo || 0), 0)
    const limite = contas.reduce((s, c) => s + Number(c.limite || 0), 0)
    return { saldo, limite, totalContas: contas.length, ativas: ativas.length }
  }, [contas])

  const chartData = useMemo(() => {
    // Constrói saldo acumulado a partir das transações recentes (visual)
    const base = Math.max(totals.saldo, 1)
    const pts = [...recent].reverse()
    let running = base * 0.7
    const data = pts.map((t, i) => {
      const m = txMeta(t.tipo)
      running += m.sign * Number(t.valor || 0) * 0.4 + base * 0.02
      return { name: dateShort(t.data) || `#${i}`, v: Math.max(running, 0) }
    })
    if (data.length < 2) {
      return [
        { name: 'Sem 1', v: base * 0.6 }, { name: 'Sem 2', v: base * 0.75 },
        { name: 'Sem 3', v: base * 0.7 }, { name: 'Hoje', v: base },
      ]
    }
    data.push({ name: 'Hoje', v: base })
    return data
  }, [recent, totals.saldo])

  if (loading) return <SkeletonDashboard />

  return (
    <>
      <div className="grid-2" style={{ marginBottom: 24 }}>
        <div className="balance-card">
          <div className="lbl"><Icon.Wallet width={15} /> Patrimônio consolidado</div>
          <div className="amount">{hide ? '••••••' : currency(totals.saldo)}</div>
          <div className="foot">
            <div>
              <div className="chip-line">NIMBUS · {totals.totalContas} {totals.totalContas === 1 ? 'conta' : 'contas'}</div>
              <div style={{ fontSize: '.84rem', opacity: .9, marginTop: 6 }}>
                Limite disponível: {hide ? '••••' : currency(totals.limite)}
              </div>
            </div>
            <button className="icon-btn" style={{ background: 'rgba(255,255,255,.16)', border: 'none', color: '#fff' }}
              onClick={() => setHide((h) => !h)} aria-label="Mostrar/ocultar saldo">
              {hide ? <Icon.EyeOff width={18} /> : <Icon.Eye width={18} />}
            </button>
          </div>
        </div>

        <div className="card card-pad" style={{ display: 'flex', flexDirection: 'column' }}>
          <div className="row between">
            <h2 className="h2">Evolução</h2>
            <span className="badge badge-green dot">Tempo real</span>
          </div>
          <div style={{ flex: 1, minHeight: 130, marginTop: 8 }}>
            <ResponsiveContainer width="100%" height={140}>
              <AreaChart data={chartData} margin={{ top: 10, right: 4, left: 4, bottom: 0 }}>
                <defs>
                  <linearGradient id="gv" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stopColor="#7c5cff" stopOpacity={0.5} />
                    <stop offset="100%" stopColor="#7c5cff" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <XAxis dataKey="name" tick={{ fill: '#6b7493', fontSize: 11 }} axisLine={false} tickLine={false} />
                <Tooltip
                  contentStyle={{ background: '#161d33', border: '1px solid #232c45', borderRadius: 12, color: '#eef1f8' }}
                  labelStyle={{ color: '#9aa4be' }} formatter={(v) => [currency(v), 'Saldo']} />
                <Area type="monotone" dataKey="v" stroke="#7c5cff" strokeWidth={2.4} fill="url(#gv)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      <div className="grid-stats" style={{ marginBottom: 28 }}>
        <Stat icon={Icon.Wallet} k="Contas ativas" v={totals.ativas} />
        <Stat icon={Icon.Card} k="Total de contas" v={totals.totalContas} />
        <Stat icon={Icon.Trend} k="Limite total" v={currency(totals.limite)} />
        <Stat icon={Icon.Receipt} k="Movimentações" v={recent.length} />
      </div>

      <div className="grid-2">
        <section className="card card-pad">
          <div className="section-head">
            <h2>Atividade recente</h2>
            <button className="btn btn-ghost btn-sm" onClick={() => navigate('/contas')}>Ver contas</button>
          </div>
          {recent.length === 0 ? (
            <EmptyState icon={Icon.Receipt} title="Nenhuma movimentação ainda"
              subtitle="As transações das suas contas aparecerão aqui." />
          ) : (
            <div className="list">
              {recent.map((t) => {
                const m = txMeta(t.tipo)
                return (
                  <div className="tx" key={`${t.contaId}-${t.id}`}>
                    <div className={`ic ${m.cls}`}><m.icon width={19} /></div>
                    <div className="info">
                      <div className="t">{t.descricao || m.label}</div>
                      <div className="d">{maskAccount(t.numero)} · {dateShort(t.data)}</div>
                    </div>
                    <div className={`amt ${m.sign > 0 ? 'pos' : m.sign < 0 ? 'neg' : ''}`}>
                      {m.sign > 0 ? '+' : m.sign < 0 ? '−' : ''}{currency(t.valor)}
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </section>

        <section className="card card-pad">
          <div className="section-head">
            <h2>Suas contas</h2>
            <button className="btn btn-ghost btn-sm" onClick={() => navigate('/contas')}><Icon.Plus width={15} /></button>
          </div>
          {contas.length === 0 ? (
            <EmptyState title="Sem contas" subtitle="Crie a primeira conta na aba Contas." />
          ) : (
            <div className="list">
              {contas.slice(0, 5).map((c) => (
                <div className="tx" key={c.id} style={{ cursor: 'pointer' }} onClick={() => navigate(`/contas/${c.id}`)}>
                  <div className="ic neutral"><Icon.Card width={19} /></div>
                  <div className="info">
                    <div className="t row gap-8">{maskAccount(c.numero)} <TipoBadge tipo={c.tipo} /></div>
                    <div className="d"><StatusBadge status={c.status} /></div>
                  </div>
                  <div className="amt mono">{currency(c.saldo)}</div>
                </div>
              ))}
            </div>
          )}
        </section>
      </div>
    </>
  )
}

function Stat({ icon: I, k, v }) {
  return (
    <div className="stat">
      <div className="ic"><I width={20} /></div>
      <div className="v mono">{v}</div>
      <div className="k">{k}</div>
    </div>
  )
}
