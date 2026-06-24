export function Skeleton({ width = '100%', height = 16, radius = 8, style }) {
  return <span className="skeleton" style={{ width, height, borderRadius: radius, ...style }} />
}

// Grade de cartões de conta (usada na tela de Contas durante o carregamento)
export function SkeletonContas({ count = 6 }) {
  return (
    <div className="grid-cards">
      {Array.from({ length: count }).map((_, i) => (
        <div className="acct" key={i} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          <div className="row between">
            <Skeleton width={34} height={24} radius={5} />
            <Skeleton width={70} height={20} radius={999} />
          </div>
          <Skeleton width="55%" height={14} />
          <Skeleton width="70%" height={26} />
          <div className="row between">
            <Skeleton width={80} height={20} radius={999} />
            <Skeleton width={60} height={12} />
          </div>
        </div>
      ))}
    </div>
  )
}

// Esqueleto do painel inicial (Dashboard)
export function SkeletonDashboard() {
  return (
    <>
      <div className="grid-2" style={{ marginBottom: 24 }}>
        <div className="balance-card" style={{ background: 'var(--surface)', display: 'flex', flexDirection: 'column', gap: 14 }}>
          <Skeleton width="40%" height={14} />
          <Skeleton width="60%" height={34} />
          <Skeleton width="50%" height={12} style={{ marginTop: 18 }} />
        </div>
        <div className="card card-pad" style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <Skeleton width="35%" height={16} />
          <Skeleton width="100%" height={120} radius={12} />
        </div>
      </div>

      <div className="grid-stats" style={{ marginBottom: 28 }}>
        {Array.from({ length: 4 }).map((_, i) => (
          <div className="stat" key={i} style={{ gap: 12 }}>
            <Skeleton width={40} height={40} radius={12} />
            <Skeleton width="55%" height={22} />
            <Skeleton width="70%" height={12} />
          </div>
        ))}
      </div>

      <div className="grid-2">
        {[0, 1].map((c) => (
          <div className="card card-pad" key={c} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <Skeleton width="40%" height={16} />
            {Array.from({ length: 4 }).map((_, i) => (
              <div className="row gap-12" key={i}>
                <Skeleton width={42} height={42} radius={13} />
                <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 6 }}>
                  <Skeleton width="60%" height={12} />
                  <Skeleton width="40%" height={10} />
                </div>
                <Skeleton width={60} height={14} />
              </div>
            ))}
          </div>
        ))}
      </div>
    </>
  )
}
