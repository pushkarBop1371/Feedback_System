import { useCallback, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client'
import Pagination from './Pagination.jsx'
import ResponseForm from './ResponseForm.jsx'
import StatsPanel from './StatsPanel.jsx'

export default function AdminSurveyDetail() {
  const { id: surveyId } = useParams()
  const navigate = useNavigate()
  const onBack = () => navigate('/admin')
  const [survey, setSurvey] = useState(null)
  const [stats, setStats] = useState(null)
  const [responses, setResponses] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)

  const loadAll = useCallback(async (pageToLoad = 0) => {
    setLoading(true)
    setError(null)
    try {
      const [surveyData, statsData, responsesData] = await Promise.all([
        api.getSurvey(surveyId),
        api.getSurveyStats(surveyId),
        api.listSurveyResponses(surveyId, { page: pageToLoad, size: 8 }),
      ])
      setSurvey(surveyData)
      setStats(statsData)
      setResponses(responsesData.content)
      setTotalPages(responsesData.totalPages)
      setPage(pageToLoad)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [surveyId])

  useEffect(() => {
    loadAll(0)
  }, [loadAll])

  async function handleDeleteResponse(response) {
    if (!window.confirm(`Delete the response from "${response.respondentName}"?`)) return
    try {
      await api.deleteResponse(response.id)
      loadAll(page)
    } catch (err) {
      alert(err.message)
    }
  }

  if (loading && !survey) return <p className="hint">Loading survey…</p>
  if (error) return <div className="alert alert-error">{error}</div>
  if (!survey) return null

  return (
    <section>
      <button className="btn btn-ghost back-link" onClick={onBack}>
        ← All surveys
      </button>

      <header className="detail-header">
        <p className="eyebrow mono">SURVEY #{String(survey.id).padStart(3, '0')} · SINCE {survey.createdDate}</p>
        <h2>{survey.title}</h2>
        <p className="detail-question">“{survey.question}”</p>
      </header>

      <StatsPanel stats={stats} />

      <div className="panel-header-row">
        <h3 className="section-title">Responses ({survey.responseCount})</h3>
        <button
          className="btn btn-primary"
          onClick={() => {
            setEditing(null)
            setShowForm((v) => !v)
          }}
        >
          {showForm && !editing ? 'Close' : '+ Add response'}
        </button>
      </div>

      {showForm && (
        <ResponseForm
          surveyId={survey.id}
          initial={editing}
          onCancel={() => {
            setShowForm(false)
            setEditing(null)
          }}
          onSaved={() => {
            setShowForm(false)
            setEditing(null)
            loadAll(0)
          }}
        />
      )}

      {responses.length === 0 ? (
        <div className="empty-state">
          <p>No responses yet.</p>
        </div>
      ) : (
        <ul className="response-ledger">
          {responses.map((response) => (
            <li key={response.id} className="ledger-row">
              <div className="ledger-main">
                <span className="ledger-name">{response.respondentName}</span>
                <span className="ledger-answer">{response.answer}</span>
              </div>
              <div className="ledger-side">
                <span className="mono muted">{response.submittedDate}</span>
                <div className="ledger-actions">
                  <button
                    className="btn btn-ghost btn-small"
                    onClick={() => {
                      setEditing(response)
                      setShowForm(true)
                    }}
                  >
                    Edit
                  </button>
                  <button
                    className="btn btn-ghost btn-small btn-danger"
                    onClick={() => handleDeleteResponse(response)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </li>
          ))}
        </ul>
      )}

      <Pagination page={page} totalPages={totalPages} onChange={loadAll} />
    </section>
  )
}
