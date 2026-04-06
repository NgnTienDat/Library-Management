import { useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getUsers, updateUserStatus, createStaff } from "../api/users.api"
import { toast } from 'sonner'

const USERS_QUERY_KEY = 'users'

function getErrorMessage(error) {
	return (
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Không thể tải danh sách người dùng. Vui lòng thử lại.'
	)
}

export function useUsers(queryParams = {}) {
	const query = useQuery({
		queryKey: [USERS_QUERY_KEY, queryParams],
		queryFn: () => getUsers(queryParams),
    keepPreviousData: true,
	})

	useEffect(() => {
		if (query.error) {
			toast.error(getErrorMessage(query.error))
		}
	}, [query.error])

	return query
}

export function useUpdateUserStatus() {
	const queryClient = useQueryClient()

	return useMutation({
		mutationFn: ({ id, status }) => updateUserStatus(id, status),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [USERS_QUERY_KEY] })
			toast.success('Cập nhật trạng thái người dùng thành công')
		},
    
		onError: (error) => {
			toast.error(getErrorMessage(error))
		}
	})
}

export function useCreateStaff() {
	const queryClient = useQueryClient()

	return useMutation({
		mutationFn: createStaff,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [USERS_QUERY_KEY] })
      toast.success('Thêm nhân viên thành công')
		},
    
    onError: (error) => {
      toast.error(getErrorMessage(error))
    }
	})
}
