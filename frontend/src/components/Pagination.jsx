export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null

  return (
    <div className="pagination">
      <button
        className="btn btn-ghost"
        onClick={() => onChange(page - 1)}
        disabled={page <= 0}
      >
        ← Prev
      </button>
      <span className="pagination-status">
        Page <strong>{page + 1}</strong> of <strong>{totalPages}</strong>
      </span>
      <button
        className="btn btn-ghost"
        onClick={() => onChange(page + 1)}
        disabled={page >= totalPages - 1}
      >
        Next →
      </button>
    </div>
  )
}
