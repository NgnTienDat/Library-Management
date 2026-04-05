import axiosInstance from './axios'

const USERS_BASE_PATH = '/api/v1/users'

function unwrapResponse(response) {
	return response?.data?.result ?? response?.data
}

function cleanParams(params = {}) {
	return Object.fromEntries(
		Object.entries(params).filter(([, value]) => value !== undefined && value !== null && value !== ''),
	)
}

export async function getUsers(params = {}) {
	const response = await axiosInstance.get(USERS_BASE_PATH, {
		params: cleanParams(params),
	})

	return unwrapResponse(response)
}

export async function updateUserStatus(id, status) {
  const response = await axiosInstance.patch(`${USERS_BASE_PATH}/${id}/status`, {
    status,
  })
  return unwrapResponse(response)
}

export async function createStaff(data) {
	const response = await axiosInstance.post(`${USERS_BASE_PATH}/staff`, data)
	return unwrapResponse(response)
}
