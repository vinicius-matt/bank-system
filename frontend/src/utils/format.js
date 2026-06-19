export const currency = (value) => {
  const n = Number(value ?? 0)
  return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export const dateTime = (value) => {
  if (!value) return '—'
  const d = new Date(value)
  if (isNaN(d)) return '—'
  return d.toLocaleString('pt-BR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

export const dateShort = (value) => {
  if (!value) return '—'
  const d = new Date(value)
  if (isNaN(d)) return '—'
  return d.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' })
}

export const initials = (name = '') =>
  name.trim().split(/\s+/).slice(0, 2).map((p) => p[0]?.toUpperCase() ?? '').join('') || '?'

export const maskAccount = (numero = '') => {
  const s = String(numero)
  return s.length <= 4 ? s : `•••• ${s.slice(-4)}`
}

// Formata e mascara o CPF: 12345678909 -> 123.•••.•••-09
export const maskCpf = (cpf = '') => {
  const d = String(cpf).replace(/\D/g, '')
  if (d.length !== 11) return cpf || '—'
  return `${d.slice(0, 3)}.•••.•••-${d.slice(9)}`
}

// Formata o celular: 11999990000 -> (11) 99999-0000
export const formatPhone = (cel = '') => {
  const d = String(cel).replace(/\D/g, '')
  if (d.length === 11) return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`
  if (d.length === 10) return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6)}`
  return cel || '—'
}
