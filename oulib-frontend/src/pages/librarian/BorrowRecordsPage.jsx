import { Loader2, Plus, Search } from 'lucide-react'
import { useMemo, useState } from 'react'
import { toast } from 'sonner'
import { useBorrowRecords } from '../../hooks/useBorrowRecords'

const STATUS_OPTIONS = [
	{ label: 'Tất cả trạng thái', value: '' },
	{ label: 'BORROWING', value: 'BORROWING' },
	{ label: 'RETURNED', value: 'RETURNED' },
	{ label: 'OVERDUE', value: 'OVERDUE' },
]

function formatDate(value) {
	if (!value) return '-'
	const parsedDate = new Date(value)
	if (Number.isNaN(parsedDate.getTime())) return value
	return parsedDate.toLocaleDateString('vi-VN')
}

function formatDateTime(value) {
	if (!value) return '-'
	const parsedDate = new Date(value)
	if (Number.isNaN(parsedDate.getTime())) return value
	return parsedDate.toLocaleString('vi-VN')
}

function getStatusBadgeClass(status) {
	if (status === 'BORROWING') return 'bg-amber-100 text-amber-700'
	if (status === 'RETURNED') return 'bg-emerald-100 text-emerald-700'
	if (status === 'OVERDUE') return 'bg-rose-100 text-rose-700'
	return 'bg-slate-200 text-slate-700'
}

function BorrowRecordsPage() {
	const [borrowerIdInput, setBorrowerIdInput] = useState('')
	const [statusInput, setStatusInput] = useState('')
	const [appliedBorrowerId, setAppliedBorrowerId] = useState('')
	const [appliedStatus, setAppliedStatus] = useState('')
	const [currentPage, setCurrentPage] = useState(0)
	const pageSize = 10

	const queryParams = useMemo(
		() => ({
			borrowerId: appliedBorrowerId || undefined,
			status: appliedStatus || undefined,
			page: currentPage,
			size: pageSize,
		}),
		[appliedBorrowerId, appliedStatus, currentPage],
	)

	const recordsQuery = useBorrowRecords(queryParams)

	const records = recordsQuery.data?.content ?? []
	const pageNumber = recordsQuery.data?.pageNumber ?? 0
	const totalPages = recordsQuery.data?.totalPages ?? 0
	const isFirstPage = recordsQuery.data?.first ?? true
	const isLastPage = recordsQuery.data?.last ?? true
	const pageButtons = Array.from({ length: totalPages }, (_, index) => index)

	const handleSearchSubmit = (event) => {
		event.preventDefault()
		setAppliedBorrowerId(borrowerIdInput.trim())
		setAppliedStatus(statusInput)
		setCurrentPage(0)
	}

	const handleAddBorrow = () => {
		toast.info('Chức năng thêm lượt mượn sẽ được cập nhật sau')
	}

	return (
		<div className='space-y-5'>
			<div className='flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between'>
				<div>
					<h1 className='text-2xl font-semibold text-slate-900'>Quản lý mượn trả sách</h1>
				</div>

				<button
					type='button'
					onClick={handleAddBorrow}
					className='inline-flex items-center justify-center gap-2 rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-500'
				>
					<Plus size={16} />
					Thêm lượt mượn
				</button>
			</div>

			<form onSubmit={handleSearchSubmit} className='flex flex-col gap-2 lg:flex-row lg:items-center'>
				<input
					type='text'
					value={borrowerIdInput}
					onChange={(event) => setBorrowerIdInput(event.target.value)}
					placeholder='Nhập borrowerId để tìm kiếm...'
					className='w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 placeholder:text-slate-400 focus:ring-2 lg:max-w-md'
				/>

				<select
					value={statusInput}
					onChange={(event) => setStatusInput(event.target.value)}
					className='w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2 lg:max-w-[220px]'
				>
					{STATUS_OPTIONS.map((status) => (
						<option key={status.value || 'ALL'} value={status.value}>
							{status.label}
						</option>
					))}
				</select>

				<button
					type='submit'
					disabled={recordsQuery.isFetching}
					className='inline-flex items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60'
				>
					{recordsQuery.isFetching ? <Loader2 size={16} className='animate-spin' /> : <Search size={16} />}
					Tìm kiếm
				</button>
			</form>

			<div className='overflow-hidden rounded-lg border border-slate-200 bg-white'>
				<div className='overflow-x-auto'>
					<table className='min-w-full divide-y divide-slate-200'>
						<thead className='bg-slate-50'>
							<tr>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>STT</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Record ID</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>ID người mượn</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Barcode</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Ngày mượn</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Hạn trả</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Ngày trả</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Trạng thái</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Action</th>
							</tr>
						</thead>

						<tbody className='divide-y divide-slate-100'>
							{recordsQuery.isLoading ? (
								<tr>
									<td colSpan={9} className='px-4 py-8 text-center text-sm text-slate-500'>
										<span className='inline-flex items-center gap-2'>
											<Loader2 size={16} className='animate-spin' />
											Đang tải...
										</span>
									</td>
								</tr>
							) : recordsQuery.isError ? (
								<tr>
									<td colSpan={9} className='px-4 py-8 text-center text-sm text-rose-600'>
										Không thể tải danh sách. Vui lòng thử lại
									</td>
								</tr>
							) : records.length === 0 ? (
								<tr>
									<td colSpan={9} className='px-4 py-8 text-center text-sm text-slate-500'>
										Không có lượt mượn/trả phù hợp
									</td>
								</tr>
							) : (
								records.map((record, index) => (
									<tr key={record.id} className='hover:bg-slate-50'>
										<td className='px-4 py-3 text-sm text-slate-700'>
											{pageNumber * pageSize + index + 1}
										</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{record.id}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{record.borrowerId || '-'}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{record.barcode || '-'}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{formatDate(record.borrowDate)}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{formatDateTime(record.dueDate)}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{formatDate(record.returnDate)}</td>
										<td className='px-4 py-3 text-sm'>
											<span
												className={`inline-flex rounded-full px-2.5 py-1 text-xs font-medium ${getStatusBadgeClass(record.status)}`}
											>
												{record.status}
											</span>
										</td>
										<td className='px-4 py-3 text-sm'>
											<button
												type='button'
												disabled
												className='rounded-md bg-slate-400 px-3 py-1.5 text-xs font-medium text-white disabled:cursor-not-allowed disabled:opacity-70'
											>
												Trả sách
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
							disabled={recordsQuery.isFetching || isFirstPage || totalPages === 0}
							className='rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50'
						>
							Trước
						</button>

						{pageButtons.map((page) => (
							<button
								key={page}
								type='button'
								onClick={() => setCurrentPage(page)}
								disabled={recordsQuery.isFetching}
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
							disabled={recordsQuery.isFetching || isLastPage || totalPages === 0}
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

export default BorrowRecordsPage
