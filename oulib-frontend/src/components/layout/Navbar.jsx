import { NavLink, useNavigate } from 'react-router-dom'
import { useAuthContext } from '../../contexts/AuthContext'
import { ROLE_ROUTE_PATHS, ROLES } from '../../utils/constants'
import { getEffectiveRole, getRoleNavItems, getRoleRedirectPath } from '../../utils/helpers'

function Navbar() {
	const navigate = useNavigate()
	const { user, isAuthenticated, logout } = useAuthContext()
	const role = getEffectiveRole(user)
	const navItems = getRoleNavItems(role)
	const guestBooksPath = ROLE_ROUTE_PATHS[ROLES.USER].books
	const visibleNavItems = isAuthenticated
		? navItems
		: navItems.filter((item) => item.to === guestBooksPath)

	const handleLogout = () => {
		logout()
		navigate('/', { replace: true })
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
					{visibleNavItems.map((item) => (
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

					{isAuthenticated ? (
						<button
							type='button'
							onClick={handleLogout}
							className='rounded-md bg-rose-600 px-3 py-2 text-sm font-medium text-white transition hover:bg-rose-500'
						>
							Đăng xuất
						</button>
					) : (
						<button
							type='button'
							onClick={() => navigate('/login')}
							className='rounded-md bg-slate-900 px-3 py-2 text-sm font-medium text-white transition hover:bg-slate-700'
						>
							Đăng nhập
						</button>
					)}
				</div>
			</div>
		</nav>
	)
}

export default Navbar
