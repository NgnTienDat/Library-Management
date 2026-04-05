import { useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { toast } from 'sonner'
import { getSystemTotals } from '../api/statistics.api'

const SYSTEM_TOTALS_QUERY_KEY = 'system-totals'

function getErrorMessage(error) {
	return (
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Khong the tai thong tin tong quan. Vui long thu lai.'
	)
}

export function useSystemTotals() {
	const query = useQuery({
		queryKey: [SYSTEM_TOTALS_QUERY_KEY],
		queryFn: getSystemTotals,
	})

	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])

	return query
}