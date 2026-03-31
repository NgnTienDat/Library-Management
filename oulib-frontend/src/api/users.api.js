import axiosInstance from "./axios"

function unwrapResponse(response) {
  return response?.data?.result ?? null
}

export async function getUsers(params = {}) {
  const response = await axiosInstance.get("/api/v1/users", { params })
  return unwrapResponse(response)
}

export async function updateUserStatus(id, status) {
  const response = await axiosInstance.patch(`/api/v1/users/${id}/status`, {
    status,
  })
  return unwrapResponse(response)
}

export async function createStaff(data) {
	const response = await axiosInstance.post('/api/v1/users/staff', data)
	return response?.data?.result ?? null
}