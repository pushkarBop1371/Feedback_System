import { useState } from 'react'
import { api, ApiError } from '../api/client'

export default function SurveyForm({ initial, onSaved, onCancel }) {
  const isEdit = Boolean(initial)
  const [title, setTitle] = useState(initial?.title ?? '')
  const [question, setQuestion] = useState(initial?.question ?? '')
  const [errors, setErrors] = useState({})
  const [submitting, setSubmitting] = useState(false)
  const [formError, setFormError] = useState(null)

  async function handleSubmit(e) {
    e.preventDefault()
    setSubmitting(true)
    setErrors({})
    setFormError(null)
    try {
      const body = { title, question }
      const saved = isEdit
        ? await api.updateSurvey(initial.id, body)
        : await api.createSurvey(body)
      onSaved(saved)
    } catch (err) {
      if (err instanceof ApiError && err.fieldErrors) {
        setErrors(err.fieldErrors)
      } else if (err instanceof ApiError) {
        setFormError(err.message)
      } else {
        setFormError('Could not reach the server. Is the backend running on :8080?')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form className="panel form-panel" onSubmit={handleSubmit}>
      <h3 className="panel-title">{isEdit ? 'Edit survey' : 'New survey'}</h3>

      {formError && <div className="alert alert-error">{formError}</div>}

      <label className="field">
        <span className="field-label">Title</span>
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="e.g. Onboarding Experience"
          maxLength={150}
        />
        {errors.title && <span className="field-error">{errors.title}</span>}
      </label>

      <label className="field">
        <span className="field-label">Question</span>
        <textarea
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder="e.g. On a scale of 1-10, how smooth was your onboarding?"
          maxLength={500}
          rows={3}
        />
        {errors.question && <span className="field-error">{errors.question}</span>}
      </label>

      <div className="form-actions">
        <button type="button" className="btn btn-ghost" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Saving…' : isEdit ? 'Save changes' : 'Create survey'}
        </button>
      </div>
    </form>
  )
}
