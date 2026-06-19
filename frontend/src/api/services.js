import api from './client'

// Algumas listagens agora retornam Page<> do Spring; desembrulhamos para array.
const unwrap = (data) => (data && Array.isArray(data.content) ? data.content : data)

// ---------- Auth ----------
export const authApi = {
  login: (payload) => api.post('/auth/login', payload).then((r) => r.data),
  register: (payload) => api.post('/auth/register', payload).then((r) => r.data),
  refresh: (refreshToken) => api.post('/auth/refresh', { refreshToken }).then((r) => r.data),
  logout: (refreshToken) => api.post('/auth/logout', { refreshToken }).then((r) => r.data),
  me: () => api.get('/auth/me').then((r) => r.data),
}

// ---------- Clientes ----------
export const clienteApi = {
  meuPerfil: () => api.get('/clientes/me').then((r) => r.data),
  atualizarMeuPerfil: (payload) => api.patch('/clientes/me', payload).then((r) => r.data),
  // Admin
  listar: (params = {}) => api.get('/clientes/listar', { params }).then((r) => unwrap(r.data)),
  buscar: (id) => api.get(`/clientes/${id}`).then((r) => r.data),
  criar: (payload) => api.post('/clientes/criar', payload).then((r) => r.data),
  atualizar: (id, payload) => api.patch(`/clientes/${id}`, payload).then((r) => r.data),
}

// ---------- Contas ----------
export const contaApi = {
  listar: (params = { size: 100 }) => api.get('/conta/listar', { params }).then((r) => unwrap(r.data)),
  resumo: () => api.get('/conta/resumo').then((r) => r.data),
  indisponiveis: () => api.get('/conta/indisponiveis').then((r) => r.data),
  buscar: (id) => api.get(`/conta/${id}`).then((r) => r.data),
  criar: (payload) => api.post('/conta/criar', payload).then((r) => r.data),
  saldo: (id) => api.get(`/conta/${id}/saldo`).then((r) => r.data),
  extrato: (id) => api.get(`/conta/${id}/extrato`).then((r) => r.data),
  depositar: (id, valor) => api.post(`/conta/${id}/depositar`, { valor }).then((r) => r.data),
  sacar: (id, valor) => api.post(`/conta/${id}/sacar`, { valor }).then((r) => r.data),
  transferir: (payload) => api.post('/conta/transferir', payload).then((r) => r.data),
  bloquear: (id) => api.put(`/conta/${id}/Bloquear`).then((r) => r.data),
  ativar: (id) => api.put(`/conta/${id}/Ativar`).then((r) => r.data),
  encerrar: (id) => api.put(`/conta/${id}/encerrar`).then((r) => r.data),
  alterarLimite: (id, valor) => api.put(`/conta/${id}/alterarLimite`, { valor }).then((r) => r.data),
  // Exportações (blob)
  extratoPdf: (id) => api.get(`/conta/${id}/extrato/pdf`, { responseType: 'blob' }).then((r) => r.data),
  extratoCsv: (id) => api.get(`/conta/${id}/extrato/csv`, { responseType: 'blob' }).then((r) => r.data),
}

// ---------- PIX ----------
export const pixApi = {
  minhasChaves: () => api.get('/pix/chaves/minhas').then((r) => r.data),
  chavesDaConta: (contaId) => api.get(`/pix/chaves/conta/${contaId}`).then((r) => r.data),
  criarChave: (payload) => api.post('/pix/chaves', payload).then((r) => r.data),
  excluirChave: (id) => api.delete(`/pix/chaves/${id}`).then((r) => r.data),
  transferir: (payload) => api.post('/pix/transferir', payload).then((r) => r.data),
}

// ---------- Notificações ----------
export const notificacaoApi = {
  listar: () => api.get('/notificacoes').then((r) => r.data),
  naoLidas: () => api.get('/notificacoes/nao-lidas').then((r) => r.data?.total ?? 0),
  marcarComoLida: (id) => api.patch(`/notificacoes/${id}/lida`).then((r) => r.data),
  marcarTodasComoLidas: () => api.patch('/notificacoes/lidas').then((r) => r.data),
}

// Dispara o download de um Blob no navegador
export const baixarBlob = (blob, filename) => {
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  a.remove()
  window.URL.revokeObjectURL(url)
}
