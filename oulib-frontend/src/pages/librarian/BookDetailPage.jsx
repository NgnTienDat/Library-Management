import { ArrowLeft, Loader2 } from 'lucide-react'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { toast } from 'sonner'
import CategorySelectWithCreate from '../../components/common/CategorySelectWithCreate'
import { useBookDetail, useDeleteBook, useReactivateBook, useUpdateBook } from '../../hooks/useBooks'

const DEFAULT_FORM = {
	id: '',
	isbn: '',
	title: '',
	publisher: '',
	numberOfPages: '',
	description: '',
	totalCopies: '',
	categoryId: '',
	authorsText: '',
	thumbnail: null,
}

function splitTextToAuthorList(value) {
	return value
		.split(/[\n,]/)
		.map((item) => item.trim())
		.filter(Boolean)
		.map((name) => ({ name }))
}

function BookDetailPage() {
	const { id } = useParams()
	const navigate = useNavigate()
	const [isEditing, setIsEditing] = useState(false)
	const [form, setForm] = useState(DEFAULT_FORM)

	const bookQuery = useBookDetail(id)
	const updateBookMutation = useUpdateBook({
		onSuccess: () => {
			setIsEditing(false)
		},
	})
	const deleteBookMutation = useDeleteBook()
	const reactivateBookMutation = useReactivateBook()

	const book = bookQuery.data
	const copyBarcodes = useMemo(
		() => (Array.isArray(book?.copies) ? book.copies : []),
		[book?.copies],
	)

	console.log('barcode list:', copyBarcodes)

	useEffect(() => {
		if (!book) {
			return
		}

		setForm({
			id: book.id || '',
			isbn: book.isbn || '',
			title: book.title || '',
			publisher: book.publisher || '',
			numberOfPages: book.numberOfPages ?? '',
			description: book.description || '',
			totalCopies: copyBarcodes.length,
			categoryId: '',
			authorsText: Array.isArray(book.authorNames) ? book.authorNames.join(', ') : '',
			thumbnail: null,
		})
	}, [book, copyBarcodes.length])

	const handleUpdate = (event) => {
		event.preventDefault()

		const totalCopies = Number(form.totalCopies)
		const numberOfPages = Number(form.numberOfPages || 0)

		if (!form.title.trim()) {
			toast.error('Tiêu đề không được để trống')
			return
		}

		if (!Number.isFinite(totalCopies) || totalCopies <= 0) {
			toast.error('Tổng số bản sao phải lớn hơn 0')
			return
		}

		if (!window.confirm('Xác nhận cập nhật thông tin sách?')) {
			return
		}

		const payload = {
			title: form.title,
			publisher: form.publisher,
			numberOfPages,
			description: form.description,
			totalCopies,
			authors: splitTextToAuthorList(form.authorsText),
			thumbnail: form.thumbnail,
		}

		if (form.categoryId.trim()) {
			payload.categoryId = form.categoryId.trim()
		}

		updateBookMutation.mutate({ id, data: payload })
	}

	const isTogglePending = deleteBookMutation.isPending || reactivateBookMutation.isPending

	const handleToggleActive = () => {
		if (book?.active) {
			if (!window.confirm('Bạn chắc chắn muốn ngừng cung cấp sách này?')) {
				return
			}
			deleteBookMutation.mutate(id)
			return
		}

		if (!window.confirm('Bạn muốn cung cấp lại sách này?')) {
			return
		}
		reactivateBookMutation.mutate(id)
	}

	if (bookQuery.isLoading) {
		return (
			<div className='rounded-lg border border-slate-200 bg-white p-8 text-center text-slate-600'>
				<span className='inline-flex items-center gap-2'>
					<Loader2 size={18} className='animate-spin' />
					Đang tải chi tiết sách...
				</span>
			</div>
		)
	}

	if (bookQuery.isError || !book) {
		return (
			<div className='space-y-3 rounded-lg border border-rose-200 bg-rose-50 p-6 text-rose-700'>
				<p>Không thể tải chi tiết sách.</p>
				<div className='flex gap-2'>
					<button
						type='button'
						onClick={() => bookQuery.refetch()}
						className='rounded-md bg-rose-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-rose-500'
					>
						Thử lại
					</button>
					<button
						type='button'
						onClick={() => navigate('/librarian/books')}
						className='rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50'
					>
						Quay lại
					</button>
				</div>
			</div>
		)
	}

	const borrowedCopies = Math.max(copyBarcodes.length - Number(book.availableCopies || 0), 0)

	return (
		<div className='space-y-5'>
			<button
				type='button'
				onClick={() => navigate('/librarian/books')}
				className='inline-flex items-center gap-2 text-sm font-medium text-slate-700 transition hover:text-slate-900'
			>
				<ArrowLeft size={16} />
				Quay về quản lý sách
			</button>

			<h1 className='text-2xl font-semibold text-slate-900'>Chi tiết sách</h1>
			<div>
				<span
					className={`inline-flex rounded-full px-3 py-1 text-xs font-medium ${
						book.active ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-200 text-slate-700'
					}`}
				>
					{book.active ? 'Đang cung cấp' : 'Ngừng cung cấp'}
				</span>
			</div>

			<form onSubmit={handleUpdate} className='space-y-5 rounded-lg border border-slate-200 bg-white p-5'>
				<div className='grid gap-5 lg:grid-cols-[260px,1fr]'>
					<div className='space-y-3'>
						<div className='overflow-hidden rounded-md border border-slate-200 bg-slate-50'>
							{book.thumbnailUrl ? (
								<img src={book.thumbnailUrl} alt={book.title} className='h-80 w-full object-cover' />
							) : (
								<div className='flex h-80 items-center justify-center text-sm text-slate-500'>Không có ảnh bìa</div>
							)}
						</div>

						<label className='block text-sm font-medium text-slate-700'>
							Đường dẫn ảnh bìa
							<input
								type='text'
								value={book.thumbnailUrl || ''}
								disabled
								className='mt-1 w-full rounded-md border border-slate-300 bg-slate-100 px-3 py-2 text-sm text-slate-600'
							/>
						</label>

						<label className='block text-sm font-medium text-slate-700'>
							Đổi ảnh bìa
							<input
								type='file'
								accept='image/*'
								disabled={!isEditing}
								onChange={(event) =>
									setForm((prev) => ({ ...prev, thumbnail: event.target.files?.[0] ?? null }))
								}
								className='mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm disabled:cursor-not-allowed disabled:bg-slate-100'
							/>
						</label>
					</div>

					<div className='grid grid-cols-1 gap-4 sm:grid-cols-2'>
						<label className='text-sm font-medium text-slate-700'>
							ID
							<input
								type='text'
								value={form.id}
								disabled
								className='mt-1 w-full rounded-md border border-slate-300 bg-slate-100 px-3 py-2 text-sm text-slate-600'
							/>
						</label>

						<label className='text-sm font-medium text-slate-700'>
							ISBN
							<input
								type='text'
								value={form.isbn}
								disabled
								className='mt-1 w-full rounded-md border border-slate-300 bg-slate-100 px-3 py-2 text-sm text-slate-600'
							/>
						</label>

						<label className='text-sm font-medium text-slate-700 sm:col-span-2'>
							Tiêu đề
							<input
								type='text'
								value={form.title}
								onChange={(event) => setForm((prev) => ({ ...prev, title: event.target.value }))}
								disabled={!isEditing}
								className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2 disabled:cursor-not-allowed disabled:bg-slate-100'
							/>
						</label>

						<label className='text-sm font-medium text-slate-700'>
							Nhà xuất bản
							<input
								type='text'
								value={form.publisher}
								onChange={(event) => setForm((prev) => ({ ...prev, publisher: event.target.value }))}
								disabled={!isEditing}
								className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2 disabled:cursor-not-allowed disabled:bg-slate-100'
							/>
						</label>

						<label className='text-sm font-medium text-slate-700'>
							Số trang
							<input
								type='number'
								value={form.numberOfPages}
								onChange={(event) => setForm((prev) => ({ ...prev, numberOfPages: event.target.value }))}
								disabled={!isEditing}
								min={0}
								className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2 disabled:cursor-not-allowed disabled:bg-slate-100'
							/>
						</label>

						<div className='sm:col-span-2'>
							<CategorySelectWithCreate
								label='Thể loại'
								value={form.categoryId}
								onChange={(categoryId) => setForm((prev) => ({ ...prev, categoryId }))}
								disabled={!isEditing || updateBookMutation.isPending}
								fallbackLabel={book.categoryName || ''}
							/>
						</div>

						<label className='text-sm font-medium text-slate-700'>
							Tổng số bản sao
							<input
								type='number'
								value={form.totalCopies}
								onChange={(event) => setForm((prev) => ({ ...prev, totalCopies: event.target.value }))}
								disabled={!isEditing}
								min={1}
								className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2 disabled:cursor-not-allowed disabled:bg-slate-100'
							/>
						</label>

						<label className='text-sm font-medium text-slate-700'>
							Trạng thái hoạt động
							<input
								type='text'
								value={book.active ? 'Đang cung cấp' : 'Ngừng cung cấp'}
								disabled
								className='mt-1 w-full rounded-md border border-slate-300 bg-slate-100 px-3 py-2 text-sm text-slate-600'
							/>
						</label>

						<label className='text-sm font-medium text-slate-700'>
							Số bản khả dụng
							<input
								type='number'
								value={book.availableCopies ?? 0}
								disabled
								className='mt-1 w-full rounded-md border border-slate-300 bg-slate-100 px-3 py-2 text-sm text-slate-600'
							/>
						</label>

						<label className='text-sm font-medium text-slate-700'>
							Số bản đang mượn
							<input
								type='number'
								value={borrowedCopies}
								disabled
								className='mt-1 w-full rounded-md border border-slate-300 bg-slate-100 px-3 py-2 text-sm text-slate-600'
							/>
						</label>

						<label className='text-sm font-medium text-slate-700 sm:col-span-2'>
							Tác giả
							<textarea
								value={form.authorsText}
								onChange={(event) => setForm((prev) => ({ ...prev, authorsText: event.target.value }))}
								disabled={!isEditing}
								rows={2}
								className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2 disabled:cursor-not-allowed disabled:bg-slate-100'
							/>
						</label>

						<label className='text-sm font-medium text-slate-700 sm:col-span-2'>
							Mô tả
							<textarea
								value={form.description}
								onChange={(event) => setForm((prev) => ({ ...prev, description: event.target.value }))}
								disabled={!isEditing}
								rows={4}
								className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2 disabled:cursor-not-allowed disabled:bg-slate-100'
							/>
						</label>

						<label className='text-sm font-medium text-slate-700 sm:col-span-2'>
							Bản sao (mã vạch)
							<textarea
								value={copyBarcodes.join('\n')}
								disabled
								rows={6}
								className='mt-1 w-full rounded-md border border-slate-300 bg-slate-100 px-3 py-2 font-mono text-xs text-slate-600'
							/>
						</label>
					</div>
				</div>

				<div className='flex flex-wrap justify-end gap-2 border-t border-slate-200 pt-4'>
					{isEditing ? (
						<>
							<button
								type='button'
								onClick={() => {
									setIsEditing(false)
									setForm((prev) => ({ ...prev, thumbnail: null, categoryId: '' }))
								}}
								className='rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50'
							>
								Hủy
							</button>
							<button
								type='submit'
								disabled={updateBookMutation.isPending}
								className='inline-flex items-center gap-2 rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60'
							>
								{updateBookMutation.isPending ? <Loader2 size={16} className='animate-spin' /> : null}
								Lưu thay đổi
							</button>
						</>
					) : (
						<button
							type='button'
							onClick={() => setIsEditing(true)}
							className='rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-500'
						>
							Chỉnh sửa
						</button>
					)}

					<button
						type='button'
						onClick={handleToggleActive}
						disabled={isTogglePending}
						className={`inline-flex items-center gap-2 rounded-md px-4 py-2 text-sm font-medium text-white transition disabled:cursor-not-allowed disabled:opacity-60 ${
							book.active ? 'bg-rose-600 hover:bg-rose-500' : 'bg-emerald-600 hover:bg-emerald-500'
						}`}
					>
						{isTogglePending ? <Loader2 size={16} className='animate-spin' /> : null}
						{book.active ? 'Ngừng cung cấp' : 'Cung cấp lại'}
					</button>
				</div>
			</form>
		</div>
	)
}

export default BookDetailPage
