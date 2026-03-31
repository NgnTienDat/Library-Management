import { ArrowLeft, Loader2 } from 'lucide-react'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { useCreateBorrow } from '../../hooks/useBorrowRecords'
import { ROLE_ROUTE_PATHS, ROLES } from '../../utils/constants'

const DEFAULT_FORM = {
	borrowerId: '',
	borrowDuration: '7',
	barcodesText: '',
}

const BORROW_DURATION_OPTIONS = ['0', '7', '14', '30']

function splitToBarcodeList(value) {
	return value
		.split(/[\n,]/)
		.map((item) => item.trim())
		.filter(Boolean)
}

function NewBorrowPage() {
	const navigate = useNavigate()
	const [form, setForm] = useState(DEFAULT_FORM)

	const librarianPaths = ROLE_ROUTE_PATHS[ROLES.LIBRARIAN]

	const createBorrowMutation = useCreateBorrow({
		onSuccess: () => {
			navigate(librarianPaths.borrow)
		},
	})

	const handleSubmit = (event) => {
		event.preventDefault()

		const borrowerId = form.borrowerId.trim()
		const barcodes = splitToBarcodeList(form.barcodesText)

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
				<p className='mt-1 text-sm text-slate-600'>
					Nhập payload theo BorrowRequest: borrowerId, borrowDuration, barcodes.
				</p>
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

				<label className='block text-sm font-medium text-slate-700'>
					Danh sách barcode (tách bởi dấu phẩy hoặc xuống dòng)
					<textarea
						rows={4}
						value={form.barcodesText}
						onChange={(event) => setForm((prev) => ({ ...prev, barcodesText: event.target.value }))}
						className='mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2'
						placeholder='BC-001, BC-002 hoặc mỗi dòng một barcode'
						required
					/>
				</label>

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
