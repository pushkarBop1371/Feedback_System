import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import Pagination from './Pagination.jsx'
import SurveyForm from './SurveyForm.jsx'

export default function AdminSurveyList() {
  const navigate = useNavigate()
  const [surveys, setSurveys] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [titleFilter, setTitleFilter] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)

  const load = useCallback(async (pageToLoad, filter) => {
    setLoading(true)
    setError(null)
    try {
      const result = await api.listSurveys({ page: pageToLoad, size: 6, title: filter })
      setSurveys(result.content)
      setTotalPages(result.totalPages)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    setPage(0)
    load(0, titleFilter)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [titleFilter])

  useEffect(() => {
    load(page, titleFilter)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page])

  async function handleDelete(survey) {
    if (!window.confirm(`Delete "${survey.title}" and all of its responses? This can't be undone.`)) {
      return
    }
    try {
      await api.deleteSurvey(survey.id)
      load(page, titleFilter)
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <section>
      <div className="toolbar">
        <input
          className="search-input"
          placeholder="Filter by title…"
          value={titleFilter}
          onChange={(e) => setTitleFilter(e.target.value)}
        />
        <button
          className="btn btn-primary"
          onClick={() => {
            setEditing(null)
            setShowForm((v) => !v)
          }}
        >
          {showForm && !editing ? 'Close' : '+ New survey'}
        </button>
      </div>

      {showForm && (
        <SurveyForm
          initial={editing}
          onCancel={() => {
            setShowForm(false)
            setEditing(null)
          }}
          onSaved={() => {
            setShowForm(false)
            setEditing(null)
            setPage(0)
            load(0, titleFilter)
          }}
        />
      )}

      {loading && <p className="hint">Loading surveys…</p>}
      {error && <div className="alert alert-error">{error}</div>}

      {!loading && !error && surveys.length === 0 && (
        <div className="empty-state">
          <p>No surveys yet. Create one to start collecting responses.</p>
        </div>
      )}

      <div className="survey-grid">
        {surveys.map((survey, idx) => (
          <article className="survey-card" key={survey.id}>
            <div className="survey-card-index">{String(page * 6 + idx + 1).padStart(2, '0')}</div>
            <div className="survey-card-body">
              <h3 onClick={() => navigate(`/admin/surveys/${survey.id}`)}>{survey.title}</h3>
              <p className="survey-card-question">{survey.question}</p>
              <div className="survey-card-meta">
                <span className="pill">{survey.responseCount} response{survey.responseCount === 1 ? '' : 's'}</span>
                <span className="mono muted">since {survey.createdDate}</span>
              </div>
            </div>
            <div className="survey-card-actions">
              <button className="btn btn-ghost" onClick={() => navigate(`/admin/surveys/${survey.id}`)}>View responses</button>
              <button
                className="btn btn-ghost"
                onClick={() => {
                  setEditing(survey)
                  setShowForm(true)
                }}
              >
                Edit
              </button>
              <button className="btn btn-ghost btn-danger" onClick={() => handleDelete(survey)}>
                Delete
              </button>
            </div>
          </article>
        ))}
      </div>

      <Pagination page={page} totalPages={totalPages} onChange={setPage} />
    </section>
  )
}
