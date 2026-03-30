import { useEffect } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { createCategory, getAllCategories } from '../api/categories.api'

const CATEGORIES_QUERY_KEY = 'categories'

function getErrorMessage(error) {
	return (
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Có lỗi xảy ra. Vui lòng thử lại.'
	)
}

export function useCategories() {
	const query = useQuery({
		queryKey: [CATEGORIES_QUERY_KEY],
		queryFn: getAllCategories,
	})

	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])

	return query
}

export function useCreateCategory(options = {}) {
	const queryClient = useQueryClient()

	return useMutation({
		mutationFn: createCategory,
		onSuccess: (data, variables, context) => {
			toast.success('Tạo thể loại thành công')
			queryClient.invalidateQueries({ queryKey: [CATEGORIES_QUERY_KEY] })
			options.onSuccess?.(data, variables, context)
		},
		onError: (error, variables, context) => {
			toast.error(getErrorMessage(error))
			options.onError?.(error, variables, context)
		},
	})
}
