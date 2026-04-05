import axiosInstance from './axios'

const STATISTICS_BASE_PATH = '/api/v1/statistics'

function unwrapResponse(response) {
	return response?.data?.result ?? response?.data
}

export async function getSystemTotals() {
	const response = await axiosInstance.get(`${STATISTICS_BASE_PATH}/system-totals`)
	return unwrapResponse(response)
}