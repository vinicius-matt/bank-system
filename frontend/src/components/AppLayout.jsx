import { useState } from 'react'
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { initials } from '../utils/format'
import Icon from './Icons'
import NotificationBell from './NotificationBell'

const NAV = [
  { to: '/', label: 'Visão geral', icon: Icon.Home, end: true },
  { to: '/contas', label: 'Contas', icon: Icon.Wallet },
  { to: '/transferir', label: 'Transferir', icon: Icon.Send },
  { to: '/pix', label: 'Pix', icon: Icon.Pix },
  { to: '/clientes', label: 'Clientes', icon: Icon.Users, adminOnly: true },
]

const TITLES = {
  '/': { h: 'Visão geral', s: 'Resumo da sua operação bancária' },
  '/contas': { h: 'Contas', s: 'Gerencie todas as contas' },
  '/transferir': { h: 'Transferir', s: 'Envie dinheiro entre contas' },
  '/clientes': { h: 'Clientes', s: 'Cadastro e gestão de clientes' },
}

export default function AppLayout() {
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [open, setOpen] = useState(false)
  const navItems = NAV.filter((n) => !n.adminOnly || isAdmin)

  const title = TITLES[location.pathname] || (location.pathname.startsWith('/contas/')
    ? { h: 'Detalhe da conta', s: 'Movimentações e operações' }
    : { h: 'Nimbus Bank', s: '' })

  const doLogout = async () => { await logout(); navigate('/login', { replace: true }) }

  return (
    <div className="app-shell">
      <aside className={`sidebar ${open ? 'open' : ''}`}>
        <div className="logo">
          <span className="mark">
            <svg width="20" height="20" viewBox="0 0 64 64" aria-hidden>
              <path d="M18 42V26l14-7 14 7v16" fill="none" stroke="#06122a" strokeWidth="4" strokeLinejoin="round" />
              <path d="M16 42h32" stroke="#06122a" strokeWidth="4" strokeLinecap="round" />
            </svg>
          </span>
          <span className="name">Nimbus Bank</span>
        </div>

        <div className="nav-label">Menu</div>
        {navItems.map(({ to, label, icon: I, end }) => (
          <NavLink key={to} to={to} end={end} className="nav-item" onClick={() => setOpen(false)}>
            <I width={19} /> {label}
          </NavLink>
        ))}

        <div className="sidebar-foot">
          <div className="user-chip">
            <span className="avatar">{initials(user?.nome || user?.email)}</span>
            <div className="meta">
              <div className="nm">{user?.nome || 'Usuário'}</div>
              <div className="em">{user?.email}</div>
            </div>
          </div>
          <button className="nav-item" onClick={doLogout} style={{ width: '100%', marginTop: 4 }}>
            <Icon.Logout width={19} /> Sair
          </button>
        </div>
      </aside>

      <div className={`scrim ${open ? 'show' : ''}`} onClick={() => setOpen(false)} />

      <div className="main">
        <header className="topbar">
          <div className="row gap-12">
            <button className="icon-btn menu-btn" onClick={() => setOpen(true)} aria-label="Menu">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"><path d="M4 7h16M4 12h16M4 17h16"/></svg>
            </button>
            <div className="title">
              <h1>{title.h}</h1>
              {title.s && <p className="hide-mobile">{title.s}</p>}
            </div>
          </div>
          <div className="topbar-actions">
            <span className="badge badge-green dot hide-mobile"><Icon.Shield width={13} /> Sessão JWT</span>
            <NotificationBell />
            <span className="avatar hide-mobile" title={user?.email}>{initials(user?.nome || user?.email)}</span>
          </div>
        </header>

        <main className="content fade-in" key={location.pathname}>
          <Outlet />
        </main>
      </div>
    </div>
  )
}
