import { createContext, useContext, useState, useCallback } from 'react'
import { api, ApiError } from '../api/client'

const STORAGE_KEY = 'ledger_admin_auth'

const AuthContext = createContext(null)

function readStoredAuth() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

// There is only one kind of account in this app: admin. Being authenticated
// IS being an admin - there's no separate "normal user" login at all.
export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(readStoredAuth)

  const persist = useCallback((value) => {
    setAuth(value)
    if (value) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(value))
    } else {
      localStorage.removeItem(STORAGE_KEY)
    }
  }, [])

  const login = useCallback(async (username, password) => {
    const result = await api.login({ username, password }) // { token, username, role }
    persist(result)
    return result
  }, [persist])

  const logout = useCallback(() => {
    persist(null)
  }, [persist])

  const value = {
    isAdmin: Boolean(auth?.token),
    username: auth?.username ?? null,
    token: auth?.token ?? null,
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider')
  return ctx
}

export { ApiError }
