import { Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from './auth/ProtectedRoute'
import AppLayout from './components/AppLayout'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import Contas from './pages/Contas'
import ContaDetalhe from './pages/ContaDetalhe'
import Clientes from './pages/Clientes'
import Transferir from './pages/Transferir'
import Pix from './pages/Pix'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/registrar" element={<Register />} />

      <Route
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<Dashboard />} />
        <Route path="/contas" element={<Contas />} />
        <Route path="/contas/:id" element={<ContaDetalhe />} />
        <Route path="/clientes" element={<Clientes />} />
        <Route path="/transferir" element={<Transferir />} />
        <Route path="/pix" element={<Pix />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
