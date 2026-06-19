// Conjunto de ícones em SVG (stroke), sem dependências externas.
const base = {
  width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none',
  stroke: 'currentColor', strokeWidth: 1.8, strokeLinecap: 'round', strokeLinejoin: 'round',
}

export const Icon = {
  Home: (p) => (<svg {...base} {...p}><path d="M3 10.5 12 3l9 7.5"/><path d="M5 9.5V21h14V9.5"/><path d="M9.5 21v-6h5v6"/></svg>),
  Wallet: (p) => (<svg {...base} {...p}><rect x="3" y="6" width="18" height="13" rx="3"/><path d="M16 12h3"/><path d="M3 9h13a2 2 0 0 1 0 4"/></svg>),
  Users: (p) => (<svg {...base} {...p}><circle cx="9" cy="8" r="3.2"/><path d="M3.5 20a5.5 5.5 0 0 1 11 0"/><path d="M16 5.2a3.2 3.2 0 0 1 0 6.1"/><path d="M17 14.5a5.5 5.5 0 0 1 3.5 5.5"/></svg>),
  Send: (p) => (<svg {...base} {...p}><path d="M21 3 10.5 13.5"/><path d="M21 3 14 21l-3.5-7.5L3 10z"/></svg>),
  ArrowDown: (p) => (<svg {...base} {...p}><path d="M12 4v14"/><path d="m6 12 6 6 6-6"/></svg>),
  ArrowUp: (p) => (<svg {...base} {...p}><path d="M12 20V6"/><path d="m6 12 6-6 6 6"/></svg>),
  Plus: (p) => (<svg {...base} {...p}><path d="M12 5v14"/><path d="M5 12h14"/></svg>),
  Lock: (p) => (<svg {...base} {...p}><rect x="4.5" y="10" width="15" height="10" rx="2.5"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/></svg>),
  Unlock: (p) => (<svg {...base} {...p}><rect x="4.5" y="10" width="15" height="10" rx="2.5"/><path d="M8 10V7a4 4 0 0 1 7.7-1.5"/></svg>),
  Power: (p) => (<svg {...base} {...p}><path d="M12 3v8"/><path d="M6.3 6.3a8 8 0 1 0 11.4 0"/></svg>),
  Logout: (p) => (<svg {...base} {...p}><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="M16 17l5-5-5-5"/><path d="M21 12H9"/></svg>),
  Eye: (p) => (<svg {...base} {...p}><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></svg>),
  EyeOff: (p) => (<svg {...base} {...p}><path d="M10.7 6.2A9.7 9.7 0 0 1 12 6c6.5 0 10 6 10 6a16 16 0 0 1-3.3 3.9M6.2 6.2C3.6 7.8 2 10 2 12s3.5 6 10 6c1.2 0 2.3-.2 3.3-.5"/><path d="m4 4 16 16"/></svg>),
  Card: (p) => (<svg {...base} {...p}><rect x="2.5" y="5" width="19" height="14" rx="3"/><path d="M2.5 10h19"/></svg>),
  Trend: (p) => (<svg {...base} {...p}><path d="M3 17l6-6 4 4 8-8"/><path d="M21 11V7h-4"/></svg>),
  Receipt: (p) => (<svg {...base} {...p}><path d="M5 3v18l2-1.3L9 21l2-1.3L13 21l2-1.3L17 21l2-1.3V3l-2 1.3L15 3l-2 1.3L11 3 9 4.3 7 3Z"/><path d="M8 8h8M8 12h8M8 16h5"/></svg>),
  Pix: (p) => (<svg {...base} {...p}><path d="M12 3.5 7.5 8M12 3.5 16.5 8M12 20.5 7.5 16M12 20.5 16.5 16M3.5 12 8 7.5M3.5 12 8 16.5M20.5 12 16 7.5M20.5 12 16 16.5"/></svg>),
  Search: (p) => (<svg {...base} {...p}><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>),
  Close: (p) => (<svg {...base} {...p}><path d="M6 6l12 12M18 6 6 18"/></svg>),
  Check: (p) => (<svg {...base} {...p}><path d="M5 12.5 10 17l9-10"/></svg>),
  Spark: (p) => (<svg {...base} {...p}><path d="M12 3v4M12 17v4M3 12h4M17 12h4M5.6 5.6l2.8 2.8M15.6 15.6l2.8 2.8M18.4 5.6l-2.8 2.8M8.4 15.6l-2.8 2.8"/></svg>),
  Settings: (p) => (<svg {...base} {...p}><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.6 1.6 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.6 1.6 0 0 0-2.7.7 1.6 1.6 0 0 0-1.6 1.3 2 2 0 0 1-4 0 1.6 1.6 0 0 0-2.4-1 1.6 1.6 0 0 0-1.8.3l-.1.1A2 2 0 1 1 1.6 17l.1-.1a1.6 1.6 0 0 0-1-2.7 2 2 0 0 1 0-4 1.6 1.6 0 0 0 1-2.7L1.6 7A2 2 0 1 1 4.4 4.2l.1.1a1.6 1.6 0 0 0 2.7-1A2 2 0 0 1 11 1.8a1.6 1.6 0 0 0 2.7 1l.1-.1A2 2 0 1 1 19.4 5l-.1.1a1.6 1.6 0 0 0-1 2.7 2 2 0 0 1 0 4 1.6 1.6 0 0 0-1 1.2Z"/></svg>),
  Bell: (p) => (<svg {...base} {...p}><path d="M6 9a6 6 0 0 1 12 0c0 5 2 6 2 6H4s2-1 2-6Z"/><path d="M10 20a2 2 0 0 0 4 0"/></svg>),
  Shield: (p) => (<svg {...base} {...p}><path d="M12 3 5 6v6c0 4.5 3 7.5 7 9 4-1.5 7-4.5 7-9V6Z"/><path d="m9 12 2 2 4-4"/></svg>),
}

export default Icon
