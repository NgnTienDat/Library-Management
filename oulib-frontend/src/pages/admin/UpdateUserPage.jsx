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
			<div className="rounded-xl border border-amber-200 bg-amber-50 p-4 text-amber-800">
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
		<div className="max-w-xl space-y-6">
			<h1 className="text-2xl font-semibold">Update User</h1>

			<form
				onSubmit={handleSubmit}
				className="space-y-4 rounded-xl border p-6"
			>
				<div>
					<label className="text-sm font-medium">Full Name</label>
					<input
						value={user.fullName ?? ''}
						disabled
						className="mt-1 w-full rounded border bg-gray-100 p-2"
					/>
				</div>

				<div>
					<label className="text-sm font-medium">Email</label>
					<input
						value={user.email ?? ''}
						disabled
						className="mt-1 w-full rounded border bg-gray-100 p-2"
					/>
				</div>

				<div>
					<label className="text-sm font-medium">Role</label>
					<input
						value={user.role ?? ''}
						disabled
						className="mt-1 w-full rounded border bg-gray-100 p-2"
					/>
				</div>

				<div>
					<label className="text-sm font-medium">Status</label>
					<select
						value={status}
						onChange={(e) => setStatus(e.target.value)}
						className="mt-1 w-full rounded border p-2"
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
						className="rounded bg-blue-600 px-4 py-2 text-white"
					>
						Update
					</button>

					<button
						type="button"
						onClick={() => navigate('/admin/users')}
						className="rounded bg-gray-400 px-4 py-2 text-white"
					>
						Cancel
					</button>
				</div>
			</form>
		</div>
	)
}

export default UpdateUserPage