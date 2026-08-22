import { useState } from 'react'
import { api, ApiError } from '../api/client'

export default function ResponseForm({ surveyId, initial, onSaved, onCancel }) {
  const isEdit = Boolean(initial)
  const [respondentName, setRespondentName] = useState(initial?.respondentName ?? '')
  const [answer, setAnswer] = useState(initial?.answer ?? '')
  const [errors, setErrors] = useState({})
  const [submitting, setSubmitting] = useState(false)
  const [formError, setFormError] = useState(null)

  async function handleSubmit(e) {
    e.preventDefault()
    setSubmitting(true)
    setErrors({})
    setFormError(null)
    try {
      const body = { respondentName, answer, surveyId }
      const saved = isEdit
        ? await api.updateResponse(initial.id, body)
        : await api.createResponse(body)
      onSaved(saved)
    } catch (err) {
      if (err instanceof ApiError && err.fieldErrors) {
        setErrors(err.fieldErrors)
      } else if (err instanceof ApiError) {
        setFormError(err.message)
      } else {
        setFormError('Could not reach the server.')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form className="panel form-panel form-panel-compact" onSubmit={handleSubmit}>
      <h4 className="panel-title">{isEdit ? 'Edit response' : 'Add response'}</h4>

      {formError && <div className="alert alert-error">{formError}</div>}

      <div className="field-row">
        <label className="field">
          <span className="field-label">Respondent name</span>
          <input
            value={respondentName}
            onChange={(e) => setRespondentName(e.target.value)}
            placeholder="e.g. Priya Nair"
            maxLength={120}
          />
          {errors.respondentName && <span className="field-error">{errors.respondentName}</span>}
        </label>

        <label className="field">
          <span className="field-label">Answer</span>
          <input
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
            placeholder="e.g. 8 or a short comment"
            maxLength={1000}
          />
          {errors.answer && <span className="field-error">{errors.answer}</span>}
        </label>
      </div>

      <div className="form-actions">
        <button type="button" className="btn btn-ghost" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Saving…' : isEdit ? 'Save changes' : 'Add response'}
        </button>
      </div>
    </form>
  )
}
