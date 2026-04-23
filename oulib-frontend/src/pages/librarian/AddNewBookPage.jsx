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
	categoryId: '',
	copyBarcodes: [''],
	authorNames: [''],
	thumbnail: null,
}

function countFilledItems(items) {
	return items.map((item) => item.trim()).filter(Boolean).length
}

function AddNewBookPage() {
	const navigate = useNavigate()
	const [form, setForm] = useState(DEFAULT_CREATE_FORM)

	const createBookMutation = useCreateBook({
		onSuccess: () => {
			navigate('/librarian/books')
		},
	})

	const handleBarcodeChange = (index, value) => {
		setForm((prev) => ({
			...prev,
			copyBarcodes: prev.copyBarcodes.map((barcode, itemIndex) =>
				itemIndex === index ? value : barcode,
			),
		}))
	}

	const handleAddBarcodeInput = () => {
		setForm((prev) => ({
			...prev,
			copyBarcodes: [...prev.copyBarcodes, ''],
		}))
	}

	const handleRemoveBarcodeInput = (index) => {
		setForm((prev) => ({
			...prev,
			copyBarcodes: prev.copyBarcodes.filter((_, itemIndex) => itemIndex !== index),
		}))
	}

	const handleAuthorChange = (index, value) => {
		setForm((prev) => ({
			...prev,
			authorNames: prev.authorNames.map((author, itemIndex) =>
				itemIndex === index ? value : author,
			),
		}))
	}

	const handleAddAuthorInput = () => {
		setForm((prev) => ({
			...prev,
			authorNames: [...prev.authorNames, ''],
		}))
	}

	const handleRemoveAuthorInput = (index) => {
		setForm((prev) => ({
			...prev,
			authorNames: prev.authorNames.filter((_, itemIndex) => itemIndex !== index),
		}))
	}

	const handleSubmit = (event) => {
		event.preventDefault()

		const numberOfPages = Number(form.numberOfPages || 0)
		const copyBarcodes = form.copyBarcodes.map((barcode) => barcode.trim()).filter(Boolean)
		const authorNames = form.authorNames.map((name) => name.trim()).filter(Boolean)

		if (!form.isbn.trim() || !form.title.trim()) {
			toast.error('ISBN và tiêu đề không được để trống')
			return
		}

		if (!form.categoryId.trim()) {
			toast.error('Vui lòng chọn thể loại')
			return
		}

		if (copyBarcodes.length === 0) {
			toast.error('Cần nhập ít nhất 1 barcode')
			return
		}

		if (copyBarcodes.length !== new Set(copyBarcodes).size) {
			toast.error('Danh sách barcode không được trùng nhau')
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

				<div className='space-y-2'>
					<div className='flex items-center justify-between gap-2'>
						<p className='text-sm font-medium text-slate-700'>Danh sách barcode</p>
						<p className='text-xs text-slate-500'>
							Tổng bản sao sẽ được tính tự động: {countFilledItems(form.copyBarcodes)}
						</p>
					</div>
					{form.copyBarcodes.map((barcode, index) => (
						<div key={`barcode-${index}`} className='flex items-center gap-2'>
							<input
								type='text'
								value={barcode}
								onChange={(event) => handleBarcodeChange(index, event.target.value)}
								placeholder={`Nhập barcode #${index + 1}`}
								className='w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
								required={index === 0}
							/>
							{index === form.copyBarcodes.length - 1 ? (
								<button
									type='button'
									onClick={handleAddBarcodeInput}
									className='shrink-0 rounded-md border border-blue-200 bg-blue-50 px-3 py-2 text-xs font-medium text-blue-700 transition hover:bg-blue-100'
								>
									Thêm barcode/bản sao
								</button>
							) : (
								<button
									type='button'
									onClick={() => handleRemoveBarcodeInput(index)}
									className='shrink-0 rounded-md border border-rose-200 bg-rose-50 px-3 py-2 text-xs font-medium text-rose-700 transition hover:bg-rose-100'
								>
									Xóa
								</button>
							)}
						</div>
					))}
				</div>

				<div className='space-y-2'>
					<p className='text-sm font-medium text-slate-700'>Danh sách tác giả mới</p>
					{form.authorNames.map((authorName, index) => (
						<div key={`author-${index}`} className='flex items-center gap-2'>
							<input
								type='text'
								value={authorName}
								onChange={(event) => handleAuthorChange(index, event.target.value)}
								placeholder={`Nhập tác giả #${index + 1}`}
								className='w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
							/>
							{index === form.authorNames.length - 1 ? (
								<button
									type='button'
									onClick={handleAddAuthorInput}
									className='shrink-0 rounded-md border border-blue-200 bg-blue-50 px-3 py-2 text-xs font-medium text-blue-700 transition hover:bg-blue-100'
								>
									Thêm tác giả
								</button>
							) : (
								<button
									type='button'
									onClick={() => handleRemoveAuthorInput(index)}
									className='shrink-0 rounded-md border border-rose-200 bg-rose-50 px-3 py-2 text-xs font-medium text-rose-700 transition hover:bg-rose-100'
								>
									Xóa
								</button>
							)}
						</div>
					))}
				</div>

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
