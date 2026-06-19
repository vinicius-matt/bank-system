import { useEffect, useRef, useState, useCallback } from 'react'
import { notificacaoApi } from '../api/services'
import { dateTime } from '../utils/format'
import Icon from './Icons'

const POLL_MS = 30000

export default function NotificationBell() {
  const [open, setOpen] = useState(false)
  const [items, setItems] = useState([])
  const [unread, setUnread] = useState(0)
  const [loading, setLoading] = useState(false)
  const ref = useRef(null)

  const carregarContador = useCallback(async () => {
    try { setUnread(await notificacaoApi.naoLidas()) } catch { /* silencioso */ }
  }, [])

  const carregarLista = useCallback(async () => {
    setLoading(true)
    try { setItems(await notificacaoApi.listar()) } catch { /* silencioso */ }
    finally { setLoading(false) }
  }, [])

  // Poll do contador de não lidas
  useEffect(() => {
    carregarContador()
    const t = setInterval(carregarContador, POLL_MS)
    return () => clearInterval(t)
  }, [carregarContador])

  // Fecha ao clicar fora
  useEffect(() => {
    const onClick = (e) => { if (ref.current && !ref.current.contains(e.target)) setOpen(false) }
    document.addEventListener('mousedown', onClick)
    return () => document.removeEventListener('mousedown', onClick)
  }, [])

  const toggle = () => {
    const next = !open
    setOpen(next)
    if (next) carregarLista()
  }

  const marcarTodas = async () => {
    try {
      await notificacaoApi.marcarTodasComoLidas()
      setItems((list) => list.map((n) => ({ ...n, lida: true })))
      setUnread(0)
    } catch { /* silencioso */ }
  }

  const aoClicarItem = async (n) => {
    if (n.lida) return
    try {
      await notificacaoApi.marcarComoLida(n.id)
      setItems((list) => list.map((x) => (x.id === n.id ? { ...x, lida: true } : x)))
      setUnread((u) => Math.max(0, u - 1))
    } catch { /* silencioso */ }
  }

  const corTipo = (t) => (t === 'PIX' ? 'var(--brand-2)' : t === 'ALERTA' ? 'var(--red)' : 'var(--brand)')

  return (
    <div className="notif" ref={ref}>
      <button className="icon-btn" onClick={toggle} aria-label="Notificações">
        <Icon.Bell width={19} />
        {unread > 0 && <span className="notif-badge">{unread > 9 ? '9+' : unread}</span>}
      </button>

      {open && (
        <div className="notif-panel fade-in">
          <div className="notif-head">
            <strong>Notificações</strong>
            {items.some((n) => !n.lida) && (
              <button className="notif-link" onClick={marcarTodas}>Marcar todas como lidas</button>
            )}
          </div>

          <div className="notif-list">
            {loading ? (
              <div className="notif-empty">Carregando…</div>
            ) : items.length === 0 ? (
              <div className="notif-empty">Nenhuma notificação por aqui.</div>
            ) : (
              items.map((n) => (
                <button key={n.id} className={`notif-item ${n.lida ? '' : 'unread'}`} onClick={() => aoClicarItem(n)}>
                  <span className="notif-dot" style={{ background: corTipo(n.tipo) }} />
                  <span className="notif-body">
                    <span className="notif-title">{n.titulo}</span>
                    <span className="notif-msg">{n.mensagem}</span>
                    <span className="notif-time">{dateTime(n.criadoEm)}</span>
                  </span>
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  )
}
