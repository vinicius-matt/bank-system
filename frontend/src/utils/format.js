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

// Máscara progressiva de CPF para input: 12345678909 -> 123.456.789-09
export const maskCpfInput = (v = '') => {
  const d = String(v).replace(/\D/g, '').slice(0, 11)
  if (d.length > 9) return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9)}`
  if (d.length > 6) return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6)}`
  if (d.length > 3) return `${d.slice(0, 3)}.${d.slice(3)}`
  return d
}

// Máscara progressiva de celular para input: 11999990000 -> (11) 99999-0000
export const maskPhoneInput = (v = '') => {
  const d = String(v).replace(/\D/g, '').slice(0, 11)
  if (d.length > 10) return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`
  if (d.length > 6) return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6)}`
  if (d.length > 2) return `(${d.slice(0, 2)}) ${d.slice(2)}`
  if (d.length > 0) return `(${d}`
  return ''
}

export const soDigitos = (s = '') => String(s).replace(/\D/g, '')
