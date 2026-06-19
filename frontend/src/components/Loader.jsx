export default function Loader({ full = false, label = 'Carregando…', small = false }) {
  const ring = (
    <svg className="spin" width={small ? 18 : 30} height={small ? 18 : 30} viewBox="0 0 50 50" aria-hidden>
      <circle cx="25" cy="25" r="20" fill="none" stroke="var(--surface-3)" strokeWidth="5" />
      <circle cx="25" cy="25" r="20" fill="none" stroke="var(--brand)" strokeWidth="5"
        strokeLinecap="round" strokeDasharray="80 200" />
    </svg>
  )
  if (small) return ring
  return (
    <div className={full ? 'loader-full' : 'loader-inline'}>
      {ring}
      {label && <span className="muted" style={{ fontSize: '.92rem' }}>{label}</span>}
    </div>
  )
}
