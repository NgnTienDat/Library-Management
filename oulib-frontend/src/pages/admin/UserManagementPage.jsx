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
    updateStatus.mutate({
      id: userId,
      status: newStatus,
    })
  }

  return (
    <div className="space-y-6">
    	<h1 className="text-2xl font-semibold">User Management
			<Link
			to="/admin/roles"
			className="rounded bg-green-600 px-4 py-2 text-white"
		>
			Add Staff
		</Link>
		</h1>
		
		<div className="overflow-x-auto rounded-xl border">
			<table className="w-full text-left">
			<thead className="bg-gray-50">
				<tr>
				<th className="p-3">ID</th>
				<th className="p-3">Full Name</th>
				<th className="p-3">Email</th>
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
					<td className="p-3">{user.status}</td>
					<td className="p-3">
					{new Date(user.createdAt).toLocaleDateString()}
					</td>
					<td className="p-3">
					<select
						value={user.status}
						onChange={(e) =>
						handleStatusChange(user.id, e.target.value)
						}
						className="border rounded px-2 py-1"
					>
						<option value="ACTIVE">ACTIVE</option>
						<option value="SUSPENDED">SUSPENDED</option>
						<option value="DELETED">DELETED</option>
						<option value="PRIVATE">PRIVATE</option>
					</select>
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
