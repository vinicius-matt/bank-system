import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Loader from '../components/Loader'

export default function ProtectedRoute({ children }) {
  const { isAuthenticated, booting } = useAuth()
  const location = useLocation()

  if (booting) return <Loader full label="Verificando sessão segura…" />
  if (!isAuthenticated) return <Navigate to="/login" replace state={{ from: location }} />
  return children
}
