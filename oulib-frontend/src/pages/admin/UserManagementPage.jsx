import { useUsers } from "../../hooks/useUsers"
import { useUpdateUserStatus } from "../../hooks/useUsers"
import { Link } from 'react-router-dom'

function UserManagementPage() {
	const { data, isLoading } = useUsers()
	const updateStatus = useUpdateUserStatus()

	const users = data?.content ?? []

	if (isLoading) {
		return (
			<div className="rounded-lg border border-slate-200 bg-white p-6 text-sm text-slate-600 shadow-sm">
				Loading users...
			</div>
		)
	}

	function handleStatusChange(userId, newStatus) {
		const confirmed = window.confirm("Are you sure you want to update this user's status?")

		if (!confirmed) return

		updateStatus.mutate({
			id: userId,
			status: newStatus,
		})
	}

  return (
    <div className="space-y-6">
		<div className="flex items-center justify-between">
			<h1 className="text-2xl font-semibold text-slate-900">User Management</h1>

			<Link
				to="/admin/roles"
				className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-500"
			>
				Add Staff
			</Link>
		</div>
		
		<div className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
			<div className="overflow-x-auto">
			<table className="min-w-full divide-y divide-slate-200">
				<thead className="bg-slate-50">
					<tr>
						<th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600">ID</th>
						<th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600">Full Name</th>
						<th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600">Email</th>
						<th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600">Role</th>
						<th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600">Status</th>
						<th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600">Created</th>
						<th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600">Action</th>
					</tr>
				</thead>

				<tbody className="divide-y divide-slate-100">
					{users.map((user) => (
					<tr key={user.id} className="hover:bg-slate-50">
						<td className="px-4 py-3 text-sm text-slate-700">{user.id}</td>
						<td className="px-4 py-3 text-sm text-slate-800">{user.fullName}</td>
						<td className="px-4 py-3 text-sm text-slate-700">{user.email}</td>
						<td className="px-4 py-3 text-sm text-slate-700">{user.role}</td>
						<td className="px-4 py-3 text-sm text-slate-700">{user.status}</td>
						<td className="px-4 py-3 text-sm text-slate-700">
						{new Date(user.createdAt).toLocaleDateString()}
						</td>
						<td className="px-4 py-3 text-sm">
							<Link
								to={`/admin/users/${user.id}`}
								state={{ user }}
								className="inline-flex rounded-md border border-slate-300 px-3 py-1.5 text-xs font-medium text-slate-700 transition hover:bg-slate-100"
							>
								Update
							</Link>
						</td>
					</tr>
					))}
				</tbody>
			</table>
			</div>
		</div>
	</div>
  )
}

export default UserManagementPage
