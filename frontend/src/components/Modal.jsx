import { useEffect } from 'react'
import Icon from './Icons'

export default function Modal({ open, onClose, title, subtitle, children, width = 460 }) {
  useEffect(() => {
    if (!open) return
    const onKey = (e) => e.key === 'Escape' && onClose?.()
    window.addEventListener('keydown', onKey)
    document.body.style.overflow = 'hidden'
    return () => {
      window.removeEventListener('keydown', onKey)
      document.body.style.overflow = ''
    }
  }, [open, onClose])

  if (!open) return null
  return (
    <div className="modal-overlay" onMouseDown={onClose}>
      <div className="modal fade-in" style={{ maxWidth: width }} onMouseDown={(e) => e.stopPropagation()}>
        <div className="modal-head">
          <div>
            <h3 className="h2">{title}</h3>
            {subtitle && <p className="muted" style={{ fontSize: '.86rem', marginTop: 2 }}>{subtitle}</p>}
          </div>
          <button className="icon-btn" onClick={onClose} aria-label="Fechar"><Icon.Close /></button>
        </div>
        <div className="modal-body">{children}</div>
      </div>
    </div>
  )
}
