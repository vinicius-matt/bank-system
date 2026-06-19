import Icon from './Icons'

export function StatusBadge({ status }) {
  const map = {
    ATIVA: ['badge-green', 'Ativa'],
    BLOQUEADA: ['badge-red', 'Bloqueada'],
    INATIVA: ['badge-neutral', 'Encerrada'],
  }
  const [cls, label] = map[status] || ['badge-neutral', status || '—']
  return <span className={`badge dot ${cls}`}>{label}</span>
}

export function TipoBadge({ tipo }) {
  return <span className="badge badge-brand">{tipo === 'POUPANCA' ? 'Poupança' : 'Corrente'}</span>
}

export function EmptyState({ icon: I = Icon.Wallet, title, subtitle, action }) {
  return (
    <div className="empty">
      <div className="eic"><I width={26} /></div>
      <div>
        <div style={{ fontWeight: 600, color: 'var(--text)' }}>{title}</div>
        {subtitle && <div style={{ fontSize: '.86rem', marginTop: 4 }}>{subtitle}</div>}
      </div>
      {action}
    </div>
  )
}

const TX_META = {
  DEPOSITO: { cls: 'in', icon: Icon.ArrowDown, sign: 1, label: 'Depósito' },
  SAQUE: { cls: 'out', icon: Icon.ArrowUp, sign: -1, label: 'Saque' },
  TRANSFERENCIA: { cls: 'neutral', icon: Icon.Send, sign: 0, label: 'Transferência' },
  PIX: { cls: 'neutral', icon: Icon.Pix, sign: 0, label: 'Pix' },
}

export function txMeta(tipo) {
  return TX_META[tipo] || { cls: 'neutral', icon: Icon.Receipt, sign: 0, label: tipo }
}
