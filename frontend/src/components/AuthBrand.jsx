import Icon from './Icons'

export default function AuthBrand() {
  return (
    <aside className="auth-brand">
      <div className="brand-logo">
        <span className="mark">
          <svg width="22" height="22" viewBox="0 0 64 64" aria-hidden>
            <path d="M18 42V26l14-7 14 7v16" fill="none" stroke="#06122a" strokeWidth="4" strokeLinejoin="round" />
            <path d="M16 42h32" stroke="#06122a" strokeWidth="4" strokeLinecap="round" />
          </svg>
        </span>
        <span className="name">Nimbus Bank</span>
      </div>

      <div className="auth-hero">
        <h1>O banco digital que <span className="grad">trabalha por você</span>.</h1>
        <p>Gestão completa de contas, transferências instantâneas e controle total do seu dinheiro — com segurança de nível bancário.</p>
      </div>

      <div className="auth-points">
        <div className="auth-point"><span className="chk"><Icon.Shield width={16} /></span> Autenticação protegida por token JWT</div>
        <div className="auth-point"><span className="chk"><Icon.Pix width={16} /></span> Transferências e Pix em segundos</div>
        <div className="auth-point"><span className="chk"><Icon.Trend width={16} /></span> Extrato e limites em tempo real</div>
      </div>
    </aside>
  )
}
