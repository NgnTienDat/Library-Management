import { useParams } from 'react-router-dom'

function BookDetailPage() {
	const { bookId } = useParams()

	return (
		<div>
			<h1 className='text-2xl font-semibold text-slate-900'>Book Detail</h1>
			<p className='mt-2 text-sm text-slate-600'>Book ID: {bookId}</p>
		</div>
	)
}

export default BookDetailPage
