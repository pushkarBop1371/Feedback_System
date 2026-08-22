import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import Pagination from './Pagination.jsx'

export default function PublicSurveyList() {
  const navigate = useNavigate()
  const [surveys, setSurveys] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [titleFilter, setTitleFilter] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

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

  return (
    <section>
      <div className="toolbar">
        <input
          className="search-input"
          placeholder="Filter by title…"
          value={titleFilter}
          onChange={(e) => setTitleFilter(e.target.value)}
        />
      </div>

      {loading && <p className="hint">Loading surveys…</p>}
      {error && <div className="alert alert-error">{error}</div>}

      {!loading && !error && surveys.length === 0 && (
        <div className="empty-state">
          <p>No surveys are open right now. Check back later.</p>
        </div>
      )}

      <div className="survey-grid">
        {surveys.map((survey, idx) => (
          <article className="survey-card" key={survey.id}>
            <div className="survey-card-index">{String(page * 6 + idx + 1).padStart(2, '0')}</div>
            <div className="survey-card-body">
              <h3 onClick={() => navigate(`/surveys/${survey.id}`)}>{survey.title}</h3>
              <p className="survey-card-question">{survey.question}</p>
              <div className="survey-card-meta">
                <span className="mono muted">open since {survey.createdDate}</span>
              </div>
            </div>
            <div className="survey-card-actions">
              <button className="btn btn-primary" onClick={() => navigate(`/surveys/${survey.id}`)}>
                Respond
              </button>
            </div>
          </article>
        ))}
      </div>

      <Pagination page={page} totalPages={totalPages} onChange={setPage} />
    </section>
  )
}
