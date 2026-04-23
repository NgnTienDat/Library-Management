import { useEffect, useRef, useState } from "react"
import { getBorrowHistory } from "../../api/borrow.api"
import { formatDate } from "../../utils/datetime"

const STATUS_FILTERS = [
  { label: "All", value: "" },
  { label: "Borrowing", value: "BORROWING" },
  { label: "Returned", value: "RETURNED" },
  { label: "Overdue", value: "OVERDUE" },
]

function resolveBorrowList(payload) {
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.content)) return payload.content
  if (Array.isArray(payload?.result)) return payload.result
  return []
}

function getStatusConfig(status) {
  if (status === "RETURNED") {
    return {
      label: "Returned",
      className: "text-green-600 font-medium",
    }
  }

  if (status === "OVERDUE") {
    return {
      label: "Overdue",
      className: "text-rose-600 font-medium",
    }
  }

  return {
    label: "Borrowing",
    className: "text-orange-500 font-medium",
  }
}

function MyBorrowedPage() {
  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(false)
  const [status, setStatus] = useState("")
  const latestRequestIdRef = useRef(0)

  useEffect(() => {
    fetchData(status)
  }, [status])

  const fetchData = async (nextStatus) => {
    const requestId = ++latestRequestIdRef.current

    try {
      setLoading(true)

      const params = {}
      if (nextStatus) params.status = nextStatus

      const data = await getBorrowHistory(params)

      // Keep only the latest response when users switch filters quickly.
      if (requestId !== latestRequestIdRef.current) return

      setRecords(resolveBorrowList(data))
    } catch (err) {
      if (requestId === latestRequestIdRef.current) {
        console.error("Borrow error:", err)
      }
    } finally {
      if (requestId === latestRequestIdRef.current) {
        setLoading(false)
      }
    }
  }

  return (
    <div className="p-6 bg-slate-100 min-h-screen">

      <h1 className="text-2xl font-bold mb-6">My Borrowed Books</h1>

      {/* FILTER */}
      <div className="flex flex-wrap gap-3 mb-6">
        {STATUS_FILTERS.map((filter) => (
          <button
            key={filter.value || "ALL"}
            onClick={() => setStatus(filter.value)}
            className={`px-4 py-2 rounded-md ${
              status === filter.value ? "bg-blue-600 text-white" : "bg-white border"
            }`}
          >
            {filter.label}
          </button>
        ))}
      </div>

      {/* LOADING */}
      {loading && <p>Loading...</p>}

      {/* EMPTY */}
      {!loading && records.length === 0 && (
        <p className="text-slate-500">No records</p>
      )}

      {/* LIST */}
      <div className="grid gap-4">
        {records.map((r) => {
          const statusConfig = getStatusConfig(r.status)
          const bookTitle = r.bookTitle || r.title || "Unknown title"
          const thumbnailUrl = r.thumbnailUrl || r.thumbnail || null

          return (
            <div
              key={r.id}
              className="bg-white p-4 rounded-lg shadow flex items-center gap-4"
            >
              {/* IMAGE */}
              {thumbnailUrl ? (
                <img
                  src={thumbnailUrl}
                  alt={bookTitle}
                  className="w-16 h-20 object-cover rounded"
                />
              ) : (
                <div className="w-16 h-20 bg-slate-200 flex items-center justify-center text-xs">
                  No Img
                </div>
              )}

              {/* INFO */}
              <div className="flex-1">
                <h3 className="font-semibold">{bookTitle}</h3>
                <p className="text-sm text-slate-500">
                  {r.authorNames?.join(", ")}
                </p>

                <p className="text-xs mt-1">
                  Borrow: {formatDate(r.borrowDate) || "-"} | Due: {formatDate(r.dueDate) || "-"}
                </p>
              </div>

              {/* STATUS */}
              <div>
                <span className={statusConfig.className}>{statusConfig.label}</span>
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export default MyBorrowedPage