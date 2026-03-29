import { NavLink, useNavigate } from 'react-router-dom'
import { useAuthContext } from '../../contexts/AuthContext'
import { getRoleNavItems, getRoleRedirectPath } from '../../utils/helpers'

function Navbar() {
	const navigate = useNavigate()
	const { user, logout } = useAuthContext()
	const role = user?.role
	const navItems = getRoleNavItems(role)

	const handleLogout = () => {
		logout()
		navigate('/login', { replace: true })
	}

	return (
		<nav className='border-b border-slate-200 bg-white'>
			<div className='mx-auto flex w-full max-w-7xl flex-col gap-3 px-4 py-3 sm:flex-row sm:items-center sm:justify-between'>
				<NavLink
					to={getRoleRedirectPath(role)}
					className='text-lg font-semibold tracking-tight text-slate-900'
				>
					OU Library
				</NavLink>

				<div className='flex flex-wrap items-center gap-2 sm:justify-end'>
					{navItems.map((item) => (
						<NavLink
							key={item.to}
							to={item.to}
							className={({ isActive }) =>
								[
									'rounded-md px-3 py-2 text-sm font-medium transition',
									isActive
										? 'bg-slate-900 text-white'
										: 'text-slate-700 hover:bg-slate-100',
								].join(' ')
							}
						>
							{item.label}
						</NavLink>
					))}

					<button
						type='button'
						onClick={handleLogout}
						className='rounded-md bg-rose-600 px-3 py-2 text-sm font-medium text-white transition hover:bg-rose-500'
					>
						Đăng xuất
					</button>
				</div>
			</div>
		</nav>
	)
}

export default Navbar
