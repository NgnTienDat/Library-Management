import { BookOpen, BookOpenCheck, ClockAlert, Loader2, RefreshCw, Undo2 } from 'lucide-react'
import { useMemo, useState } from 'react'
import {
	Bar,
	BarChart,
	CartesianGrid,
	Cell,
	Legend,
	Line,
	LineChart,
	Pie,
	PieChart,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis,
} from 'recharts'
import {
	useBorrowingActivity,
	useInventoryStatus,
	useSystemTotals,
	useTopBorrowedBooks,
	useTopBorrowedCategories,
} from '../../hooks/useStatistics'

const TIMEFRAME_OPTIONS = [
	{ label: 'Theo ngày', value: 'day' },
	{ label: 'Theo tháng', value: 'month' },
	{ label: 'Theo quý', value: 'quarter' },
]

const PIE_COLORS = ['#0f766e', '#ea580c', '#2563eb', '#ca8a04', '#be123c']
const NUMBER_FORMATTER = new Intl.NumberFormat('vi-VN')

function formatNumber(value) {
	const safeValue = Number(value)
	return NUMBER_FORMATTER.format(Number.isFinite(safeValue) ? safeValue : 0)
}

function toPercent(value, total) {
	if (!total) return 0
	return Number(((value / total) * 100).toFixed(1))
}

function toIsoDate(date) {
	const year = date.getFullYear()
	const month = String(date.getMonth() + 1).padStart(2, '0')
	const day = String(date.getDate()).padStart(2, '0')
	return `${year}-${month}-${day}`
}

function parseIsoDate(dateString) {
	if (!dateString || typeof dateString !== 'string') return null
	const [year, month, day] = dateString.split('-').map(Number)
	if (!year || !month || !day) return null
	return new Date(year, month - 1, day)
}

function getDateRangeByTimeframe(timeframe) {
	const today = new Date()
	const endDate = new Date(today.getFullYear(), today.getMonth(), today.getDate())

	if (timeframe === 'day') {
		const startDate = new Date(endDate)
		startDate.setDate(endDate.getDate() - 9)
		return {
			label: '10 ngày gần nhất',
			from: toIsoDate(startDate),
			to: toIsoDate(endDate),
			groupBy: 'day',
		}
	}

	if (timeframe === 'month') {
		const startDate = new Date(endDate.getFullYear(), endDate.getMonth(), 1)
		return {
			label: 'Tháng này',
			from: toIsoDate(startDate),
			to: toIsoDate(endDate),
			groupBy: 'week',
		}
	}

	const quarterStartMonth = Math.floor(endDate.getMonth() / 3) * 3
	const startDate = new Date(endDate.getFullYear(), quarterStartMonth, 1)
	return {
		label: 'Quý này',
		from: toIsoDate(startDate),
		to: toIsoDate(endDate),
		groupBy: 'month',
	}
}

function formatTimelineLabel(timeKey, groupBy) {
	if (!timeKey) return '-'

	if (groupBy === 'month') {
		const [year, month] = String(timeKey).split('-')
		if (!year || !month) return String(timeKey)
		return `${month}/${year}`
	}

	const parsed = parseIsoDate(timeKey)
	if (!parsed) return String(timeKey)

	const day = String(parsed.getDate()).padStart(2, '0')
	const month = String(parsed.getMonth() + 1).padStart(2, '0')
	return groupBy === 'week' ? `Tuần ${day}/${month}` : `${day}/${month}`
}

function StatsCard({ title, value, subtitle, icon: Icon, tone }) {
	const toneClasses = {
		teal: 'border-teal-200 bg-teal-50 text-teal-900',
		orange: 'border-orange-200 bg-orange-50 text-orange-900',
		blue: 'border-blue-200 bg-blue-50 text-blue-900',
		rose: 'border-rose-200 bg-rose-50 text-rose-900',
	}

	return (
		<div className={`rounded-2xl border p-5 shadow-sm ${toneClasses[tone]}`}>
			<div className='flex items-start justify-between gap-3'>
				<div>
					<p className='text-sm font-medium text-slate-700'>{title}</p>
					<p className='mt-3 text-3xl font-semibold'>{value}</p>
					<p className='mt-1 text-xs text-slate-600'>{subtitle}</p>
				</div>
				<div className='rounded-xl bg-white/70 p-2.5'>
					<Icon size={18} />
				</div>
			</div>
		</div>
	)
}

function BorrowChart({ data, title }) {
	return (
		<div className='rounded-2xl border border-slate-200 bg-white p-5 shadow-sm'>
			<div className='mb-4'>
				<h2 className='text-lg font-semibold text-slate-900'>Biểu đồ xu hướng mượn sách</h2>
				<p className='text-sm text-slate-600'>{title}</p>
			</div>
			<div className='h-80'>
				{data.length === 0 ? (
					<div className='flex h-full items-center justify-center text-sm text-slate-500'>
						Không có dữ liệu trong khoảng thời gian đã chọn
					</div>
				) : (
					<ResponsiveContainer width='100%' height='100%'>
						<LineChart data={data} margin={{ top: 8, right: 12, left: 0, bottom: 0 }}>
							<CartesianGrid strokeDasharray='3 3' stroke='#e2e8f0' />
							<XAxis dataKey='label' stroke='#64748b' fontSize={12} />
							<YAxis stroke='#64748b' fontSize={12} allowDecimals={false} />
							<Tooltip
								formatter={(value) => [`${formatNumber(value)} lượt`, 'Lượt mượn']}
								labelFormatter={(label) => `Mốc thời gian: ${label}`}
							/>
							<Line
								type='monotone'
								dataKey='borrow'
								stroke='#0f766e'
								strokeWidth={3}
								dot={{ r: 4, fill: '#0f766e', strokeWidth: 0 }}
								activeDot={{ r: 6 }}
								isAnimationActive
								animationDuration={700}
							/>
						</LineChart>
					</ResponsiveContainer>
				)}
			</div>
		</div>
	)
}

function BorrowVsReturnChart({ data, title }) {
	return (
		<div className='rounded-2xl border border-slate-200 bg-white p-5 shadow-sm'>
			<div className='mb-4'>
				<h2 className='text-lg font-semibold text-slate-900'>Biểu đồ mượn vs trả</h2>
				<p className='text-sm text-slate-600'>{title}</p>
			</div>
			<div className='h-80'>
				{data.length === 0 ? (
					<div className='flex h-full items-center justify-center text-sm text-slate-500'>
						Không có dữ liệu trong khoảng thời gian đã chọn
					</div>
				) : (
					<ResponsiveContainer width='100%' height='100%'>
						<BarChart data={data} margin={{ top: 8, right: 12, left: 0, bottom: 0 }}>
							<CartesianGrid strokeDasharray='3 3' stroke='#e2e8f0' />
							<XAxis dataKey='label' stroke='#64748b' fontSize={12} />
							<YAxis stroke='#64748b' fontSize={12} allowDecimals={false} />
							<Tooltip formatter={(value) => `${formatNumber(value)} lượt`} />
							<Legend />
							<Bar
								dataKey='borrow'
								name='Mượn'
								fill='#2563eb'
								radius={[8, 8, 0, 0]}
								isAnimationActive
								animationDuration={600}
							/>
							<Bar
								dataKey='returned'
								name='Trả'
								fill='#f97316'
								radius={[8, 8, 0, 0]}
								isAnimationActive
								animationDuration={800}
							/>
						</BarChart>
					</ResponsiveContainer>
				)}
			</div>
		</div>
	)
}

function CategoryPieChart({ data }) {
	return (
		<div className='rounded-2xl border border-slate-200 bg-white p-5 shadow-sm'>
			<div className='mb-4'>
				<h2 className='text-lg font-semibold text-slate-900'>Top thể loại được mượn</h2>
				<p className='text-sm text-slate-600'>Tỷ trọng theo số lượt mượn trong kỳ</p>
			</div>
			<div className='h-80'>
				{data.length === 0 ? (
					<div className='flex h-full items-center justify-center text-sm text-slate-500'>
						Không có dữ liệu thể loại trong kỳ
					</div>
				) : (
					<ResponsiveContainer width='100%' height='100%'>
						<PieChart>
							<Pie
								data={data}
								dataKey='value'
								nameKey='name'
								cx='50%'
								cy='50%'
								innerRadius={70}
								outerRadius={110}
								paddingAngle={3}
								isAnimationActive
								animationDuration={850}
							>
								{data.map((entry, index) => (
									<Cell key={`${entry.name}-${index}`} fill={PIE_COLORS[index % PIE_COLORS.length]} />
								))}
							</Pie>
							<Tooltip formatter={(value) => `${formatNumber(value)} lượt`} />
							<Legend verticalAlign='bottom' height={36} />
						</PieChart>
					</ResponsiveContainer>
				)}
			</div>
		</div>
	)
}

function TopBooksTable({ rows }) {
	return (
		<div className='rounded-2xl border border-slate-200 bg-white p-5 shadow-sm'>
			<h2 className='text-lg font-semibold text-slate-900'>Top sách mượn nhiều</h2>
			<p className='mt-1 text-sm text-slate-600'>Các đầu sách có nhu cầu cao nhất toàn hệ thống</p>
			<div className='mt-4 overflow-x-auto'>
				{rows.length === 0 ? (
					<div className='py-10 text-center text-sm text-slate-500'>Chưa có dữ liệu top sách</div>
				) : (
					<table className='min-w-full divide-y divide-slate-200'>
						<thead className='bg-slate-50'>
							<tr>
								<th className='px-3 py-2 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>#</th>
								<th className='px-3 py-2 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Tên sách</th>
								<th className='px-3 py-2 text-right text-xs font-semibold uppercase tracking-wide text-slate-600'>Số lượt mượn</th>
							</tr>
						</thead>
						<tbody className='divide-y divide-slate-100'>
							{rows.map((book, index) => (
								<tr key={`${book.title}-${index}`} className='hover:bg-slate-50'>
									<td className='px-3 py-2.5 text-sm text-slate-700'>{index + 1}</td>
									<td className='px-3 py-2.5 text-sm font-medium text-slate-800'>{book.title}</td>
									<td className='px-3 py-2.5 text-right text-sm text-slate-700'>{formatNumber(book.borrows)}</td>
								</tr>
							))}
						</tbody>
					</table>
				)}
			</div>
		</div>
	)
}

function InventoryStatusTable({ inventory }) {
	return (
		<div className='rounded-2xl border border-slate-200 bg-white p-5 shadow-sm'>
			<h2 className='text-lg font-semibold text-slate-900'>Tình trạng kho sách</h2>
			<p className='mt-1 text-sm text-slate-600'>Số liệu tồn kho và trạng thái vận hành hiện tại</p>
			<div className='mt-4 overflow-x-auto'>
				<table className='min-w-full divide-y divide-slate-200'>
					<thead className='bg-slate-50'>
						<tr>
							<th className='px-3 py-2 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Chỉ số</th>
							<th className='px-3 py-2 text-right text-xs font-semibold uppercase tracking-wide text-slate-600'>Giá trị</th>
						</tr>
					</thead>
					<tbody className='divide-y divide-slate-100'>
						<tr>
							<td className='px-3 py-2.5 text-sm font-medium text-slate-800'>Tổng số sách</td>
							<td className='px-3 py-2.5 text-right text-sm text-slate-700'>{formatNumber(inventory.totalBooks)}</td>
						</tr>
						<tr>
							<td className='px-3 py-2.5 text-sm font-medium text-slate-800'>Số còn trong kho</td>
							<td className='px-3 py-2.5 text-right text-sm text-slate-700'>{formatNumber(inventory.inStock)}</td>
						</tr>
						<tr>
							<td className='px-3 py-2.5 text-sm font-medium text-slate-800'>Số đang mượn</td>
							<td className='px-3 py-2.5 text-right text-sm text-slate-700'>{formatNumber(inventory.onLoan)}</td>
						</tr>
						<tr>
							<td className='px-3 py-2.5 text-sm font-medium text-slate-800'>Số hỏng/mất</td>
							<td className='px-3 py-2.5 text-right text-sm text-slate-700'>{formatNumber(inventory.damagedOrLost)}</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	)
}

function DashboardPage() {
	const [timeframe, setTimeframe] = useState('month')
	const timeframeConfig = useMemo(() => getDateRangeByTimeframe(timeframe), [timeframe])

	const systemTotalsQuery = useSystemTotals()
	const inventoryStatusQuery = useInventoryStatus()
	const borrowingActivityQuery = useBorrowingActivity({
		from: timeframeConfig.from,
		to: timeframeConfig.to,
		groupBy: timeframeConfig.groupBy,
	})
	const topBooksQuery = useTopBorrowedBooks(8)
	const topCategoriesQuery = useTopBorrowedCategories({
		from: timeframeConfig.from,
		to: timeframeConfig.to,
		limit: 5,
	})

	const queries = [
		systemTotalsQuery,
		inventoryStatusQuery,
		borrowingActivityQuery,
		topBooksQuery,
		topCategoriesQuery,
	]

	const isAnyFetching = queries.some((query) => query.isFetching)
	const isInitialLoading = queries.every((query) => query.isLoading)
	const failedQueries = queries.filter((query) => query.isError)
	const lastUpdatedAt = Math.max(...queries.map((query) => query.dataUpdatedAt || 0), 0)

	const activities = borrowingActivityQuery.data ?? []
	const totalBorrow = activities.reduce((sum, item) => sum + Number(item.borrowCount || 0), 0)
	const totalReturn = activities.reduce((sum, item) => sum + Number(item.returnCount || 0), 0)

	const systemTotals = systemTotalsQuery.data ?? {}
	const onLoanCount =
		Number(systemTotals.totalCurrentlyBorrowed || 0) + Number(systemTotals.totalOverdue || 0)
	const overdueCount = Number(systemTotals.totalOverdue || 0)
	const overdueRate = toPercent(overdueCount, onLoanCount)

	const inventoryData = inventoryStatusQuery.data ?? {}
	const inventory = {
		totalBooks: Number(inventoryData.totalBooks || 0),
		inStock: Number(inventoryData.availableCopies || 0),
		onLoan: Number(inventoryData.borrowedCopies || 0),
		damagedOrLost: Number(
			inventoryData.damagedOrLostCopies ||
				Number(inventoryData.damagedCopies || 0) + Number(inventoryData.lostCopies || 0),
		),
	}

	const borrowTrendData = activities.map((item) => ({
		label: formatTimelineLabel(item.time, timeframeConfig.groupBy),
		borrow: Number(item.borrowCount || 0),
	}))

	const borrowVsReturnData = activities.map((item) => ({
		label: formatTimelineLabel(item.time, timeframeConfig.groupBy),
		borrow: Number(item.borrowCount || 0),
		returned: Number(item.returnCount || 0),
	}))

	const topBooksRows = (topBooksQuery.data ?? []).map((item) => ({
		title: item.title,
		borrows: Number(item.borrowCount || 0),
	}))

	const topCategoriesData = (topCategoriesQuery.data ?? []).map((item) => ({
		name: item.categoryName,
		value: Number(item.borrowCount || 0),
	}))

	const cards = [
		{
			title: `Tổng lượt mượn (${timeframeConfig.label})`,
			value: formatNumber(totalBorrow),
			subtitle: 'Tính từ biểu đồ hoạt động mượn/trả',
			icon: BookOpenCheck,
			tone: 'teal',
		},
		{
			title: `Tổng lượt trả (${timeframeConfig.label})`,
			value: formatNumber(totalReturn),
			subtitle: 'Tính từ biểu đồ hoạt động mượn/trả',
			icon: Undo2,
			tone: 'orange',
		},
		{
			title: 'Sách đang được mượn',
			value: formatNumber(onLoanCount),
			subtitle: 'Bao gồm cả các lượt đang quá hạn',
			icon: BookOpen,
			tone: 'blue',
		},
		{
			title: 'Mượn quá hạn',
			value: `${formatNumber(overdueCount)} (${overdueRate}%)`,
			subtitle: 'Tỷ lệ quá hạn trên tổng sách đang mượn',
			icon: ClockAlert,
			tone: 'rose',
		},
	]

	const handleRefresh = () => {
		queries.forEach((query) => {
			query.refetch()
		})
	}

	if (isInitialLoading) {
		return (
			<div className='rounded-xl border border-slate-200 bg-white p-8 text-center text-slate-600'>
				<span className='inline-flex items-center gap-2'>
					<Loader2 size={18} className='animate-spin' />
					Đang tải dữ liệu dashboard...
				</span>
			</div>
		)
	}

	return (
		<div className='space-y-6'>
			<div className='rounded-2xl border border-slate-200 bg-linear-to-r from-teal-50 via-white to-orange-50 p-6'>
				<div className='flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between'>
					<div>
						<h1 className='text-2xl font-semibold text-slate-900'>Dashboard thống kê thư viện</h1>
						<p className='mt-1 text-sm text-slate-600'>
							Dữ liệu thống kê thời gian thực phục vụ theo dõi hoạt động mượn trả của thủ thư.
						</p>
						<p className='mt-2 text-xs text-slate-500'>
							Cập nhật lần cuối:{' '}
							{lastUpdatedAt
								? new Date(lastUpdatedAt).toLocaleString('vi-VN')
								: 'Chưa có dữ liệu'}
						</p>
					</div>

					<div className='flex flex-col gap-2 sm:flex-row sm:items-center'>
						<select
							value={timeframe}
							onChange={(event) => setTimeframe(event.target.value)}
							className='rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-teal-500 focus:ring-2'
						>
							{TIMEFRAME_OPTIONS.map((option) => (
								<option key={option.value} value={option.value}>
									{option.label}
								</option>
							))}
						</select>

						<button
							type='button'
							onClick={handleRefresh}
							disabled={isAnyFetching}
							className='inline-flex items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60'
						>
							<RefreshCw size={15} className={isAnyFetching ? 'animate-spin' : ''} />
							Làm mới dữ liệu
						</button>
					</div>
				</div>
			</div>

			{failedQueries.length > 0 ? (
				<div className='rounded-xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-800'>
					Một số API thống kê đang lỗi. Dashboard hiển thị dữ liệu hiện có, bạn có thể bấm "Làm mới dữ liệu" để thử lại.
				</div>
			) : null}

			<section className='grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4'>
				{cards.map((card) => (
					<StatsCard
						key={card.title}
						title={card.title}
						value={card.value}
						subtitle={card.subtitle}
						icon={card.icon}
						tone={card.tone}
					/>
				))}
			</section>

			<section className='grid grid-cols-1 gap-5 xl:grid-cols-2'>
				<BorrowChart data={borrowTrendData} title={`Xu hướng mượn theo ${timeframeConfig.label.toLowerCase()}`} />
				<BorrowVsReturnChart
					data={borrowVsReturnData}
					title={`So sánh mượn và trả theo ${timeframeConfig.label.toLowerCase()}`}
				/>
			</section>

			<section className='grid grid-cols-1 gap-5 xl:grid-cols-3'>
				<div className='xl:col-span-1'>
					<CategoryPieChart data={topCategoriesData} />
				</div>
				<div className='xl:col-span-2'>
					<TopBooksTable rows={topBooksRows} />
				</div>
			</section>

			<section>
				<InventoryStatusTable inventory={inventory} />
			</section>
		</div>
	)
}

export default DashboardPage
