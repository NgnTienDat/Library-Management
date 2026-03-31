import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getUsers, updateUserStatus, createStaff } from "../api/users.api"

export function useUsers(params) {
  return useQuery({
    queryKey: ["users", params],
    queryFn: () => getUsers(params),
    keepPreviousData: true,
  })
}

export function useUpdateUserStatus() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, status }) => updateUserStatus(id, status),
    onSuccess: () => {
      queryClient.invalidateQueries(["users"])
    },
  })
}

export function useCreateStaff() {
	const queryClient = useQueryClient()

	return useMutation({
		mutationFn: createStaff,
		onSuccess: () => {
			queryClient.invalidateQueries(['users'])
		},
	})
}