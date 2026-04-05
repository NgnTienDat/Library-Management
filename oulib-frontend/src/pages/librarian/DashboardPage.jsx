import { AlertTriangle, Loader2, RefreshCw } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useSystemTotals } from '../../hooks/useSystemTotals'
import { ROLE_ROUTE_PATHS, ROLES } from '../../utils/constants'

const librarianPaths = ROLE_ROUTE_PATHS[ROLES.LIBRARIAN]

const EMPTY_TOTALS = Object.freeze({
	totalUsers: 0,
	totalBooks: 0,
	totalCopies: 0,
	totalBorrowRecords: 0,
	totalCurrentlyBorrowed: 0,
	totalOverdue: 0,
})

const NUMBER_FORMATTER = new Intl.NumberFormat('vi-VN')

function formatNumber(value) {
	const safeValue = Number(value)
	return NUMBER_FORMATTER.format(Number.isFinite(safeValue) ? safeValue : 0)
}

function toPercent(value, total) {
	if (!total) return 0
	return Number(((value / total) * 100).toFixed(1))
}

function DashboardPage() {
	const navigate = useNavigate()
	const systemTotalsQuery = useSystemTotals()

	const totals = systemTotalsQuery.data ?? EMPTY_TOTALS
	const totalOnLoan = Math.max(
		0,
		Number(totals.totalCurrentlyBorrowed || 0) + Number(totals.totalOverdue || 0),
	)
	const availableCopies = Math.max(0, Number(totals.totalCopies || 0) - totalOnLoan)
	const borrowingRate = toPercent(totalOnLoan, totals.totalCopies)
	const overdueRate = toPercent(totals.totalOverdue, totalOnLoan)

	const quickActions = [
		{
			label: 'Quản lý mượn trả',
			description: 'Xử lý các lượt mượn và trạng thái phiếu',
			to: librarianPaths.borrow,
		},
		{
			label: 'Tiếp nhận trả sách',
			description: 'Vào màn hình trả sách để thao tác nhanh',
			to: librarianPaths.return,
		},
		{
			label: 'Quản lý đầu sách',
			description: 'Thêm mới, chỉnh sửa và kiểm soát kho sách',
			to: librarianPaths.books,
		},
	]

	if (systemTotalsQuery.isLoading) {
		return (
			<div className='rounded-xl border border-slate-200 bg-white p-8 text-center text-slate-600'>
				<span className='inline-flex items-center gap-2'>
					<Loader2 size={18} className='animate-spin' />
					Đang tải dữ liệu dashboard...
				</span>
			</div>
		)
	}

	if (systemTotalsQuery.isError) {
		return (
			<div className='space-y-4 rounded-xl border border-rose-200 bg-rose-50 p-6'>
				<h1 className='text-xl font-semibold text-rose-700'>Không tải được dashboard</h1>
				<p className='text-sm text-rose-600'>
					Đã xảy ra lỗi khi lấy thống kê hệ thống. Vui lòng thử lại sau ít phút.
				</p>
				<button
					type='button'
					onClick={() => systemTotalsQuery.refetch()}
					className='inline-flex items-center gap-2 rounded-md bg-rose-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-rose-500'
				>
					<RefreshCw size={16} />
					Thử lại
				</button>
			</div>
		)
	}

	return (
		<div className='space-y-6'>
			<div className='flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between'>
				<div>
					<h1 className='text-2xl font-semibold text-slate-900'>Dashboard thủ thư</h1>
					<p className='mt-1 text-sm text-slate-600'>
						Theo dõi nhanh tình hình kho sách, lượt mượn và công việc cần ưu tiên.
					</p>
				</div>

				<button
					type='button'
					onClick={() => systemTotalsQuery.refetch()}
					disabled={systemTotalsQuery.isFetching}
					className='inline-flex items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60'
				>
					<RefreshCw size={16} className={systemTotalsQuery.isFetching ? 'animate-spin' : ''} />
					Làm mới số liệu
				</button>
			</div>

			{totals.totalOverdue > 0 ? (
				<div className='rounded-xl border border-amber-200 bg-amber-50 p-4'>
					<div className='flex items-start gap-3'>
						<AlertTriangle size={18} className='mt-0.5 text-amber-700' />
						<div>
							<p className='text-sm font-semibold text-amber-800'>Cần xử lý quá hạn</p>
							<p className='mt-1 text-sm text-amber-700'>
								Hiện có {formatNumber(totals.totalOverdue)} lượt mượn quá hạn. Nên ưu tiên liên hệ người mượn và hỗ trợ xử lý trả sách.
							</p>
						</div>
					</div>
				</div>
			) : null}

			<section className='grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4'>
				<div className='rounded-xl border border-slate-200 bg-white p-5'>
					<p className='text-sm text-slate-500'>Tổng người dùng</p>
					<p className='mt-3 text-3xl font-semibold text-slate-900'>{formatNumber(totals.totalUsers)}</p>
				</div>
				<div className='rounded-xl border border-slate-200 bg-white p-5'>
					<p className='text-sm text-slate-500'>Tổng đầu sách</p>
					<p className='mt-3 text-3xl font-semibold text-slate-900'>{formatNumber(totals.totalBooks)}</p>
				</div>
				<div className='rounded-xl border border-slate-200 bg-white p-5'>
					<p className='text-sm text-slate-500'>Tổng bản sao</p>
					<p className='mt-3 text-3xl font-semibold text-slate-900'>{formatNumber(totals.totalCopies)}</p>
				</div>
				<div className='rounded-xl border border-slate-200 bg-white p-5'>
					<p className='text-sm text-slate-500'>Tổng phiếu mượn</p>
					<p className='mt-3 text-3xl font-semibold text-slate-900'>{formatNumber(totals.totalBorrowRecords)}</p>
				</div>
			</section>

			<section className='grid grid-cols-1 gap-4 lg:grid-cols-3'>
				<div className='rounded-xl border border-blue-200 bg-blue-50 p-5'>
					<p className='text-sm text-blue-700'>Đang được mượn</p>
					<p className='mt-3 text-3xl font-semibold text-blue-900'>{formatNumber(totalOnLoan)}</p>
					<p className='mt-2 text-xs text-blue-700'>Chiếm {borrowingRate}% tổng bản sao</p>
				</div>
				<div className='rounded-xl border border-emerald-200 bg-emerald-50 p-5'>
					<p className='text-sm text-emerald-700'>Bản sao còn khả dụng</p>
					<p className='mt-3 text-3xl font-semibold text-emerald-900'>{formatNumber(availableCopies)}</p>
					<p className='mt-2 text-xs text-emerald-700'>Sẵn sàng phục vụ lượt mượn mới</p>
				</div>
				<div className='rounded-xl border border-rose-200 bg-rose-50 p-5'>
					<p className='text-sm text-rose-700'>Mượn quá hạn</p>
					<p className='mt-3 text-3xl font-semibold text-rose-900'>{formatNumber(totals.totalOverdue)}</p>
					<p className='mt-2 text-xs text-rose-700'>Chiếm {overdueRate}% tổng lượt đang giữ sách</p>
				</div>
			</section>

			<section className='grid grid-cols-1 gap-4 lg:grid-cols-2'>
				<div className='rounded-xl border border-slate-200 bg-white p-5'>
					<h2 className='text-lg font-semibold text-slate-900'>Nhịp vận hành</h2>
					<div className='mt-4 space-y-4'>
						<div>
							<div className='mb-1 flex items-center justify-between text-sm text-slate-600'>
								<span>Mức sử dụng bản sao</span>
								<span>{borrowingRate}%</span>
							</div>
							<div className='h-2 rounded-full bg-slate-100'>
								<div
									className='h-2 rounded-full bg-blue-600 transition-all'
									style={{ width: `${Math.min(borrowingRate, 100)}%` }}
								/>
							</div>
						</div>

						<div>
							<div className='mb-1 flex items-center justify-between text-sm text-slate-600'>
								<span>Tỉ lệ quá hạn</span>
								<span>{overdueRate}%</span>
							</div>
							<div className='h-2 rounded-full bg-slate-100'>
								<div
									className='h-2 rounded-full bg-rose-500 transition-all'
									style={{ width: `${Math.min(overdueRate, 100)}%` }}
								/>
							</div>
						</div>
					</div>
				</div>

				<div className='rounded-xl border border-slate-200 bg-white p-5'>
					<h2 className='text-lg font-semibold text-slate-900'>Hành động nhanh</h2>
					<div className='mt-4 space-y-3'>
						{quickActions.map((action) => (
							<button
								key={action.to}
								type='button'
								onClick={() => navigate(action.to)}
								className='w-full rounded-lg border border-slate-200 bg-slate-50 p-3 text-left transition hover:border-slate-300 hover:bg-white'
							>
								<p className='text-sm font-semibold text-slate-900'>{action.label}</p>
								<p className='mt-1 text-xs text-slate-600'>{action.description}</p>
							</button>
						))}
					</div>
				</div>
			</section>
		</div>
	)
}

export default DashboardPage
