import { ArrowLeft, Loader2 } from 'lucide-react'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import CategorySelectWithCreate from '../../components/common/CategorySelectWithCreate'
import { useCreateBook } from '../../hooks/useBooks'

const DEFAULT_CREATE_FORM = {
	isbn: '',
	title: '',
	publisher: '',
	numberOfPages: '',
	description: '',
	totalCopies: '',
	categoryId: '',
	copyBarcodesText: '',
	authorNamesText: '',
	thumbnail: null,
}

function splitTextToList(value) {
	return value
		.split(/[\n,]/)
		.map((item) => item.trim())
		.filter(Boolean)
}

function AddNewBookPage() {
	const navigate = useNavigate()
	const [form, setForm] = useState(DEFAULT_CREATE_FORM)

	const createBookMutation = useCreateBook({
		onSuccess: () => {
			navigate('/librarian/books')
		},
	})

	const handleSubmit = (event) => {
		event.preventDefault()

		const totalCopies = Number(form.totalCopies)
		const numberOfPages = Number(form.numberOfPages || 0)
		const copyBarcodes = splitTextToList(form.copyBarcodesText)
		const authorNames = splitTextToList(form.authorNamesText)

		if (!form.isbn.trim() || !form.title.trim()) {
			toast.error('ISBN và tiêu đề không được để trống')
			return
		}

		if (!form.categoryId.trim()) {
			toast.error('Vui lòng chọn thể loại')
			return
		}

		if (!Number.isFinite(totalCopies) || totalCopies <= 0) {
			toast.error('Tổng số bản sao phải lớn hơn 0')
			return
		}

		if (copyBarcodes.length !== totalCopies) {
			toast.error('Số lượng barcode phải bằng tổng số bản sao')
			return
		}

		if (!window.confirm('Xác nhận tạo sách mới?')) {
			return
		}

		createBookMutation.mutate({
			isbn: form.isbn,
			title: form.title,
			publisher: form.publisher,
			numberOfPages,
			description: form.description,
			totalCopies,
			categoryId: form.categoryId,
			copyBarcodes,
			authors: authorNames.map((name) => ({ name })),
			thumbnail: form.thumbnail,
		})
	}

	return (
		<div className='space-y-5'>
			<button
				type='button'
				onClick={() => navigate('/librarian/books')}
				className='inline-flex items-center gap-2 text-sm font-medium text-slate-700 transition hover:text-slate-900'
			>
				<ArrowLeft size={16} />
				Quay lại danh sách sách
			</button>

			<div>
				<h1 className='text-2xl font-semibold text-slate-900'>Thêm sách mới</h1>
				<p className='mt-1 text-sm text-slate-600'>Nhập thông tin sách theo đúng payload backend.</p>
			</div>

			<form onSubmit={handleSubmit} className='space-y-4 rounded-lg border border-slate-200 bg-white p-5'>
				<div className='grid grid-cols-1 gap-4 sm:grid-cols-2'>
					<label className='text-sm font-medium text-slate-700'>
						ISBN
						<input
							type='text'
							value={form.isbn}
							onChange={(event) => setForm((prev) => ({ ...prev, isbn: event.target.value }))}
							className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
							required
						/>
					</label>

					<label className='text-sm font-medium text-slate-700'>
						Tiêu đề
						<input
							type='text'
							value={form.title}
							onChange={(event) => setForm((prev) => ({ ...prev, title: event.target.value }))}
							className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
							required
						/>
					</label>

					<label className='text-sm font-medium text-slate-700'>
						Nhà xuất bản
						<input
							type='text'
							value={form.publisher}
							onChange={(event) => setForm((prev) => ({ ...prev, publisher: event.target.value }))}
							className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
						/>
					</label>

					<label className='text-sm font-medium text-slate-700'>
						Số trang
						<input
							type='number'
							value={form.numberOfPages}
							onChange={(event) => setForm((prev) => ({ ...prev, numberOfPages: event.target.value }))}
							className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
							min={0}
						/>
					</label>

					<label className='text-sm font-medium text-slate-700'>
						Tổng số bản sao
						<input
							type='number'
							value={form.totalCopies}
							onChange={(event) => setForm((prev) => ({ ...prev, totalCopies: event.target.value }))}
							className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
							min={1}
							required
						/>
					</label>

					<div className='sm:col-span-2'>
						<CategorySelectWithCreate
							label='Thể loại'
							value={form.categoryId}
							onChange={(categoryId) => setForm((prev) => ({ ...prev, categoryId }))}
							disabled={createBookMutation.isPending}
						/>
					</div>
				</div>

				<label className='block text-sm font-medium text-slate-700'>
					Mô tả
					<textarea
						value={form.description}
						onChange={(event) => setForm((prev) => ({ ...prev, description: event.target.value }))}
						rows={3}
						className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
					/>
				</label>

				<label className='block text-sm font-medium text-slate-700'>
					Danh sách barcode (tách bởi dấu phẩy hoặc xuống dòng)
					<textarea
						value={form.copyBarcodesText}
						onChange={(event) => setForm((prev) => ({ ...prev, copyBarcodesText: event.target.value }))}
						rows={3}
						className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
						required
					/>
				</label>

				<label className='block text-sm font-medium text-slate-700'>
					Tác giả mới (tên, tách bởi dấu phẩy hoặc xuống dòng)
					<textarea
						value={form.authorNamesText}
						onChange={(event) => setForm((prev) => ({ ...prev, authorNamesText: event.target.value }))}
						rows={2}
						className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
					/>
				</label>

				<label className='block text-sm font-medium text-slate-700'>
					Ảnh bìa
					<input
						type='file'
						accept='image/*'
						onChange={(event) => setForm((prev) => ({ ...prev, thumbnail: event.target.files?.[0] ?? null }))}
						className='mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm'
					/>
				</label>

				<div className='flex justify-end gap-2 pt-2'>
					<button
						type='button'
						onClick={() => navigate('/librarian/books')}
						className='rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50'
					>
						Hủy
					</button>
					<button
						type='submit'
						disabled={createBookMutation.isPending}
						className='inline-flex items-center gap-2 rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60'
					>
						{createBookMutation.isPending ? <Loader2 size={16} className='animate-spin' /> : null}
						Lưu
					</button>
				</div>
			</form>
		</div>
	)
}

export default AddNewBookPage
