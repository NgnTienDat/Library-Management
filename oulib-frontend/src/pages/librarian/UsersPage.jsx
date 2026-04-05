import { ChevronRight, Loader2, RefreshCw } from 'lucide-react'
import { useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useUsers } from '../../hooks/useUsers'
import { ROLE_ROUTE_PATHS, ROLES } from '../../utils/constants'

const librarianPaths = ROLE_ROUTE_PATHS[ROLES.LIBRARIAN]

function getRoleBadgeClass(role) {
	if (role === 'SYSADMIN') return 'bg-violet-100 text-violet-700'
	if (role === 'LIBRARIAN') return 'bg-blue-100 text-blue-700'
	if (role === 'USER') return 'bg-slate-200 text-slate-700'
	return 'bg-slate-100 text-slate-600'
}

function getStatusBadgeClass(user) {
	if (user?.status === 'SUSPENDED' || user?.active === false) {
		return 'bg-rose-100 text-rose-700'
	}

	return 'bg-emerald-100 text-emerald-700'
}

function UsersPage() {
	const navigate = useNavigate()
	const [currentPage, setCurrentPage] = useState(0)
	const pageSize = 10

	const queryParams = useMemo(
		() => ({
			page: currentPage,
			size: pageSize,
		}),
		[currentPage],
	)

	const usersQuery = useUsers(queryParams)
	const users = (usersQuery.data?.content ?? []).filter((user) => user?.role === 'USER')
	const pageNumber = usersQuery.data?.pageNumber ?? 0
	const totalPages = usersQuery.data?.totalPages ?? 0
	const isFirstPage = usersQuery.data?.first ?? true
	const isLastPage = usersQuery.data?.last ?? true

	const pageButtons = Array.from({ length: totalPages }, (_, index) => index)

	const goToUserDetail = (user) => {
		navigate(`${librarianPaths.users}/${user.id}`, {
			state: {
				user: {
					id: user.id,
					fullName: user.fullName,
					email: user.email,
					role: user.role,
					status: user.status,
					active: user.active,
				},
			},
		})
	}

	return (
		<div className='space-y-5'>
			<div className='flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between'>
				<div>
					<h1 className='text-2xl font-semibold text-slate-900'>Danh sách người dùng</h1>
					<p className='mt-1 text-sm text-slate-600'>
						Nhấn vào từng người dùng để xem thông tin chi tiết và lịch sử mượn trả.
					</p>
				</div>

				<button
					type='button'
					onClick={() => usersQuery.refetch()}
					disabled={usersQuery.isFetching}
					className='inline-flex items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60'
				>
					<RefreshCw size={16} className={usersQuery.isFetching ? 'animate-spin' : ''} />
					Làm mới
				</button>
			</div>

			<div className='overflow-hidden rounded-lg border border-slate-200 bg-white'>
				<div className='overflow-x-auto'>
					<table className='min-w-full divide-y divide-slate-200'>
						<thead className='bg-slate-50'>
							<tr>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>STT</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>User ID</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Họ tên</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Email</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Vai trò</th>
								<th className='px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600'>Trạng thái</th>
								<th className='px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-600'>Chi tiết</th>
							</tr>
						</thead>

						<tbody className='divide-y divide-slate-100'>
							{usersQuery.isLoading ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-sm text-slate-500'>
										<span className='inline-flex items-center gap-2'>
											<Loader2 size={16} className='animate-spin' />
											Đang tải danh sách người dùng...
										</span>
									</td>
								</tr>
							) : usersQuery.isError ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-sm text-rose-600'>
										Không thể tải danh sách người dùng.
									</td>
								</tr>
							) : users.length === 0 ? (
								<tr>
									<td colSpan={7} className='px-4 py-8 text-center text-sm text-slate-500'>
										Chưa có người dùng trong trang này.
									</td>
								</tr>
							) : (
								users.map((user, index) => (
									<tr
										key={user.id}
										onClick={() => goToUserDetail(user)}
										className='cursor-pointer hover:bg-slate-50'
									>
										<td className='px-4 py-3 text-sm text-slate-700'>{pageNumber * pageSize + index + 1}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{user.id || '-'}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{user.fullName || '-'}</td>
										<td className='px-4 py-3 text-sm text-slate-700'>{user.email || '-'}</td>
										<td className='px-4 py-3 text-sm'>
											<span
												className={`inline-flex rounded-full px-2.5 py-1 text-xs font-medium ${getRoleBadgeClass(user.role)}`}
											>
												{user.role || '-'}
											</span>
										</td>
										<td className='px-4 py-3 text-sm'>
											<span
												className={`inline-flex rounded-full px-2.5 py-1 text-xs font-medium ${getStatusBadgeClass(user)}`}
											>
												{user.status || (user.active ? 'ACTIVE' : 'INACTIVE')}
											</span>
										</td>
										<td className='px-4 py-3 text-right text-sm'>
											<button
												type='button'
												onClick={(event) => {
													event.stopPropagation()
													goToUserDetail(user)
												}}
												className='inline-flex items-center gap-1 rounded-md border border-slate-300 px-3 py-1.5 text-xs font-medium text-slate-700 transition hover:bg-slate-100'
											>
												Xem
												<ChevronRight size={14} />
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
							disabled={usersQuery.isFetching || isFirstPage || totalPages === 0}
							className='rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50'
						>
							Trước
						</button>

						{pageButtons.map((page) => (
							<button
								key={page}
								type='button'
								onClick={() => setCurrentPage(page)}
								disabled={usersQuery.isFetching}
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
							disabled={usersQuery.isFetching || isLastPage || totalPages === 0}
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

export default UsersPage
