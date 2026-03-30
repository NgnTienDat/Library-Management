import { useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { toast } from 'sonner'
import { getBorrowRecords } from '../api/borrow.api'

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
