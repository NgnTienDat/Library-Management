import { useEffect, useState } from "react"
import { useParams, useNavigate } from "react-router-dom"
import { getBookById } from "../../api/books.api"

function BookDetailPage() {
  const { bookId } = useParams()
  const navigate = useNavigate()

  const [book, setBook] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchBook()
  }, [bookId])

  const fetchBook = async () => {
    try {
      setLoading(true)

      if (!bookId) return

      const data = await getBookById(bookId)

      const result = data?.result || data

      setBook(result)
    } catch (err) {
      console.error("Error fetch book:", err)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-linear-to-b from-slate-50 to-white px-4 py-10 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-6xl rounded-2xl border border-slate-200 bg-white p-8 text-center text-slate-500 shadow-sm">
          Đang tải thông tin sách...
        </div>
      </div>
    )
  }

  if (!book) {
    return (
      <div className="min-h-screen bg-linear-to-b from-slate-50 to-white px-4 py-10 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-6xl rounded-2xl border border-rose-200 bg-rose-50 p-8 text-center text-rose-600 shadow-sm">
          Không tìm thấy sách
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-linear-to-b from-slate-50 to-white px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-6xl">
        <button
          onClick={() => navigate(-1)}
          className="mb-5 inline-flex items-center gap-2 rounded-full border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:border-blue-300 hover:text-blue-700"
        >
          <span aria-hidden="true">←</span>
          Quay lại
        </button>

        <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
          <div className="grid gap-8 p-5 sm:p-7 lg:grid-cols-[300px_1fr] lg:gap-10 lg:p-8">
            <div>
              {book.thumbnailUrl ? (
                <img
                  src={book.thumbnailUrl}
                  alt={book.title}
                  className="aspect-3/4 w-full rounded-xl border border-slate-200 object-cover"
                />
              ) : (
                <div className="flex aspect-3/4 w-full items-center justify-center rounded-xl border border-dashed border-slate-300 bg-slate-100 text-sm text-slate-500">
                  Không có ảnh
                </div>
              )}
            </div>

            <div className="space-y-5">
              <div className="border-b border-slate-200 pb-4">
                <h1 className="text-xl font-bold text-slate-900 sm:text-2xl">Chi tiết sách</h1>
                <p className="mt-1 text-sm text-slate-600">Thông tin và trạng thái của đầu sách</p>
              </div>

              <dl className="grid grid-cols-1 gap-3 text-sm sm:grid-cols-2">
                <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3 sm:col-span-2">
                  <dt className="text-slate-500">Nhan đề</dt>
                  <dd className="mt-1 font-semibold text-slate-900">{book.title || "Chưa cập nhật"}</dd>
                </div>

                <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
                  <dt className="text-slate-500">ISBN</dt>
                  <dd className="mt-1 font-medium text-slate-900">{book.isbn || "Chưa cập nhật"}</dd>
                </div>

                <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
                  <dt className="text-slate-500">Thể loại</dt>
                  <dd className="mt-1 font-medium text-slate-900">{book.categoryName || "Chưa phân loại"}</dd>
                </div>

                <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3 sm:col-span-2">
                  <dt className="text-slate-500">Tác giả</dt>
                  <dd className="mt-1 font-medium text-slate-900">{book.authorNames?.join(", ") || "Chưa có thông tin tác giả"}</dd>
                </div>

                <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
                  <dt className="text-slate-500">Nhà xuất bản</dt>
                  <dd className="mt-1 font-medium text-slate-900">{book.publisher || "Chưa cập nhật"}</dd>
                </div>

                <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
                  <dt className="text-slate-500">Số trang</dt>
                  <dd className="mt-1 font-medium text-slate-900">{book.numberOfPages || 0}</dd>
                </div>

                <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
                  <dt className="text-slate-500">Số lượng còn</dt>
                  <dd className="mt-1 text-base font-semibold text-slate-900">{book.availableCopies ?? 0}</dd>
                </div>

                <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
                  <dt className="text-slate-500">Trạng thái</dt>
                  <dd
                    className={`mt-1 font-semibold ${book.active ? "text-emerald-700" : "text-rose-700"}`}
                  >
                    {book.active ? "Đang phục vụ" : "Tạm ngưng"}
                  </dd>
                </div>
              </dl>

              <section className="rounded-xl border border-slate-200 bg-white p-4 sm:p-5">
                <h2 className="mb-2 text-sm font-semibold uppercase tracking-wide text-slate-500">
                  Mô tả
                </h2>
                <p className="text-sm leading-6 text-slate-700">
                  {book.description || "Chưa có mô tả cho đầu sách này."}
                </p>
              </section>

              
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default BookDetailPage