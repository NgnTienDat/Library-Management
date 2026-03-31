import axiosInstance from './axios'

function unwrapResponse(response) {
	return response?.data?.result ?? null
}

export async function getSystemTotals() {
	const response = await axiosInstance.get('/api/v1/statistics/system-totals')
	return unwrapResponse(response)
}