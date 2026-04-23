import { useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { toast } from 'sonner'
import { getAuditLogs } from '../api/audit.api'

const AUDIT_LOGS_QUERY_KEY = 'audit-logs'

function getErrorMessage(error) {
	return (
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Không thể tải audit logs. Vui lòng thử lại.'
	)
}

export function useAuditLogs(queryParams = {}) {
	const query = useQuery({
		queryKey: [AUDIT_LOGS_QUERY_KEY, queryParams],
		queryFn: () => getAuditLogs(queryParams),
		keepPreviousData: true,
	})

	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])

	return query
}
