import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuthContext } from '../contexts/AuthContext'
import { getRoleRedirectPath, isPathAllowedForRole } from '../utils/helpers'

function ProtectedRoute() {
	const location = useLocation()
	const { isAuthenticated, user } = useAuthContext()

	if (!isAuthenticated) {
		return <Navigate to='/login' state={{ from: location }} replace />
	}

	const role = user?.role

	if (!role) {
		return <Navigate to='/login' replace />
	}

	if (!isPathAllowedForRole(role, location.pathname)) {
		return <Navigate to={getRoleRedirectPath(role)} replace />
	}

	return <Outlet />
}

export default ProtectedRoute
