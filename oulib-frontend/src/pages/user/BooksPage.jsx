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

  // PAGINATION
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  // RECOMMEND
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

      setTrending(t?.result || [])
      setPopular(p?.result || [])
    } catch (err) {
      console.error(err)
    }
  }

  return (
    <div className="flex min-h-screen bg-slate-100">

      {/* SIDEBAR */}
      <div className="w-64 bg-white border-r p-5">
        <h2 className="text-lg font-bold mb-4">Categories</h2>

        <div className="space-y-2">
          <button
            onClick={() => setCategoryId("")}
            className={`w-full text-left px-3 py-2 rounded-md ${
              categoryId === "" ? "bg-blue-600 text-white" : "hover:bg-slate-100"
            }`}
          >
            All Books
          </button>

          {categories.map((c) => (
            <button
              key={c.id}
              onClick={() => setCategoryId(c.id)}
              className={`w-full text-left px-3 py-2 rounded-md ${
                categoryId === c.id
                  ? "bg-blue-600 text-white"
                  : "hover:bg-slate-100"
              }`}
            >
              {c.name}
            </button>
          ))}
        </div>
      </div>

      {/* MAIN */}
      <div className="flex-1 p-6">

        <h1 className="text-2xl font-bold mb-4">Books Library</h1>

        {/* SEARCH */}
        <div className="flex gap-2 mb-6">
          <input
            type="text"
            placeholder="Search books..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="flex-1 border px-4 py-2 rounded-md"
          />

          <button
            onClick={() => {
              setPage(0)
              fetchBooks()
            }}
            className="bg-blue-600 text-white px-5 py-2 rounded-md"
          >
            Search
          </button>
        </div>

        {/* TRENDING */}
        <h2 className="text-lg font-bold mb-2"> Sách trending</h2>
        <div className="flex gap-4 overflow-x-auto mb-6">
          {trending.map((b) => (
            <div
              key={b.id}
              onClick={() => navigate(`/books/${b.id}`)}
              className="min-w-[150px] cursor-pointer"
            >
              {b.thumbnailUrl ? (
                <img src={b.thumbnailUrl} className="h-32 w-full object-cover rounded" />
              ) : (
                <div className="h-32 bg-slate-200 flex items-center justify-center text-xs">
                  No Image
                </div>
              )}
              <p className="text-sm mt-1 line-clamp-2">{b.title}</p>
            </div>
          ))}
        </div>

        {/* POPULAR */}
        <h2 className="text-lg font-bold mb-2"> Sách phổ biến</h2>
        <div className="flex gap-4 overflow-x-auto mb-6">
          {popular.map((b) => (
            <div
              key={b.id}
              onClick={() => navigate(`/books/${b.id}`)}
              className="min-w-[150px] cursor-pointer"
            >
              {b.thumbnailUrl ? (
                <img src={b.thumbnailUrl} className="h-32 w-full object-cover rounded" />
              ) : (
                <div className="h-32 bg-slate-200 flex items-center justify-center text-xs">
                  No Image
                </div>
              )}
              <p className="text-sm mt-1 line-clamp-2">{b.title}</p>
            </div>
          ))}
        </div>

        {/* LOADING */}
        {loading && <p>Loading books...</p>}

        {/* EMPTY */}
        {!loading && books.length === 0 && <p>No books found</p>}

        {/* LIST */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-5">
          {books.map((b) => (
            <div
              key={b.id}
              onClick={() => navigate(`/books/${b.id}`)}
              className="bg-white rounded-lg shadow cursor-pointer"
            >
              {b.thumbnailUrl ? (
                <img src={b.thumbnailUrl} className="h-40 w-full object-cover" />
              ) : (
                <div className="h-40 bg-slate-200 flex items-center justify-center">
                  No Image
                </div>
              )}

              <div className="p-3">
                <h3 className="text-sm font-semibold">{b.title}</h3>
                <p className="text-xs text-gray-500">{b.categoryName}</p>
                <p className="text-xs">Available: {b.availableCopies}</p>
              </div>
            </div>
          ))}
        </div>

        {/* PAGINATION */}
        <div className="flex justify-center items-center gap-4 mt-6">
          <button
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
            className="px-4 py-2 bg-slate-200 rounded"
          >
            Trang trước
          </button>

          <span>
            Trang {page + 1} / {totalPages}
          </span>

          <button
            disabled={page >= totalPages - 1}
            onClick={() => setPage(page + 1)}
            className="px-4 py-2 bg-slate-200 rounded"
          >
            Trang sau
          </button>
        </div>

      </div>
    </div>
  )
}

export default BooksPage