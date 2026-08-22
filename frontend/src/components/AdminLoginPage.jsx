import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useAuth, ApiError } from '../auth/AuthContext.jsx'

export default function AdminLoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  // If ProtectedAdminRoute bounced the visitor here, send them back to the
  // page they actually wanted once they log in; otherwise land on the dashboard.
  const redirectTo = location.state?.from?.pathname ?? '/admin'

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await login(username, password)
      navigate(redirectTo, { replace: true })
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.status === 401 ? 'Incorrect username or password.' : err.message)
      } else {
        setError('Could not reach the server. Is the backend running?')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="admin-login-page">
      <div className="panel form-panel admin-login-card">
        <p className="eyebrow mono">RESTRICTED ACCESS</p>
        <h2 className="panel-title" style={{ fontSize: 22, marginBottom: 4 }}>
          Admin Login
        </h2>
        <p className="hint" style={{ marginBottom: 4 }}>
          Only admins can sign in. Surveys are open for everyone to view and respond to without an account.
        </p>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit} className="form-panel" style={{ padding: 0, boxShadow: 'none', border: 'none' }}>
          <label className="field">
            <span className="field-label">Username</span>
            <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="admin" autoFocus />
          </label>
          <label className="field">
            <span className="field-label">Password</span>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
            />
          </label>

          <div className="form-actions" style={{ justifyContent: 'space-between', alignItems: 'center' }}>
            <button type="button" className="btn btn-ghost" onClick={() => navigate('/')}>
              ← Back to surveys
            </button>
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Signing in…' : 'Sign in'}
            </button>
          </div>
        </form>

        <p className="hint mono" style={{ fontSize: 12 }}>
          Demo admin — admin / admin123
        </p>
      </div>
    </section>
  )
}
