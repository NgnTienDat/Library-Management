import { ChevronDown, Loader2 } from 'lucide-react'
import { useEffect, useMemo, useRef, useState } from 'react'
import { toast } from 'sonner'
import { useCategories, useCreateCategory } from '../../hooks/useCategories'

function CategorySelectWithCreate({
	label = 'Thể loại',
	value,
	onChange,
	disabled = false,
	fallbackLabel = '',
	placeholder = 'Tìm hoặc chọn thể loại...',
}) {
	const [isOpen, setIsOpen] = useState(false)
	const [searchText, setSearchText] = useState('')
	const containerRef = useRef(null)

	const categoriesQuery = useCategories()
	const createCategoryMutation = useCreateCategory({
		onSuccess: (createdCategory) => {
			onChange?.(createdCategory?.id || '')
			setSearchText(createdCategory?.name || '')
			setIsOpen(false)
		},
	})

	const categories = Array.isArray(categoriesQuery.data) ? categoriesQuery.data : []
	const selectedCategory = useMemo(
		() => categories.find((category) => category.id === value),
		[categories, value],
	)

	const filteredCategories = useMemo(() => {
		const keyword = searchText.trim().toLowerCase()
		if (!keyword) {
			return categories
		}

		return categories.filter((category) => category.name?.toLowerCase().includes(keyword))
	}, [categories, searchText])

	useEffect(() => {
		if (selectedCategory?.name) {
			setSearchText(selectedCategory.name)
			return
		}

		if (!value && fallbackLabel) {
			setSearchText(fallbackLabel)
			return
		}

		if (!value && !fallbackLabel) {
			setSearchText('')
		}
	}, [selectedCategory?.name, value, fallbackLabel])

	useEffect(() => {
		function handleClickOutside(event) {
			if (containerRef.current && !containerRef.current.contains(event.target)) {
				setIsOpen(false)
			}
		}

		document.addEventListener('mousedown', handleClickOutside)
		return () => document.removeEventListener('mousedown', handleClickOutside)
	}, [])

	const handleInputChange = (event) => {
		const nextValue = event.target.value
		setSearchText(nextValue)
		setIsOpen(true)
		if (value) {
			onChange?.('')
		}
	}

	const handleSelectCategory = (category) => {
		onChange?.(category.id)
		setSearchText(category.name)
		setIsOpen(false)
	}

	const handleCreateCategory = () => {
		if (disabled || createCategoryMutation.isPending) {
			return
		}

		const rawName = window.prompt('Nhập tên thể loại mới')
		const name = rawName?.trim()

		if (!name) {
			return
		}

		const existedCategory = categories.find(
			(category) => category.name?.trim().toLowerCase() === name.toLowerCase(),
		)

		if (existedCategory) {
			onChange?.(existedCategory.id)
			setSearchText(existedCategory.name)
			setIsOpen(false)
			toast.info('Thể loại đã tồn tại, đã chọn tự động')
			return
		}

		createCategoryMutation.mutate({ name })
	}

	return (
		<label className='block text-sm font-medium text-slate-700'>
			{label}
			<div className='mt-1 flex gap-2'>
				<div ref={containerRef} className='relative flex-1'>
					<input
						type='text'
						value={searchText}
						onChange={handleInputChange}
						onFocus={() => !disabled && setIsOpen(true)}
						disabled={disabled}
						placeholder={placeholder}
						className='w-full rounded-md border border-slate-300 bg-white px-3 py-2 pr-9 text-sm outline-none ring-blue-500 focus:ring-2 disabled:cursor-not-allowed disabled:bg-slate-100'
					/>
					<ChevronDown size={16} className='pointer-events-none absolute right-3 top-2.5 text-slate-400' />

					{isOpen && !disabled ? (
						<div className='absolute z-20 mt-1 max-h-56 w-full overflow-auto rounded-md border border-slate-200 bg-white shadow-lg'>
							{categoriesQuery.isLoading ? (
								<div className='flex items-center gap-2 px-3 py-2 text-sm text-slate-500'>
									<Loader2 size={14} className='animate-spin' />
									Đang tải thể loại...
								</div>
							) : filteredCategories.length === 0 ? (
								<div className='px-3 py-2 text-sm text-slate-500'>Không tìm thấy thể loại phù hợp</div>
							) : (
								filteredCategories.map((category) => (
									<button
										key={category.id}
										type='button'
										onClick={() => handleSelectCategory(category)}
										className='block w-full px-3 py-2 text-left text-sm text-slate-700 transition hover:bg-slate-50'
									>
										{category.name}
									</button>
								))
							)}
						</div>
					) : null}
				</div>

				<button
					type='button'
					onClick={handleCreateCategory}
					disabled={disabled || createCategoryMutation.isPending}
					className='inline-flex items-center gap-2 rounded-md border border-slate-300 bg-white px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60'
				>
					{createCategoryMutation.isPending ? <Loader2 size={14} className='animate-spin' /> : null}
					Thể loại mới
				</button>
			</div>
		</label>
	)
}

export default CategorySelectWithCreate
