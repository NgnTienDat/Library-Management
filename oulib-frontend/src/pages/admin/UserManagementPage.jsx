import { useUsers } from "../../hooks/useUsers"
import { useUpdateUserStatus } from "../../hooks/useUsers"
import { Link } from 'react-router-dom'

function UserManagementPage() {
	const { data, isLoading } = useUsers()
	const updateStatus = useUpdateUserStatus()

	const users = data?.content ?? []

	if (isLoading) {
		return <div>Loading users...</div>
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
			<h1 className="text-2xl font-semibold">User Management</h1>

			<Link
				to="/admin/roles"
				className="rounded bg-green-600 px-4 py-2 text-white hover:bg-green-500"
			>
				Add Staff
			</Link>
		</div>
		
		<div className="overflow-x-auto rounded-xl border">
			<table className="w-full text-left">
				<thead className="bg-gray-50">
					<tr>
						<th className="p-3">ID</th>
						<th className="p-3">Full Name</th>
						<th className="p-3">Email</th>
						<th className="p-3">Role</th>
						<th className="p-3">Status</th>
						<th className="p-3">Created</th>
						<th className="p-3">Action</th>
					</tr>
				</thead>

				<tbody>
					{users.map((user) => (
					<tr key={user.id} className="border-t">
						<td className="p-3">{user.id}</td>
						<td className="p-3">{user.fullName}</td>
						<td className="p-3">{user.email}</td>
						<td className="p-3">{user.role}</td>
						<td className="p-3">{user.status}</td>
						<td className="p-3">
						{new Date(user.createdAt).toLocaleDateString()}
						</td>
						<td className="p-3">
							<Link
								to={`/admin/users/${user.id}`}
								state={{ user }}
								className="text-blue-600 hover:underline"
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
  )
}

export default UserManagementPage
