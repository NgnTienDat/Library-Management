import { Outlet } from 'react-router-dom'
import { useAuthContext } from '../../contexts/AuthContext'
import { ROLES } from '../../utils/constants'
import Navbar from './Navbar'
import Sidebar from './Sidebar'

function MainLayout() {
	const { user } = useAuthContext()
	const role = user?.role

	return (
		<div className='min-h-screen bg-slate-100'>
			<Navbar />
			<div className='mx-auto flex w-full max-w-7xl'>
				{role === ROLES.USER ? <Sidebar /> : null}
				<main className='min-w-0 flex-1 p-4 sm:p-6'>
					<Outlet />
				</main>
			</div>
		</div>
	)
}

export default MainLayout