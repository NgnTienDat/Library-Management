import { useEffect } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import {
	createBook,
	deleteBook,
	getBookById,
	getBooks,
	reactivateBook,
	updateBook,
} from '../api/books.api'

const BOOKS_QUERY_KEY = 'books'

function getErrorMessage(error) {
	return (
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Co loi xay ra. Vui long thu lai.'
	)
}

export function useBooks(queryParams = {}) {
	const query = useQuery({
		queryKey: [BOOKS_QUERY_KEY, queryParams],
		queryFn: () => getBooks(queryParams),
	})

	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])

	return query
}

export function useBookDetail(id) {
	const query = useQuery({
		queryKey: [BOOKS_QUERY_KEY, 'detail', id],
		queryFn: () => getBookById(id),
		enabled: Boolean(id),
	})

	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])

	return query
}

export function useCreateBook(options = {}) {
	const queryClient = useQueryClient()

	return useMutation({
		mutationFn: createBook,
		onSuccess: (data, variables, context) => {
			toast.success('Tao sach thanh cong')
			queryClient.invalidateQueries({ queryKey: [BOOKS_QUERY_KEY] })
			options.onSuccess?.(data, variables, context)
		},
		onError: (error, variables, context) => {
			toast.error(getErrorMessage(error))
			options.onError?.(error, variables, context)
		},
	})
}

export function useUpdateBook(options = {}) {
	const queryClient = useQueryClient()

	return useMutation({
		mutationFn: ({ id, data }) => updateBook(id, data),
		onSuccess: (data, variables, context) => {
			toast.success('Cap nhat sach thanh cong')
			queryClient.invalidateQueries({ queryKey: [BOOKS_QUERY_KEY] })
			queryClient.invalidateQueries({
				queryKey: [BOOKS_QUERY_KEY, 'detail', variables?.id],
			})
			options.onSuccess?.(data, variables, context)
		},
		onError: (error, variables, context) => {
			toast.error(getErrorMessage(error))
			options.onError?.(error, variables, context)
		},
	})
}

export function useDeleteBook(options = {}) {
	const queryClient = useQueryClient()

	return useMutation({
		mutationFn: deleteBook,
		onSuccess: (data, variables, context) => {
			toast.success('Ngung cung cap sach thanh cong')
			queryClient.invalidateQueries({ queryKey: [BOOKS_QUERY_KEY] })
			queryClient.invalidateQueries({
				queryKey: [BOOKS_QUERY_KEY, 'detail', variables],
			})
			options.onSuccess?.(data, variables, context)
		},
		onError: (error, variables, context) => {
			toast.error(getErrorMessage(error))
			options.onError?.(error, variables, context)
		},
	})
}

export function useReactivateBook(options = {}) {
	const queryClient = useQueryClient()

	return useMutation({
		mutationFn: reactivateBook,
		onSuccess: (data, variables, context) => {
			toast.success('Cung cap lai sach thanh cong')
			queryClient.invalidateQueries({ queryKey: [BOOKS_QUERY_KEY] })
			queryClient.invalidateQueries({
				queryKey: [BOOKS_QUERY_KEY, 'detail', variables],
			})
			options.onSuccess?.(data, variables, context)
		},
		onError: (error, variables, context) => {
			toast.error(getErrorMessage(error))
			options.onError?.(error, variables, context)
		},
	})
}
