import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCreateStaff } from '../../hooks/useUsers'
import { ROLES } from '../../utils/constants'

function RoleManagementPage() {
	const navigate = useNavigate()
	const createStaffMutation = useCreateStaff()

	const [form, setForm] = useState({
		fullName: '',
		email: '',
		password: '',
		role: ROLES.LIBRARIAN,
	})

	function handleChange(e) {
		setForm({
			...form,
			[e.target.name]: e.target.value,
		})
	}

	function handleSubmit(e) {
		e.preventDefault()

		createStaffMutation.mutate(form, {
			onSuccess: () => {
				alert('Staff created successfully')
				navigate('/admin/users')
			},
			onError: (err) => {
				alert(
					err?.response?.data?.message ||
					'Failed to create staff'
				)
			},
		})
	}

	return (
		<div className="max-w-xl space-y-6">
			<h1 className="text-2xl font-semibold text-slate-900">
				Add Staff
			</h1>

			<form
				onSubmit={handleSubmit}
				className="space-y-4 rounded-xl border border-slate-200 bg-white p-6"
			>
				<div>
					<label className="block text-sm font-medium">
						Full Name
					</label>
					<input
						type="text"
						name="fullName"
						value={form.fullName}
						onChange={handleChange}
						required
						className="mt-1 w-full rounded border p-2"
					/>
				</div>

				<div>
					<label className="block text-sm font-medium">
						Email
					</label>
					<input
						type="email"
						name="email"
						value={form.email}
						onChange={handleChange}
						required
						className="mt-1 w-full rounded border p-2"
					/>
				</div>

				<div>
					<label className="block text-sm font-medium">
						Password
					</label>
					<input
					type="password"
					name="password"
					value={form.password}
					onChange={handleChange}
					required
					minLength={8}
					className="mt-1 w-full rounded border p-2"
					/>
				</div>

				<div>
					<label className="block text-sm font-medium">
						Role
					</label>
					<select
						name="role"
						value={form.role}
						onChange={handleChange}
						className="mt-1 w-full rounded border p-2"
					>
						<option value={ROLES.LIBRARIAN}>
							LIBRARIAN
						</option>
						<option value={ROLES.SYSADMIN}>
							SYSADMIN
						</option>
					</select>
				</div>

				<button
					type="submit"
					className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-500"
					disabled={createStaffMutation.isLoading}
				>
					{createStaffMutation.isLoading
						? 'Creating...'
						: 'Create Staff'}
				</button>
			</form>
		</div>
	)
}

export default RoleManagementPage