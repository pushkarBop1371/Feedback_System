const BASE_URL = 'http://localhost:8081/api'
const STORAGE_KEY = 'ledger_admin_auth'

class ApiError extends Error {
  constructor(message, status, fieldErrors) {
    super(message)
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

function getToken() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw)?.token : null
  } catch {
    return null
  }
}

async function request(path, options = {}) {
  const token = getToken()
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) }
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
  })

  if (res.status === 204) {
    return null
  }

  const data = await res.json().catch(() => null)

  if (!res.ok) {
    const message = data?.message || `Request failed with status ${res.status}`
    throw new ApiError(message, res.status, data?.fieldErrors)
  }

  return data
}

export const api = {
  // Admin auth (only admins log in - no self-registration)
  login: (body) => request('/auth/login', { method: 'POST', body: JSON.stringify(body) }),

  // Surveys - reading is public, writing is admin-only (enforced server-side too)
  listSurveys: (params = {}) => {
    const qs = new URLSearchParams(params).toString()
    return request(`/surveys${qs ? `?${qs}` : ''}`)
  },
  getSurvey: (id) => request(`/surveys/${id}`),
  createSurvey: (body) => request('/surveys', { method: 'POST', body: JSON.stringify(body) }),
  updateSurvey: (id, body) => request(`/surveys/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  deleteSurvey: (id) => request(`/surveys/${id}`, { method: 'DELETE' }),
  getSurveyStats: (id) => request(`/surveys/${id}/stats`),
  listSurveyResponses: (id, params = {}) => {
    const qs = new URLSearchParams(params).toString()
    return request(`/surveys/${id}/responses${qs ? `?${qs}` : ''}`)
  },

  // Responses - submitting is public, viewing/editing/deleting is admin-only
  createResponse: (body) => request('/responses', { method: 'POST', body: JSON.stringify(body) }),
  updateResponse: (id, body) => request(`/responses/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  deleteResponse: (id) => request(`/responses/${id}`, { method: 'DELETE' }),
}

export { ApiError }
