import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { getBooks } from "../../api/books.api"
import { getAllCategories } from "../../api/categories.api"

function BooksPage() {
  const navigate = useNavigate()

  const [books, setBooks] = useState([])
  const [categories, setCategories] = useState([])
  const [search, setSearch] = useState("")
  const [categoryId, setCategoryId] = useState("")
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    fetchCategories()
    fetchBooks()
  }, [])

  useEffect(() => {
    fetchBooks()
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
        page: 0,
        size: 12,
      }

      if (search) params.search = search
      if (categoryId) params.categoryId = categoryId

      const data = await getBooks(params)

      const list = data?.result?.content || data?.content || []

      setBooks(list)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
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
            className={`w-full text-left px-3 py-2 rounded-md transition ${
              categoryId === ""
                ? "bg-blue-600 text-white"
                : "hover:bg-slate-100"
            }`}
          >
            All Books
          </button>

          {categories.map((c) => (
            <button
              key={c.id}
              onClick={() => setCategoryId(c.id)}
              className={`w-full text-left px-3 py-2 rounded-md transition ${
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

        {/* HEADER */}
        <h1 className="text-2xl font-bold mb-4">Books Library</h1>

        {/* SEARCH */}
        <div className="flex gap-2 mb-6">
          <input
            type="text"
            placeholder="Search books..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="flex-1 border border-slate-300 px-4 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          <button
            onClick={fetchBooks}
            className="bg-blue-600 text-white px-5 py-2 rounded-md hover:bg-blue-500"
          >
            Search
          </button>
        </div>

        {/* LOADING */}
        {loading && (
          <p className="text-slate-500">Loading books...</p>
        )}

        {/* EMPTY */}
        {!loading && books.length === 0 && (
          <p className="text-slate-500">No books found</p>
        )}

        {/* LIST */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-5">
          {books.map((b) => (
            <div
              key={b.id}
              onClick={() => navigate(`/books/${b.id}`)}
              className="bg-white rounded-lg shadow hover:shadow-lg transition cursor-pointer overflow-hidden"
            >
              {/* IMAGE */}
              {b.thumbnailUrl ? (
                <img
                  src={b.thumbnailUrl}
                  alt={b.title}
                  className="h-40 w-full object-cover"
                />
              ) : (
                <div className="h-40 bg-slate-200 flex items-center justify-center text-sm text-slate-500">
                  No Image
                </div>
              )}

              {/* CONTENT */}
              <div className="p-3">
                <h3 className="font-semibold text-sm line-clamp-2">
                  {b.title}
                </h3>

                <p className="text-xs text-slate-500 mt-1">
                  {b.categoryName || "Unknown"}
                </p>

                <p className="text-xs mt-1">
                  Available: {b.availableCopies ?? 0}
                </p>
              </div>
            </div>
          ))}
        </div>

      </div>
    </div>
  )
}

export default BooksPage