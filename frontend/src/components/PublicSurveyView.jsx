import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client'
import ResponseForm from './ResponseForm.jsx'

export default function PublicSurveyView() {
  const { id: surveyId } = useParams()
  const navigate = useNavigate()
  const onBack = () => navigate('/')
  const [survey, setSurvey] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [submitted, setSubmitted] = useState(false)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(null)
    setSubmitted(false)
    api
      .getSurvey(surveyId)
      .then((data) => {
        if (!cancelled) setSurvey(data)
      })
      .catch((err) => {
        if (!cancelled) setError(err.message)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [surveyId])

  if (loading) return <p className="hint">Loading survey…</p>
  if (error) return <div className="alert alert-error">{error}</div>
  if (!survey) return null

  return (
    <section>
      <button className="btn btn-ghost back-link" onClick={onBack}>
        ← All surveys
      </button>

      <header className="detail-header">
        <p className="eyebrow mono">SINCE {survey.createdDate}</p>
        <h2>{survey.title}</h2>
        <p className="detail-question">“{survey.question}”</p>
      </header>

      {submitted ? (
        <div className="panel empty-state" style={{ textAlign: 'left' }}>
          <p style={{ fontWeight: 600, marginBottom: 6 }}>Thanks — your response was recorded.</p>
          <p className="hint" style={{ padding: 0 }}>
            Responses aren't shown publicly. If you'd like to submit another response, use the form below.
          </p>
          <div style={{ marginTop: 14 }}>
            <button className="btn btn-ghost" onClick={() => setSubmitted(false)}>
              Submit another response
            </button>
          </div>
        </div>
      ) : (
        <ResponseForm
          surveyId={survey.id}
          onCancel={onBack}
          onSaved={() => setSubmitted(true)}
        />
      )}
    </section>
  )
}
