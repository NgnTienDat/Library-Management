import { useEffect, useState } from "react"
import { useParams, useNavigate } from "react-router-dom"
import { getBookById } from "../../api/books.api"

function BookDetailPage() {
  const { bookId } = useParams() // FIX QUAN TRỌNG
  const navigate = useNavigate()

  const [book, setBook] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchBook()
  }, [bookId])

  const fetchBook = async () => {
    try {
      setLoading(true)

      console.log("Book ID:", bookId) // debug

      if (!bookId) return

      const data = await getBookById(bookId)

      console.log("API response:", data)

      const result = data?.result || data

      setBook(result)
    } catch (err) {
      console.error("Error fetch book:", err)
    } finally {
      setLoading(false)
    }
  }

  // LOADING
  if (loading) {
    return (
      <div className="p-6 text-center text-slate-500">
        Loading book detail...
      </div>
    )
  }

  // NOT FOUND
  if (!book) {
    return (
      <div className="p-6 text-center text-red-500">
        Book not found
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-slate-100 p-6">
      {/* BACK */}
      <button
        onClick={() => navigate(-1)}
        className="mb-4 text-blue-600 hover:underline"
      >
        ← Back
      </button>

      <div className="bg-white rounded-xl shadow p-6 grid md:grid-cols-2 gap-6">
        
        {/* IMAGE */}
        <div>
          {book.thumbnailUrl ? (
            <img
              src={book.thumbnailUrl}
              alt={book.title}
              className="w-full h-80 object-cover rounded-lg"
            />
          ) : (
            <div className="w-full h-80 bg-slate-200 flex items-center justify-center rounded-lg">
              No Image
            </div>
          )}
        </div>

        {/* INFO */}
        <div>
          <h1 className="text-2xl font-bold mb-2">
            {book.title}
          </h1>

          <p className="text-slate-500 mb-2">
            Category: {book.categoryName || "Unknown"}
          </p>

          <p className="mb-2">
            <strong>Author:</strong>{" "}
            {book.authorNames?.join(", ") || "Unknown"}
          </p>

          <p className="mb-2">
            <strong>Publisher:</strong> {book.publisher}
          </p>

          <p className="mb-2">
            <strong>Pages:</strong> {book.numberOfPages}
          </p>

          <p className="mb-2">
            <strong>Available:</strong> {book.availableCopies}
          </p>

          <p className="mb-4">
            <strong>Status:</strong>{" "}
            {book.active ? "Có sẵn" : "Không có sẵn"}
          </p>

          <div className="border-t pt-4">
            <p className="text-sm text-slate-700">
              {book.description || "No description"}
            </p>
          </div>

          {/* BUTTON */}
          <button
            className="mt-6 w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-500"
          >
            Borrow Book
          </button>
        </div>
      </div>
    </div>
  )
}

export default BookDetailPage