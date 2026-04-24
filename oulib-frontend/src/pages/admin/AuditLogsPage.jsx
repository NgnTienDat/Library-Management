import { useMemo, useState } from 'react'
import { useAuditLogs } from '../../hooks/useAuditLogs'
import { formatDateTime } from '../../utils/datetime'

function JsonPreview({ value }) {
	if (!value) {
		return <span className='text-slate-400'>-</span>
	}

	return (
		<details className='max-w-[320px]'>
			<summary className='cursor-pointer truncate text-sm text-slate-700'>Xem JSON</summary>
			<pre className='mt-2 max-h-40 overflow-auto rounded bg-slate-100 p-2 text-xs text-slate-700'>
				{value}
			</pre>
		</details>
	)
}

function AuditLogsPage() {
	const [page, setPage] = useState(0)
	const [size, setSize] = useState(20)

	const queryParams = useMemo(() => ({ page, size }), [page, size])
	const { data, isLoading, isFetching, refetch } = useAuditLogs(queryParams)

	const logs = data?.content ?? []
	const currentPage = data?.pageNumber ?? page
	const totalPages = data?.totalPages ?? 0
	const totalElements = data?.totalElements ?? 0
	const isFirst = data?.first ?? true
	const isLast = data?.last ?? true

	const handleSizeChange = (event) => {
		const nextSize = Number(event.target.value)
		setSize(nextSize)
		setPage(0)
	}

	return (
		<div className='space-y-6'>
			<div className='flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between'>
				<div>
					<h1 className='text-2xl font-semibold text-slate-900'>Audit Logs</h1>
					<p className='mt-1 text-sm text-slate-500'>
						Theo dõi các hành động quan trọng trong hệ thống.
					</p>
				</div>

				<div className='flex items-center gap-3'>
					<label className='text-sm text-slate-600'>
						Số dòng:
						<select
							value={size}
							onChange={handleSizeChange}
							className='ml-2 rounded-md border border-slate-300 bg-white px-2 py-1 text-sm outline-none ring-blue-500 focus:ring-2'
						>
							<option value={10}>10</option>
							<option value={20}>20</option>
							<option value={50}>50</option>
						</select>
					</label>

					<button
						type='button'
						onClick={() => refetch()}
						className='rounded-md border border-slate-300 bg-white px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-100'
					>
						Làm mới
					</button>
				</div>
			</div>

			<div className='overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm'>
				<div className='overflow-x-auto'>
					<table className='min-w-full divide-y divide-slate-200 text-left text-sm'>
						<thead className='bg-slate-50 text-xs uppercase tracking-wide text-slate-600'>
							<tr>
								<th className='px-4 py-3'>Time</th>
								<th className='px-4 py-3'>User ID</th>
								<th className='px-4 py-3'>Action</th>
								<th className='px-4 py-3'>Resource</th>
								<th className='px-4 py-3'>Resource ID</th>
								<th className='px-4 py-3'>Old Value</th>
								<th className='px-4 py-3'>New Value</th>
							</tr>
						</thead>
						<tbody className='divide-y divide-slate-100'>
							{isLoading ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-slate-500'>
										Đang tải audit logs...
									</td>
								</tr>
							) : logs.length === 0 ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-slate-500'>
										Không có dữ liệu audit log.
									</td>
								</tr>
							) : (
								logs.map((log) => (
									<tr key={log.id} className='align-top hover:bg-slate-50'>
										<td className='px-4 py-3 text-slate-700'>{formatDateTime(log.createdAt)}</td>
										<td className='px-4 py-3 font-medium text-slate-800'>{log.userId ?? '-'}</td>
										<td className='px-4 py-3'>
											<span className='rounded-full bg-slate-100 px-2 py-1 text-xs font-semibold text-slate-700'>
												{log.action}
											</span>
										</td>
										<td className='px-4 py-3 text-slate-700'>{log.resourceType}</td>
										<td className='px-4 py-3 text-slate-700'>{log.resourceId ?? '-'}</td>
										<td className='px-4 py-3'>
											<JsonPreview value={log.oldValue} />
										</td>
										<td className='px-4 py-3'>
											<JsonPreview value={log.newValue} />
										</td>
									</tr>
								))
							)}
						</tbody>
					</table>
				</div>
			</div>

			<div className='flex flex-col gap-3 rounded-lg border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between'>
				<p className='text-sm text-slate-600'>
					Tổng số bản ghi: <span className='font-semibold text-slate-900'>{totalElements}</span>
				</p>

				<div className='flex items-center gap-2'>
					<button
						type='button'
						onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
						disabled={isFirst || isFetching}
						className='rounded-md border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50'
					>
						Trước
					</button>

					<span className='px-2 text-sm text-slate-700'>
						Trang {totalPages === 0 ? 0 : currentPage + 1} / {totalPages}
					</span>

					<button
						type='button'
						onClick={() => setPage((prev) => prev + 1)}
						disabled={isLast || isFetching || totalPages === 0}
						className='rounded-md border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50'
					>
						Sau
					</button>
				</div>
			</div>
		</div>
	)
}

export default AuditLogsPage
