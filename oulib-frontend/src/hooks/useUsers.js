import { useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { toast } from 'sonner'
import { getUsers } from '../api/users.api'

const USERS_QUERY_KEY = 'users'

function getErrorMessage(error) {
	return (
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Khong the tai danh sach nguoi dung. Vui long thu lai.'
	)
}

export function useUsers(queryParams = {}) {
	const query = useQuery({
		queryKey: [USERS_QUERY_KEY, queryParams],
		queryFn: () => getUsers(queryParams),
	})

	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])

	return query
}
