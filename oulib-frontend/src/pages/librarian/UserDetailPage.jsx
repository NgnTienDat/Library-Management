import { ArrowLeft, Loader2, RefreshCw } from 'lucide-react'
import { useMemo, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { useUserBorrowingHistory } from '../../hooks/useBorrowRecords'
import { ROLE_ROUTE_PATHS, ROLES } from '../../utils/constants'
import { formatDate, formatDateTime } from '../../utils/datetime'

const librarianPaths = ROLE_ROUTE_PATHS[ROLES.LIBRARIAN]

const STATUS_OPTIONS = [
	{ label: 'Tất cả trạng thái', value: '' },
	{ label: 'BORROWING', value: 'BORROWING' },
	{ label: 'RETURNED', value: 'RETURNED' },
	{ label: 'OVERDUE', value: 'OVERDUE' },
]

function getStatusBadgeClass(status) {
	if (status === 'BORROWING') return 'bg-amber-100 text-amber-700'
	if (status === 'RETURNED') return 'bg-emerald-100 text-emerald-700'
	if (status === 'OVERDUE') return 'bg-rose-100 text-rose-700'
	return 'bg-slate-200 text-slate-700'
}

function UserDetailPage() {
	const navigate = useNavigate()
	const location = useLocation()
	const { userId } = useParams()
	const [statusFilter, setStatusFilter] = useState('')

	const selectedUser = location.state?.user
	const historyQuery = useUserBorrowingHistory(userId, statusFilter || undefined)
	const records = historyQuery.data ?? []

	const summary = useMemo(() => {
		const borrowing = records.filter((record) => record.status === 'BORROWING').length
		const returned = records.filter((record) => record.status === 'RETURNED').length
		const overdue = records.filter((record) => record.status === 'OVERDUE').length

		return {
			total: records.length,
			borrowing,
			returned,
			overdue,
		}
	}, [records])

	return (
		<div className='space-y-5'>
			<div className='flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between'>
				<div>
					<button
						type='button'
						onClick={() => navigate(librarianPaths.users)}
						className='inline-flex items-center gap-2 text-sm font-medium text-slate-600 transition hover:text-slate-900'
					>
						<ArrowLeft size={16} />
						Quay lại danh sách users
					</button>
					<h1 className='mt-2 text-2xl font-semibold text-slate-900'>Chi tiết người dùng</h1>
					<p className='mt-1 text-sm text-slate-600'>
						Theo dõi thông tin cá nhân và lịch sử mượn trả sách của người dùng.
					</p>
				</div>

				<button
					type='button'
					onClick={() => historyQuery.refetch()}
					disabled={historyQuery.isFetching}
					className='inline-flex items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60'
				>
					<RefreshCw size={16} className={historyQuery.isFetching ? 'animate-spin' : ''} />
					Làm mới
				</button>
			</div>

			<section className='grid grid-cols-1 gap-4 lg:grid-cols-2'>
				<div className='rounded-xl border border-slate-200 bg-white p-5'>
					<h2 className='text-base font-semibold text-slate-900'>Thông tin cá nhân</h2>
					<div className='mt-4 space-y-3 text-sm text-slate-700'>
						<p>
							<span className='font-medium text-slate-500'>ID:</span> {selectedUser?.id || userId || '-'}
						</p>
						<p>
							<span className='font-medium text-slate-500'>Họ tên:</span>{' '}
							{selectedUser?.fullName || 'Không có dữ liệu từ trang danh sách'}
						</p>
						<p>
							<span className='font-medium text-slate-500'>Email:</span>{' '}
							{selectedUser?.email || 'Không có dữ liệu từ trang danh sách'}
						</p>
					</div>
				</div>

				<div className='grid grid-cols-2 gap-3 rounded-xl border border-slate-200 bg-white p-5 sm:grid-cols-4'>
					<div>
						<p className='text-xs text-slate-500'>Tổng phiếu</p>
						<p className='mt-1 text-2xl font-semibold text-slate-900'>{summary.total}</p>
					</div>
					<div>
						<p className='text-xs text-slate-500'>Đang mượn</p>
						<p className='mt-1 text-2xl font-semibold text-amber-700'>{summary.borrowing}</p>
					</div>
					<div>
						<p className='text-xs text-slate-500'>Đã trả</p>
						<p className='mt-1 text-2xl font-semibold text-emerald-700'>{summary.returned}</p>
					</div>
					<div>
						<p className='text-xs text-slate-500'>Quá hạn</p>
						<p className='mt-1 text-2xl font-semibold text-rose-700'>{summary.overdue}</p>
					</div>
				</div>
			</section>

			<section className='rounded-xl border border-slate-200 bg-white'>
				<div className='flex flex-col gap-3 border-b border-slate-200 px-4 py-4 sm:flex-row sm:items-center sm:justify-between'>
					<h2 className='text-base font-semibold text-slate-900'>Lịch sử mượn trả</h2>

					<select
						value={statusFilter}
						onChange={(event) => setStatusFilter(event.target.value)}
						className='w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2 sm:max-w-52'
					>
						{STATUS_OPTIONS.map((option) => (
							<option key={option.value || 'ALL'} value={option.value}>
								{option.label}
							</option>
						))}
					</select>
				</div>

				<div className='overflow-x-auto'>
					<table className='min-w-full divide-y divide-slate-200'>
						<thead className='bg-slate-50'>
							<tr>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>STT</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Record ID</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Barcode</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Ngày mượn</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Hạn trả</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Ngày trả</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Trạng thái</th>
							</tr>
						</thead>

						<tbody className='divide-y divide-slate-100'>
							{historyQuery.isLoading ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-sm text-slate-500'>
										<span className='inline-flex items-center gap-2'>
											<Loader2 size={16} className='animate-spin' />
											Đang tải lịch sử mượn trả...
										</span>
									</td>
								</tr>
							) : historyQuery.isError ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-sm text-rose-600'>
										Không thể tải lịch sử mượn trả của người dùng.
									</td>
								</tr>
							) : records.length === 0 ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-sm text-slate-500'>
										Không có dữ liệu mượn trả phù hợp.
									</td>
								</tr>
							) : (
								records.map((record, index) => (
									<tr key={record.id} className='hover:bg-slate-50'>
										<td className='px-4 py-3 text-sm text-slate-700'>{index + 1}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{record.id || '-'}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{record.barcode || '-'}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{formatDate(record.borrowDate) || '-'}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{formatDateTime(record.dueDate) || '-'}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{formatDate(record.returnDate) || '-'}</td>
										<td className='px-4 py-3 text-sm'>
											<span
												className={`inline-flex rounded-full px-2.5 py-1 text-xs font-medium ${getStatusBadgeClass(record.status)}`}
											>
												{record.status || '-'}
											</span>
										</td>
									</tr>
								))
							)}
						</tbody>
					</table>
				</div>
			</section>
		</div>
	)
}

export default UserDetailPage
