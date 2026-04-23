import { ROLE_REDIRECT_PATHS, ROLE_ROUTE_PATHS, ROLES } from './constants'

export const ROLE_NAV_ITEMS = Object.freeze({
	[ROLES.SYSADMIN]: Object.freeze([
		{ label: 'Dashboard', to: ROLE_ROUTE_PATHS[ROLES.SYSADMIN].dashboard },
		{ label: 'Quản lý người dùng', to: ROLE_ROUTE_PATHS[ROLES.SYSADMIN].users },
		{ label: 'Báo cáo thống kê', to: ROLE_ROUTE_PATHS[ROLES.SYSADMIN].statistics },
		{ label: 'Audit Logs', to: ROLE_ROUTE_PATHS[ROLES.SYSADMIN].auditLogs },
	]),
	[ROLES.LIBRARIAN]: Object.freeze([
		{ label: 'Dashboard', to: ROLE_ROUTE_PATHS[ROLES.LIBRARIAN].dashboard },
		{ label: 'Quản lý sách', to: ROLE_ROUTE_PATHS[ROLES.LIBRARIAN].books },
		{ label: 'Quản lý mượn trả sách', to: ROLE_ROUTE_PATHS[ROLES.LIBRARIAN].borrow },
		{ label: 'Người dùng', to: ROLE_ROUTE_PATHS[ROLES.LIBRARIAN].users },
	]),
	[ROLES.USER]: Object.freeze([
		{ label: 'Tìm sách', to: ROLE_ROUTE_PATHS[ROLES.USER].books },
		{ label: 'Lịch sử mượn trả', to: ROLE_ROUTE_PATHS[ROLES.USER].myBorrowed },
		{ label: 'Thông tin cá nhân', to: ROLE_ROUTE_PATHS[ROLES.USER].profile },
	]),
})

function normalizePath(pathname) {
	if (!pathname || pathname === '/') {
		return '/'
	}

	return pathname.endsWith('/') ? pathname.slice(0, -1) : pathname
}

export function getRoleRedirectPath(role) {
	return ROLE_REDIRECT_PATHS[role] ?? '/'
}

export function getRoleNavItems(role) {
	return ROLE_NAV_ITEMS[role] ?? []
}

export function getEffectiveRole(user) {
	if (!user) {
		return ROLES.USER
	}

	return user.role
}

export function isPathAllowedForRole(role, pathname) {
	const roleRoutePaths = Object.values(ROLE_ROUTE_PATHS[role] ?? {})

	if (!roleRoutePaths.length) {
		return false
	}

	const normalizedPath = normalizePath(pathname)

	return roleRoutePaths.some((routePath) => {
		const normalizedRoutePath = normalizePath(routePath)
		return (
			normalizedPath === normalizedRoutePath ||
			normalizedPath.startsWith(`${normalizedRoutePath}/`)
		)
	})
}
