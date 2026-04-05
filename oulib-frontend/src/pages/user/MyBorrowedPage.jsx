import { useEffect, useState } from "react"
import { getBorrowHistory } from "../../api/borrow.api"

function MyBorrowedPage() {
  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(false)
  const [status, setStatus] = useState("")

  useEffect(() => {
    fetchData()
  }, [status])

  const fetchData = async () => {
    try {
      setLoading(true)

      const params = {}
      if (status) params.status = status

      const data = await getBorrowHistory(params)

      // 🔥 tùy backend trả kiểu nào
      const list = data?.content || data || []

      setRecords(list)
    } catch (err) {
      console.error("Borrow error:", err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="p-6 bg-slate-100 min-h-screen">

      <h1 className="text-2xl font-bold mb-6">My Borrowed Books</h1>

      {/* FILTER */}
      <div className="flex gap-3 mb-6">
        <button
          onClick={() => setStatus("")}
          className={`px-4 py-2 rounded-md ${
            status === "" ? "bg-blue-600 text-white" : "bg-white border"
          }`}
        >
          All
        </button>

        <button
          onClick={() => setStatus("BORROWING")}
          className={`px-4 py-2 rounded-md ${
            status === "BORROWING"
              ? "bg-blue-600 text-white"
              : "bg-white border"
          }`}
        >
          Borrowing
        </button>

        <button
          onClick={() => setStatus("RETURNED")}
          className={`px-4 py-2 rounded-md ${
            status === "RETURNED"
              ? "bg-blue-600 text-white"
              : "bg-white border"
          }`}
        >
          Returned
        </button>
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
          const isReturned = !!r.returnedDate

          return (
            <div
              key={r.id}
              className="bg-white p-4 rounded-lg shadow flex items-center gap-4"
            >
              {/* IMAGE */}
              {r.thumbnailUrl ? (
                <img
                  src={r.thumbnailUrl}
                  className="w-16 h-20 object-cover rounded"
                />
              ) : (
                <div className="w-16 h-20 bg-slate-200 flex items-center justify-center text-xs">
                  No Img
                </div>
              )}

              {/* INFO */}
              <div className="flex-1">
                <h3 className="font-semibold">{r.bookTitle}</h3>
                <p className="text-sm text-slate-500">
                  {r.authorNames?.join(", ")}
                </p>

                <p className="text-xs mt-1">
                  Borrow: {r.borrowDate} | Due: {r.dueDate}
                </p>
              </div>

              {/* STATUS */}
              <div>
                {isReturned ? (
                  <span className="text-green-600 font-medium">
                    Returned
                  </span>
                ) : (
                  <span className="text-orange-500 font-medium">
                    Borrowing
                  </span>
                )}
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export default MyBorrowedPage