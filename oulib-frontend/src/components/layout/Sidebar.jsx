import { useAuthContext } from '../../contexts/AuthContext'
import { ROLES } from '../../utils/constants'

const categories = ['Fiction', 'Science', 'Technology', 'History']

function Sidebar() {
	const { user } = useAuthContext()

	if (user?.role !== ROLES.USER) {
		return null
	}

	return (
		<aside className='h-[calc(100vh-4rem)] w-64 shrink-0 overflow-y-auto border-r border-slate-200 bg-white'>
			<div className='p-4'>
				<h2 className='text-sm font-semibold uppercase tracking-wide text-slate-500'>Categories</h2>
				<ul className='mt-3 space-y-2'>
					{categories.map((category) => (
						<li key={category} className='rounded-md bg-slate-50 px-3 py-2 text-sm text-slate-700'>
							{category}
						</li>
					))}
				</ul>
			</div>
		</aside>
	)
}

export default Sidebar
