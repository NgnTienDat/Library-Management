import { useAuthContext } from '../../contexts/AuthContext'
import { useCategories } from '../../hooks/useCategories'
import { ROLES } from '../../utils/constants'
import { getEffectiveRole } from '../../utils/helpers'

function Sidebar() {
	const { user } = useAuthContext()
	const role = getEffectiveRole(user)
	const categoriesQuery = useCategories()
	const categories = Array.isArray(categoriesQuery.data) ? categoriesQuery.data : []

	if (role !== ROLES.USER) {
		return null
	}

	return (
		<aside className='h-[calc(100vh-4rem)] w-64 shrink-0 overflow-y-auto border-r border-slate-200 bg-white'>
			<div className='p-4'>
				<h2 className='text-sm font-semibold uppercase tracking-wide text-slate-500'>Categories</h2>
				<ul className='mt-3 space-y-2'>
					{categoriesQuery.isLoading ? (
						<li className='rounded-md bg-slate-50 px-3 py-2 text-sm text-slate-500'>Đang tải thể loại...</li>
					) : categories.length === 0 ? (
						<li className='rounded-md bg-slate-50 px-3 py-2 text-sm text-slate-500'>Chưa có thể loại</li>
					) : (
						categories.map((category) => (
							<li
								key={category.id ?? category.name}
								className='rounded-md bg-slate-50 px-3 py-2 text-sm text-slate-700'
							>
								{category.name}
							</li>
						))
					)}
				</ul>
			</div>
		</aside>
	)
}

export default Sidebar
