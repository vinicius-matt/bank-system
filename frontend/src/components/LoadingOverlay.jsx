import Loader from './Loader'

export default function LoadingOverlay({ show, message = 'Processando…', hint }) {
  if (!show) return null
  return (
    <div className="overlay fade-in" role="status" aria-live="polite">
      <div className="overlay-box">
        <Loader />
        <div className="overlay-msg">{message}</div>
        {hint && <div className="overlay-hint">{hint}</div>}
      </div>
    </div>
  )
}
