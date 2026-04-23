import { useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { toast } from 'sonner'
import {
	getBorrowingActivity,
	getInventoryStatus,
	getSystemTotals,
	getTopBorrowedBooks,
	getTopBorrowedCategories,
} from '../api/statistics.api'

function getErrorMessage(error) {
	return (
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Khong the tai du lieu thong ke. Vui long thu lai.'
	)
}

function useErrorToast(query) {
	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])
}

export function useSystemTotals() {
	const query = useQuery({
		queryKey: ['system-totals'],
		queryFn: getSystemTotals,
		staleTime: 5 * 60 * 1000,
		refetchOnWindowFocus: false,
	})

	useErrorToast(query)
	return query
}

export function useInventoryStatus() {
	const query = useQuery({
		queryKey: ['inventory-status'],
		queryFn: getInventoryStatus,
		staleTime: 5 * 60 * 1000,
		refetchOnWindowFocus: false,
	})

	useErrorToast(query)
	return query
}

export function useBorrowingActivity(params, options = {}) {
	const query = useQuery({
		queryKey: ['borrowing-activity', params],
		queryFn: () => getBorrowingActivity(params),
		enabled: Boolean(params?.from && params?.to),
		staleTime: 5 * 60 * 1000,
		refetchOnWindowFocus: false,
		...options,
	})

	useErrorToast(query)
	return query
}

export function useTopBorrowedBooks(limit = 10) {
	const query = useQuery({
		queryKey: ['top-borrowed-books', limit],
		queryFn: () => getTopBorrowedBooks(limit),
		staleTime: 5 * 60 * 1000,
		refetchOnWindowFocus: false,
	})

	useErrorToast(query)
	return query
}

export function useTopBorrowedCategories(params, options = {}) {
	const query = useQuery({
		queryKey: ['top-borrowed-categories', params],
		queryFn: () => getTopBorrowedCategories(params),
		enabled: Boolean(params?.from && params?.to),
		staleTime: 5 * 60 * 1000,
		refetchOnWindowFocus: false,
		...options,
	})

	useErrorToast(query)
	return query
}