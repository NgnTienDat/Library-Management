import { Loader2, Plus, Search } from 'lucide-react'
import { useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useBooks } from '../../hooks/useBooks'

function ManageBooksPage() {
	const navigate = useNavigate()
	const [keywordInput, setKeywordInput] = useState('')
	const [appliedKeyword, setAppliedKeyword] = useState('')
	const [currentPage, setCurrentPage] = useState(0)
	const pageSize = 10

	const queryParams = useMemo(
		() => ({
			keyword: appliedKeyword || undefined,
			page: currentPage,
			size: pageSize,
		}),
		[appliedKeyword, currentPage],
	)

	const booksQuery = useBooks(queryParams)

	const books = booksQuery.data?.content ?? []
	const pageNumber = booksQuery.data?.pageNumber ?? 0
	const totalPages = booksQuery.data?.totalPages ?? 0
	const isFirstPage = booksQuery.data?.first ?? true
	const isLastPage = booksQuery.data?.last ?? true
	const pageButtons = Array.from({ length: totalPages }, (_, index) => index)

	const handleSearchSubmit = (event) => {
		event.preventDefault()
		setAppliedKeyword(keywordInput.trim())
		setCurrentPage(0)
	}

	const handleOpenCreate = () => {
		navigate('/librarian/books/new')
	}

	return (
		<div className='space-y-5'>
			<div className='flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between'>
				<div>
					<h1 className='text-2xl font-semibold text-slate-900'>Quản lý sách</h1>
				</div>

				<button
					type='button'
					onClick={handleOpenCreate}
					className='inline-flex items-center justify-center gap-2 rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-500'
				>
					<Plus size={16} />
					Thêm sách mới
				</button>
			</div>

			<form onSubmit={handleSearchSubmit} className='flex flex-col gap-2 sm:flex-row sm:items-center'>
				<input
					type='text'
					value={keywordInput}
					onChange={(event) => setKeywordInput(event.target.value)}
					placeholder='Nhap tu khoa tim kiem...'
					className='w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 placeholder:text-slate-400 focus:ring-2 sm:max-w-md'
				/>
				<button
					type='submit'
					disabled={booksQuery.isFetching}
					className='inline-flex items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60'
				>
					{booksQuery.isFetching ? <Loader2 size={16} className='animate-spin' /> : <Search size={16} />}
					Tìm kiếm
				</button>
			</form>

			<div className='overflow-hidden rounded-lg border border-slate-200 bg-white'>
				<div className='overflow-x-auto'>
					<table className='min-w-full divide-y divide-slate-200'>
						<thead className='bg-slate-50'>
							<tr>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>STT</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>ID</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>ISBN</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Tiêu đề</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Thể loại</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Trạng thái</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Hành động</th>
							</tr>
						</thead>
						<tbody className='divide-y divide-slate-100'>
							{booksQuery.isLoading ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-sm text-slate-500'>
										<span className='inline-flex items-center gap-2'>
											<Loader2 size={16} className='animate-spin' />
											Đang tải...
										</span>
									</td>
								</tr>
							) : booksQuery.isError ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-sm text-rose-600'>
										Không thể tải danh sách. Vui lòng thử lại
									</td>
								</tr>
							) : books.length === 0 ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-sm text-slate-500'>
										Không tìm thấy sách
									</td>
								</tr>
							) : (
								books.map((book, index) => (
									<tr key={book.id} className='hover:bg-slate-50'>
										<td className='px-4 py-3 text-sm text-slate-700'>
											{pageNumber * pageSize + index + 1}
										</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{book.id}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{book.isbn}</td>
										<td className='px-4 py-3 text-sm font-medium text-slate-900'>{book.title}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{book.categoryName || '-'}</td>
										<td className='px-4 py-3 text-sm'>
											<span
												className={`inline-flex rounded-full px-2.5 py-1 text-xs font-medium ${
													book.active
														? 'bg-emerald-100 text-emerald-700'
														: 'bg-slate-200 text-slate-700'
												}`}
											>
												{book.active ? 'Đang cung cấp' : 'Ngừng cung cấp'}
											</span>
										</td>
										<td className='px-4 py-3 text-sm'>
											<button
												type='button'
												onClick={() => navigate(`/librarian/books/${book.id}`)}
												className='rounded-md bg-slate-900 px-3 py-1.5 text-xs font-medium text-white transition hover:bg-slate-700'
											>
												Xem chi tiết
											</button>
										</td>
									</tr>
								))
							)}
						</tbody>
					</table>
				</div>

				<div className='flex flex-col gap-3 border-t border-slate-200 px-4 py-3 sm:flex-row sm:items-center sm:justify-between'>
					<p className='text-sm text-slate-600'>
						Trang {totalPages === 0 ? 0 : pageNumber + 1}/{totalPages}
					</p>

					<div className='flex flex-wrap items-center gap-2'>
						<button
							type='button'
							onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 0))}
							disabled={booksQuery.isFetching || isFirstPage || totalPages === 0}
							className='rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50'
						>
							Trước 
						</button>

						{pageButtons.map((page) => (
							<button
								key={page}
								type='button'
								onClick={() => setCurrentPage(page)}
								disabled={booksQuery.isFetching}
								className={`rounded-md px-3 py-1.5 text-sm font-medium transition disabled:cursor-not-allowed disabled:opacity-50 ${
									page === pageNumber
										? 'bg-blue-600 text-white'
										: 'border border-slate-300 text-slate-700 hover:bg-slate-50'
								}`}
							>
								{page + 1}
							</button>
						))}

						<button
							type='button'
							onClick={() => setCurrentPage((prev) => prev + 1)}
							disabled={booksQuery.isFetching || isLastPage || totalPages === 0}
							className='rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50'
						>
							Sau
						</button>
					</div>
				</div>
			</div>
		</div>
	)
}

export default ManageBooksPage
