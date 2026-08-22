export default function StatsPanel({ stats }) {
  if (!stats) return null

  const { totalResponses, numericResponses, nonNumericResponses, averageNumericAnswer, minNumericAnswer, maxNumericAnswer } = stats

  const hasRange = minNumericAnswer != null && maxNumericAnswer != null && maxNumericAnswer > minNumericAnswer
  const avgPercent = hasRange
    ? ((averageNumericAnswer - minNumericAnswer) / (maxNumericAnswer - minNumericAnswer)) * 100
    : 50

  return (
    <div className="panel stats-panel">
      <h4 className="panel-title">Aggregate stats</h4>
      <div className="stats-grid">
        <div className="stat-block">
          <span className="stat-value mono">{totalResponses}</span>
          <span className="stat-label">total responses</span>
        </div>
        <div className="stat-block">
          <span className="stat-value mono">{numericResponses}</span>
          <span className="stat-label">numeric answers</span>
        </div>
        <div className="stat-block">
          <span className="stat-value mono">{nonNumericResponses}</span>
          <span className="stat-label">text answers</span>
        </div>
        <div className="stat-block">
          <span className="stat-value mono">
            {averageNumericAnswer != null ? averageNumericAnswer.toFixed(2) : '—'}
          </span>
          <span className="stat-label">average (numeric)</span>
        </div>
      </div>

      {numericResponses > 0 && (
        <div className="range-bar-wrap">
          <div className="range-bar-labels mono muted">
            <span>{minNumericAnswer}</span>
            <span>{maxNumericAnswer}</span>
          </div>
          <div className="range-bar">
            <div className="range-bar-marker" style={{ left: `${avgPercent}%` }} title={`Average: ${averageNumericAnswer?.toFixed(2)}`} />
          </div>
        </div>
      )}

      {numericResponses === 0 && (
        <p className="hint">No numeric answers yet — averages will appear once respondents submit a number.</p>
      )}
    </div>
  )
}
