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
