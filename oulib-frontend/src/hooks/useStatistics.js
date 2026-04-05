import { useQuery } from '@tanstack/react-query'
import { getSystemTotals } from '../api/statistics.api'

export function useSystemTotals() {
	return useQuery({
		queryKey: ['system-totals'],
		queryFn: getSystemTotals,
		staleTime: 5 * 60 * 1000,
		refetchOnWindowFocus: false,
	})
}