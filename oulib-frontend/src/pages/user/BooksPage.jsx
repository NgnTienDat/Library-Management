import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { getBooks } from "../../api/books.api"
import { getAllCategories } from "../../api/categories.api"
import { getTrending, getPopular } from "../../api/recommendations.api"

function BooksPage() {
  const navigate = useNavigate()

  const [books, setBooks] = useState([])
  const [categories, setCategories] = useState([])
  const [search, setSearch] = useState("")
  const [categoryId, setCategoryId] = useState("")
  const [loading, setLoading] = useState(false)

  // PHAN TRANG
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  // GOI Y
  const [trending, setTrending] = useState([])
  const [popular, setPopular] = useState([])

  useEffect(() => {
    fetchCategories()
    fetchBooks()
    fetchRecommendations()
  }, [])

  useEffect(() => {
    fetchBooks()
  }, [categoryId])

  useEffect(() => {
    fetchBooks()
  }, [page])

  useEffect(() => {
    setPage(0)
  }, [categoryId])

  const fetchCategories = async () => {
    try {
      const data = await getAllCategories()
      setCategories(data?.result || data || [])
    } catch (err) {
      console.error(err)
    }
  }

  const fetchBooks = async () => {
    try {
      setLoading(true)

      const params = {
        page: page,
        size: 12,
      }

      if (search) params.keyword = search
      if (categoryId) params.categoryId = categoryId

      const data = await getBooks(params)

      const list = data?.result?.content || data?.content || []
      setBooks(list)

      setTotalPages(data?.result?.totalPages || data?.totalPages || 0)

    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const fetchRecommendations = async () => {
    try {
      const t = await getTrending()
      const p = await getPopular()

      setTrending(t || [])
      setPopular(p || [])
    } catch (err) {
      console.error(err)
    }
  }

  return (
    <div className="flex min-h-screen bg-slate-100 text-slate-900">

      {/* THANH BEN TRAI */}
      <div className="w-64 shrink-0 border-r border-slate-200 bg-white p-5">
        <h2 className="mb-4 text-lg font-semibold text-slate-900">Thể loại</h2>

        <div className="space-y-2">
          <button
            onClick={() => setCategoryId("")}
            className={`w-full rounded-md px-3 py-2 text-left text-sm font-medium transition ${categoryId === "" ? "bg-blue-600 text-white" : "text-slate-700 hover:bg-slate-100"
              }`}
          >
            Tất cả sách
          </button>

          {categories.map((c) => (
            <button
              key={c.id}
              onClick={() => setCategoryId(c.id)}
              className={`w-full rounded-md px-3 py-2 text-left text-sm font-medium transition ${categoryId === c.id
                ? "bg-blue-600 text-white"
                : "text-slate-700 hover:bg-slate-100"
                }`}
            >
              {c.name}
            </button>
          ))}
        </div>
      </div>

      {/* NOI DUNG CHINH */}
      <div className="flex-1 space-y-5 px-5">

       

        {/* TIM KIEM */}
        <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
          <input
            type="text"
            placeholder="Tìm kiếm sách..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 placeholder:text-slate-400 
            focus:ring-2 sm:flex-1"
          />

          <button
            onClick={() => {
              setPage(0)
              fetchBooks()
            }}
            className="inline-flex items-center justify-center rounded-md bg-blue-600 px-5 py-2 text-sm font-medium text-white transition hover:bg-blue-500"
          >
            Tìm kiếm
          </button>
        </div>

        {/* THINH HANH */}
        {trending.length > 0 && (
          <div className="overflow-hidden rounded-lg border border-slate-200 bg-white p-4">
            <h2 className="mb-3 text-lg font-semibold text-slate-900">Sách thịnh hành 7 ngày qua</h2>
            <div className="flex gap-4 overflow-x-auto pb-1">
              {trending.map((b) => (
                <div
                  key={b.id}
                  onClick={() => navigate(`/books/${b.id}`)}
                  className="min-w-44 cursor-pointer overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm transition hover:-translate-y-0.5 hover:shadow"
                >
                  {b.thumbnailUrl ? (
                    <img src={b.thumbnailUrl} className="h-36 w-full object-cover" />
                  ) : (
                    <div className="flex h-36 items-center justify-center bg-slate-100 text-sm text-slate-500">
                      Không có ảnh
                    </div>
                  )}
                  <div className="p-2.5">
                    <p className="line-clamp-2 text-sm font-semibold text-slate-900">{b.title}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* PHO BIEN */}
        <div className="overflow-hidden rounded-lg border border-slate-200 bg-white p-4">
          <h2 className="mb-3 text-lg font-semibold text-slate-900">Sách phổ biến</h2>
          <div className="flex gap-4 overflow-x-auto pb-1">
            {popular.map((b) => (
              <div
                key={b.id}
                onClick={() => navigate(`/books/${b.id}`)}
                className="min-w-44 cursor-pointer overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm transition hover:-translate-y-0.5 hover:shadow"
              >
                {b.thumbnailUrl ? (
                  <img src={b.thumbnailUrl} className="h-36 w-full object-cover" />
                ) : (
                  <div className="flex h-36 items-center justify-center bg-slate-100 text-sm text-slate-500">
                    Không có ảnh
                  </div>
                )}
                <div className="p-2.5">
                  <p className="line-clamp-2 text-sm font-semibold text-slate-900">{b.title}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        <h2 className="text-lg font-semibold text-slate-900">Sách</h2>

        {/* DANG TAI */}
        {loading && <p className="text-sm text-slate-500">Đang tải sách...</p>}

        {/* RONG */}
        {!loading && books.length === 0 && <p className="text-sm text-slate-500">Không tìm thấy sách</p>}

        {/* DANH SACH */}
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 justify-items-center">
          {books.map((b) => (
            <div
              key={b.id}
              onClick={() => navigate(`/books/${b.id}`)}
              className="w-full max-w-45 cursor-pointer overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm transition hover:-translate-y-0.5 hover:shadow"
            >
              {b.thumbnailUrl ? (
                <img src={b.thumbnailUrl} className="h-36 w-full object-cover" />
              ) : (
                <div className="flex h-36 items-center justify-center bg-slate-100 text-sm text-slate-500">
                  Không có ảnh
                </div>
              )}

              <div className="p-2.5">
                <h3 className="line-clamp-2 text-sm font-semibold text-slate-900">{b.title}</h3>
                <p className="mt-1 text-xs text-slate-500">{b.categoryName}</p>
                <p className="mt-1 text-xs text-slate-600">Số lượng còn: {b.availableCopies}</p>
              </div>
            </div>
          ))}
        </div>

        {/* PHAN TRANG */}
        <div className="flex justify-center items-center gap-4 mt-6">
          <button
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
            className="rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Trang trước
          </button>

          <span className="text-sm text-slate-600">
            Trang {page + 1} / {totalPages}
          </span>

          <button
            disabled={page >= totalPages - 1}
            onClick={() => setPage(page + 1)}
            className="rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Trang sau
          </button>
        </div>

      </div>
    </div>
  )
}

export default BooksPage