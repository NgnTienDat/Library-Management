import { useEffect } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import {
	createBorrow,
	getBorrowRecordDetail,
	getBorrowRecords,
	getUserBorrowingHistory,
	returnBorrow,
} from '../api/borrow.api'

const BORROW_RECORDS_QUERY_KEY = 'borrow-records'

function getErrorMessage(error) {
	return (
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Có lỗi xảy ra. Vui lòng thử lại.'
	)
}

export function useBorrowRecords(queryParams = {}) {
	const query = useQuery({
		queryKey: [BORROW_RECORDS_QUERY_KEY, queryParams],
		queryFn: () => getBorrowRecords(queryParams),
	})

	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])

	return query
}

export function useBorrowRecordDetail(recordId) {
	const query = useQuery({
		queryKey: [BORROW_RECORDS_QUERY_KEY, 'detail', recordId],
		queryFn: () => getBorrowRecordDetail(recordId),
		enabled: Boolean(recordId),
	})

	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])

	return query
}

export function useUserBorrowingHistory(userId, status) {
	const query = useQuery({
		queryKey: [BORROW_RECORDS_QUERY_KEY, 'user-history', userId, status ?? 'ALL'],
		queryFn: () => getUserBorrowingHistory(userId, { status }),
		enabled: Boolean(userId),
	})

	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])

	return query
}

export function useCreateBorrow(options = {}) {
	const queryClient = useQueryClient()

	return useMutation({
		mutationFn: createBorrow,
		onSuccess: (data, variables, context) => {
			toast.success('Tạo lượt mượn thành công')
			queryClient.invalidateQueries({ queryKey: [BORROW_RECORDS_QUERY_KEY] })
			options.onSuccess?.(data, variables, context)
		},
		onError: (error, variables, context) => {
			toast.error(getErrorMessage(error))
			options.onError?.(error, variables, context)
		},
	})
}

export function useReturnBorrow(options = {}) {
	const queryClient = useQueryClient()

	return useMutation({
		mutationFn: returnBorrow,
		onSuccess: (data, variables, context) => {
			toast.success('Trả sách thành công')
			queryClient.invalidateQueries({ queryKey: [BORROW_RECORDS_QUERY_KEY] })
			options.onSuccess?.(data, variables, context)
		},
		onError: (error, variables, context) => {
			toast.error(getErrorMessage(error))
			options.onError?.(error, variables, context)
		},
	})
}
