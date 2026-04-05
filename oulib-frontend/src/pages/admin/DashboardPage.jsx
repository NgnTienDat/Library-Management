import { useSystemTotals } from '../../hooks/useStatistics'

function StatCard({ label, value, helper }) {
	return (
		<div className='rounded-2xl border border-slate-200 bg-white p-5 shadow-sm'>
			<p className='text-sm font-medium text-slate-500'>{label}</p>
			<p className='mt-2 text-3xl font-semibold text-slate-900'>{value}</p>
			{helper ? <p className='mt-2 text-sm text-slate-500'>{helper}</p> : null}
		</div>
	)
}

function DashboardPage() {
	const { data, isLoading, isError, error, refetch } = useSystemTotals()

	const errorMessage =
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Không tải được dữ liệu thống kê'

	if (isLoading) {
		return (
			<div className='space-y-4'>
				<h1 className='text-2xl font-semibold text-slate-900'>Admin Dashboard</h1>
				<div className='rounded-2xl border border-slate-200 bg-white p-6 text-slate-600 shadow-sm'>
					Đang tải thống kê...
				</div>
			</div>
		)
	}

	if (isError) {
		return (
			<div className='space-y-4'>
				<h1 className='text-2xl font-semibold text-slate-900'>Admin Dashboard</h1>
				<div className='rounded-2xl border border-rose-200 bg-rose-50 p-6 text-rose-700 shadow-sm'>
					<p className='font-medium'>Có lỗi khi tải dashboard</p>
					<p className='mt-1 text-sm'>{errorMessage}</p>
					<button
						type='button'
						onClick={() => refetch()}
						className='mt-4 rounded-lg bg-rose-600 px-4 py-2 text-sm font-medium text-white hover:bg-rose-500'
					>
						Thử lại
					</button>
				</div>
			</div>
		)
	}

	return (
		<div className='space-y-6'>
			<div>
				<h1 className='text-2xl font-semibold text-slate-900'>Admin Dashboard</h1>
				<p className='mt-1 text-sm text-slate-500'>
					Tổng quan hệ thống thư viện.
				</p>
			</div>

			<div className='grid gap-4 md:grid-cols-2 xl:grid-cols-3'>
				<StatCard
					label='Tổng số user'
					value={data?.totalUsers ?? 0}
					helper='Bao gồm toàn bộ tài khoản trong hệ thống'
				/>
				<StatCard
					label='Tổng số sách'
					value={data?.totalBooks ?? 0}
					helper='Số đầu sách hiện có'
				/>
				<StatCard
					label='Tổng số bản sao'
					value={data?.totalCopies ?? 0}
					helper='Số bản copy trong kho'
				/>
				<StatCard
					label='Tổng phiếu mượn'
					value={data?.totalBorrowRecords ?? 0}
					helper='Tất cả lượt mượn/trả đã ghi nhận'
				/>
				<StatCard
					label='Đang mượn'
					value={data?.totalCurrentlyBorrowed ?? 0}
					helper='Số sách đang được giữ ngoài thư viện'
				/>
				<StatCard
					label='Quá hạn'
					value={data?.totalOverdue ?? 0}
					helper='Số lượt mượn quá hạn hiện tại'
				/>
			</div>
		</div>
	)
}

export default DashboardPage