import axiosInstance from './axios'

function unwrapResponse(response) {
	return response?.data?.result ?? response?.data
}

export async function login(data) {
	const response = await axiosInstance.post('/auth/login', data)
	return unwrapResponse(response)
}

export async function register(data) {
	const response = await axiosInstance.post('/api/v1/users', data)
	return unwrapResponse(response)
}
