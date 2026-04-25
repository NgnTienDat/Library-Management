import { ArrowLeft, CheckCircle2, Loader2 } from 'lucide-react'
import { useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { verifyBarcode } from '../../api/books.api'
import { useCreateBorrow } from '../../hooks/useBorrowRecords'
import { ROLE_ROUTE_PATHS, ROLES } from '../../utils/constants'

const DEFAULT_FORM = {
	borrowerId: '',
	borrowDuration: '7',
}

const BORROW_DURATION_OPTIONS = ['0', '7', '14', '30']

function createBarcodeField(id) {
	return {
		id,
		value: '',
		verified: false,
		verifying: false,
		bookTitle: '',
	}
}

function getErrorMessage(error) {
	return (
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Có lỗi xảy ra. Vui lòng thử lại.'
	)
}

function NewBorrowPage() {
	const navigate = useNavigate()
	const barcodeIdRef = useRef(2)
	const [form, setForm] = useState(DEFAULT_FORM)
	const [barcodeFields, setBarcodeFields] = useState([createBarcodeField(1)])

	const librarianPaths = ROLE_ROUTE_PATHS[ROLES.LIBRARIAN]

	const createBorrowMutation = useCreateBorrow({
		onSuccess: () => {
			navigate(librarianPaths.borrow)
		},
	})

	const updateBarcodeField = (id, updater) => {
		setBarcodeFields((prev) => prev.map((field) => (field.id === id ? updater(field) : field)))
	}

	const handleBarcodeChange = (id, value) => {
		updateBarcodeField(id, (field) => ({
			...field,
			value,
			verified: false,
			bookTitle: '',
		}))
	}

	const handleAddBarcodeField = () => {
		const nextId = barcodeIdRef.current
		barcodeIdRef.current += 1
		setBarcodeFields((prev) => [...prev, createBarcodeField(nextId)])
	}

	const handleVerifyBarcode = async (id) => {
		const field = barcodeFields.find((item) => item.id === id)
		if (!field) return

		const barcode = field.value.trim()
		if (!barcode) {
			toast.error('Vui lòng nhập barcode trước khi verify')
			return
		}

		const duplicated = barcodeFields.some((item) => item.id !== id && item.value.trim() === barcode)
		if (duplicated) {
			toast.error('Barcode này bị trùng với ô khác')
			return
		}

		updateBarcodeField(id, (current) => ({
			...current,
			value: barcode,
			verifying: true,
			verified: false,
			bookTitle: '',
		}))

		try {
			const result = await verifyBarcode(barcode)

			updateBarcodeField(id, (current) => ({
				...current,
				value: barcode,
				verifying: false,
				verified: true,
				bookTitle: result?.bookTitle || '',
			}))

			toast.success(`Verify thành công barcode ${barcode}`)
		} catch (error) {
			updateBarcodeField(id, (current) => ({
				...current,
				verifying: false,
				verified: false,
				bookTitle: '',
			}))

			toast.error(getErrorMessage(error))
		}
	}

	const handleSubmit = (event) => {
		event.preventDefault()

		const borrowerId = form.borrowerId.trim()
		const barcodes = barcodeFields.map((item) => item.value.trim()).filter(Boolean)

		if (!borrowerId) {
			toast.error('BorrowerId không được để trống')
			return
		}

		if (!BORROW_DURATION_OPTIONS.includes(form.borrowDuration)) {
			toast.error('Thời gian mượn phải là một trong các giá trị: 0, 7, 14, 30 ngày')
			return
		}

		const borrowDuration = Number.parseInt(form.borrowDuration, 10)

		if (!barcodes.length) {
			toast.error('Vui lòng nhập ít nhất 1 barcode')
			return
		}

		if (barcodes.length !== new Set(barcodes).size) {
			toast.error('Danh sách barcode bị trùng')
			return
		}

		const unverifiedField = barcodeFields.find((item) => item.value.trim() && !item.verified)
		if (unverifiedField) {
			toast.error(`Barcode ${unverifiedField.value.trim()} chưa verify`) 
			return
		}

		if (!window.confirm('Xác nhận tạo lượt mượn mới?')) {
			return
		}

		createBorrowMutation.mutate({
			borrowerId,
			borrowDuration,
			barcodes,
		})
	}

	return (
		<div className='space-y-5'>
			<button
				type='button'
				onClick={() => navigate(librarianPaths.borrow)}
				className='inline-flex items-center gap-2 text-sm font-medium text-slate-700 transition hover:text-slate-900'
			>
				<ArrowLeft size={16} />
				Quay lại quản lý mượn trả
			</button>

			<div>
				<h1 className='text-2xl font-semibold text-slate-900'>Thêm lượt mượn mới</h1>
				{/* <p className='mt-1 text-sm text-slate-600'>
					Nhập payload theo BorrowRequest: borrowerId, borrowDuration, barcodes.
				</p> */}
			</div>

			<form onSubmit={handleSubmit} className='space-y-4 rounded-lg border border-slate-200 bg-white p-5'>
				<div className='grid grid-cols-1 gap-4 sm:grid-cols-2'>
					<label className='text-sm font-medium text-slate-700'>
						Borrower ID
						<input
							type='text'
							value={form.borrowerId}
							onChange={(event) => setForm((prev) => ({ ...prev, borrowerId: event.target.value }))}
							className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
							placeholder='Ví dụ: 6fd4f0c7-a77a-4b03-8e95-xxxx'
							required
						/>
					</label>

					<label className='text-sm font-medium text-slate-700'>
						Thời gian mượn (ngày)
						<select
							value={form.borrowDuration}
							onChange={(event) => setForm((prev) => ({ ...prev, borrowDuration: event.target.value }))}
							className='mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
							required
						>
							{BORROW_DURATION_OPTIONS.map((value) => (
								<option key={value} value={value}>
									{value}
								</option>
							))}
						</select>
					</label>
				</div>

				<div className='space-y-3'>
					<p className='text-sm font-medium text-slate-700'>Danh sách barcode</p>

					{barcodeFields.map((field, index) => (
						<div key={field.id} className='rounded-md border border-slate-200 bg-slate-50 p-3'>
							<div className='flex flex-col gap-2 sm:flex-row sm:items-end'>
								<div className='flex-1'>
									<label className='text-xs font-medium uppercase tracking-wide text-slate-500'>
										Barcode {index + 1}
									</label>
									<div className='mt-1 flex items-center gap-2'>
										<input
											type='text'
											value={field.value}
											onChange={(event) => handleBarcodeChange(field.id, event.target.value)}
											className='w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
											placeholder='Nhập barcode'
										/>
										{field.verified ? <CheckCircle2 size={18} className='shrink-0 text-emerald-600' /> : null}
									</div>
								</div>

								<button
									type='button'
									onClick={() => handleVerifyBarcode(field.id)}
									disabled={field.verifying || !field.value.trim() || createBorrowMutation.isPending}
									className='inline-flex items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-60'
								>
									{field.verifying ? <Loader2 size={16} className='animate-spin' /> : null}
									{field.verifying ? 'Đang verify' : 'Verify'}
								</button>
							</div>

							{field.verified ? (
								<p className='mt-2 text-xs font-medium text-emerald-700'>
									Đã xác thực{field.bookTitle ? ` - ${field.bookTitle}` : ''}
								</p>
							) : null}
						</div>
					))}

					<button
						type='button'
						onClick={handleAddBarcodeField}
						disabled={createBorrowMutation.isPending}
						className='rounded-md border border-dashed border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60'
					>
						Thêm
					</button>
				</div>

				<div className='flex justify-end gap-2 pt-2'>
					<button
						type='button'
						onClick={() => navigate(librarianPaths.borrow)}
						className='rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50'
					>
						Hủy
					</button>
					<button
						type='submit'
						disabled={createBorrowMutation.isPending}
						className='inline-flex items-center gap-2 rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60'
					>
						{createBorrowMutation.isPending ? <Loader2 size={16} className='animate-spin' /> : null}
						Tạo lượt mượn
					</button>
				</div>
			</form>
		</div>
	)
}

export default NewBorrowPage
