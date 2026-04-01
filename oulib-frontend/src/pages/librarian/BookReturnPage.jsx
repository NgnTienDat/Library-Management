import { ArrowLeft, Loader2 } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useBorrowRecordDetail, useReturnBorrow } from '../../hooks/useBorrowRecords'
import { ROLE_ROUTE_PATHS, ROLES } from '../../utils/constants'
import { formatDate, formatDateTime } from '../../utils/datetime'

function getStatusBadgeClass(status) {
	if (status === 'BORROWING') return 'bg-amber-100 text-amber-700'
	if (status === 'RETURNED') return 'bg-emerald-100 text-emerald-700'
	if (status === 'OVERDUE') return 'bg-rose-100 text-rose-700'
	return 'bg-slate-200 text-slate-700'
}

function BookReturnPage() {
	const navigate = useNavigate()
	const { recordId } = useParams()
	const librarianPaths = ROLE_ROUTE_PATHS[ROLES.LIBRARIAN]

	const detailQuery = useBorrowRecordDetail(recordId)
	const returnMutation = useReturnBorrow({
		onSuccess: () => {
			navigate(librarianPaths.borrow)
		},
	})

	const record = detailQuery.data
	const canReturn = record?.status === 'BORROWING'

	const handleReturnBook = () => {
		if (!record?.barcode) return
		if (!canReturn) return

		if (!window.confirm('Xác nhận trả sách cho lượt mượn này?')) {
			return
		}

		returnMutation.mutate({
			barcodes: [record.barcode],
		})
	}

	if (detailQuery.isLoading) {
		return (
			<div className='rounded-lg border border-slate-200 bg-white p-8 text-center text-slate-600'>
				<span className='inline-flex items-center gap-2'>
					<Loader2 size={18} className='animate-spin' />
					Đang tải chi tiết lượt mượn...
				</span>
			</div>
		)
	}

	if (detailQuery.isError || !record) {
		return (
			<div className='space-y-3 rounded-lg border border-rose-200 bg-rose-50 p-6 text-rose-700'>
				<p>Không thể tải thông tin lượt mượn.</p>
				<div className='flex gap-2'>
					<button
						type='button'
						onClick={() => detailQuery.refetch()}
						className='rounded-md bg-rose-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-rose-500'
					>
						Thử lại
					</button>
					<button
						type='button'
						onClick={() => navigate(librarianPaths.borrow)}
						className='rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50'
					>
						Quay lại
					</button>
				</div>
			</div>
		)
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
				<h1 className='text-2xl font-semibold text-slate-900'>Trả sách</h1>
				<p className='mt-1 text-sm text-slate-600'>
					Xác nhận thông tin lượt mượn trước khi thực hiện trả sách.
				</p>
			</div>

			<div className='space-y-4 rounded-lg border border-slate-200 bg-white p-5'>
				<div className='grid grid-cols-1 gap-4 sm:grid-cols-2'>
					<div>
						<p className='text-xs uppercase tracking-wide text-slate-500'>Record ID</p>
						<p className='mt-1 break-all text-sm font-medium text-slate-900'>{record.id || '-'}</p>
					</div>
					<div>
						<p className='text-xs uppercase tracking-wide text-slate-500'>Trạng thái</p>
						<div className='mt-1'>
							<span
								className={`inline-flex rounded-full px-2.5 py-1 text-xs font-medium ${getStatusBadgeClass(record.status)}`}
							>
								{record.status || '-'}
							</span>
						</div>
					</div>

					<div>
						<p className='text-xs uppercase tracking-wide text-slate-500'>Tên người mượn</p>
						<p className='mt-1 text-sm font-medium text-slate-900'>{record.borrowerFullName || '-'}</p>
					</div>
					<div>
						<p className='text-xs uppercase tracking-wide text-slate-500'>Email</p>
						<p className='mt-1 break-all text-sm text-slate-700'>{record.borrowerEmail || '-'}</p>
					</div>

					<div>
						<p className='text-xs uppercase tracking-wide text-slate-500'>ID người mượn</p>
						<p className='mt-1 break-all text-sm text-slate-700'>{record.borrowerId || '-'}</p>
					</div>
					<div>
						<p className='text-xs uppercase tracking-wide text-slate-500'>Barcode</p>
						<p className='mt-1 text-sm text-slate-700'>{record.barcode || '-'}</p>
					</div>

					<div>
						<p className='text-xs uppercase tracking-wide text-slate-500'>Ngày mượn (createdAt)</p>
						<p className='mt-1 text-sm text-slate-700'>{formatDateTime(record.borrowDate) || '-'}</p>
					</div>
					<div>
						<p className='text-xs uppercase tracking-wide text-slate-500'>Hạn trả</p>
						<p className='mt-1 text-sm text-slate-700'>{formatDate(record.dueDate) || '-'}</p>
					</div>
				</div>

				<div className='flex justify-end gap-2 border-t border-slate-200 pt-4'>
					<button
						type='button'
						onClick={() => navigate(librarianPaths.borrow)}
						className='rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50'
					>
						Hủy
					</button>
					<button
						type='button'
						onClick={handleReturnBook}
						disabled={!canReturn || returnMutation.isPending}
						className='inline-flex items-center gap-2 rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60'
					>
						{returnMutation.isPending ? <Loader2 size={16} className='animate-spin' /> : null}
						Trả sách
					</button>
				</div>
			</div>
		</div>
	)
}

export default BookReturnPage
