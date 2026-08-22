import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from './AuthContext.jsx'

/**
 * Wraps every /admin/* route. If the visitor isn't logged in as admin,
 * bounce them to /admin/login and remember the page they were trying to
 * reach (via location state) so AdminLoginPage can send them straight
 * back there after a successful login.
 */
export default function ProtectedAdminRoute() {
  const { isAdmin } = useAuth()
  const location = useLocation()

  if (!isAdmin) {
    return <Navigate to="/admin/login" replace state={{ from: location }} />
  }

  return <Outlet />
}
