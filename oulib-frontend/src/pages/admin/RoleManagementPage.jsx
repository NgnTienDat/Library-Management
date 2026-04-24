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
		<div className="max-w-xl space-y-5">
			<div>
				<h1 className="text-2xl font-semibold text-slate-900">
					Add Staff
				</h1>
				<p className="mt-1 text-sm text-slate-600">
					Create librarian or sysadmin account for internal operations.
				</p>
			</div>

			<form
				onSubmit={handleSubmit}
				className="space-y-4 rounded-lg border border-slate-200 bg-white p-6 shadow-sm"
			>
				<div>
					<label className="block text-sm font-medium text-slate-700">
						Full Name
					</label>
					<input
						type="text"
						name="fullName"
						value={form.fullName}
						onChange={handleChange}
						required
						className="mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2"
					/>
				</div>

				<div>
					<label className="block text-sm font-medium text-slate-700">
						Email
					</label>
					<input
						type="email"
						name="email"
						value={form.email}
						onChange={handleChange}
						required
						className="mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2"
					/>
				</div>

				<div>
					<label className="block text-sm font-medium text-slate-700">
						Password
					</label>
					<input
					type="password"
					name="password"
					value={form.password}
					onChange={handleChange}
					required
					minLength={8}
					className="mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2"
					/>
				</div>

				<div>
					<label className="block text-sm font-medium text-slate-700">
						Role
					</label>
					<select
						name="role"
						value={form.role}
						onChange={handleChange}
						className="mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none ring-blue-500 focus:ring-2"
					>
						<option value={ROLES.LIBRARIAN}>
							LIBRARIAN
						</option>
						<option value={ROLES.SYSADMIN}>
							SYSADMIN
						</option>
					</select>
				</div>

				<div className="pt-2">
					<button
						type="submit"
						className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-500"
						disabled={createStaffMutation.isLoading}
					>
						{createStaffMutation.isLoading
							? 'Creating...'
							: 'Create Staff'}
					</button>
				</div>
			</form>
		</div>
	)
}

export default RoleManagementPage