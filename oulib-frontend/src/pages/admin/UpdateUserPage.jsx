import { useEffect, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { useUpdateUserStatus } from '../../hooks/useUsers'

function UpdateUserPage() {
	const { id } = useParams()
	const navigate = useNavigate()
	const location = useLocation()
	const updateStatus = useUpdateUserStatus()

	const user = location.state?.user
	const [status, setStatus] = useState('')

	useEffect(() => {
		if (user?.status) {
			setStatus(user.status)
		}
	}, [user])

	if (!user) {
		return (
			<div className="rounded-lg border border-amber-200 bg-amber-50 p-4 text-amber-800">
				Không có dữ liệu người dùng. Hãy quay lại danh sách và mở trang Update từ nút Update.
			</div>
		)
	}

	function handleSubmit(e) {
		e.preventDefault()

		updateStatus.mutate(
			{ id, status },
			{
				onSuccess: () => {
					alert('User updated successfully')
					navigate('/admin/users')
				},
			}
		)
	}

	return (
		<div className="max-w-xl space-y-5">
			<h1 className="text-2xl font-semibold text-slate-900">Update User</h1>

			<form
				onSubmit={handleSubmit}
				className="space-y-4 rounded-lg border border-slate-200 bg-white p-6 shadow-sm"
			>
				<div>
					<label className="text-sm font-medium text-slate-700">Full Name</label>
					<input
						value={user.fullName ?? ''}
						disabled
						className="mt-1 w-full rounded-md border border-slate-300 bg-slate-100 px-3 py-2 text-sm text-slate-600"
					/>
				</div>

				<div>
					<label className="text-sm font-medium text-slate-700">Email</label>
					<input
						value={user.email ?? ''}
						disabled
						className="mt-1 w-full rounded-md border border-slate-300 bg-slate-100 px-3 py-2 text-sm text-slate-600"
					/>
				</div>

				<div>
					<label className="text-sm font-medium text-slate-700">Role</label>
					<input
						value={user.role ?? ''}
						disabled
						className="mt-1 w-full rounded-md border border-slate-300 bg-slate-100 px-3 py-2 text-sm text-slate-600"
					/>
				</div>

				<div>
					<label className="text-sm font-medium text-slate-700">Status</label>
					<select
						value={status}
						onChange={(e) => setStatus(e.target.value)}
						className="mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2"
					>
						<option value="ACTIVE">ACTIVE</option>
						<option value="SUSPENDED">SUSPENDED</option>
						<option value="DELETED">DELETED</option>
						<option value="PRIVATE">PRIVATE</option>
					</select>
				</div>

				<div className="flex gap-3">
					<button
						type="submit"
						className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-500"
					>
						Update
					</button>

					<button
						type="button"
						onClick={() => navigate('/admin/users')}
						className="rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
					>
						Cancel
					</button>
				</div>
			</form>
		</div>
	)
}

export default UpdateUserPage