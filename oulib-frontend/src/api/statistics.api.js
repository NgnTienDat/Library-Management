import axiosInstance from './axios'

const STATISTICS_BASE_PATH = '/api/v1/statistics'

function unwrapResponse(response) {
	return response?.data?.result ?? response?.data
}

export async function getSystemTotals() {
	const response = await axiosInstance.get(`${STATISTICS_BASE_PATH}/system-totals`)
	return unwrapResponse(response)
}

export async function getInventoryStatus() {
	const response = await axiosInstance.get(`${STATISTICS_BASE_PATH}/inventory-status`)
	return unwrapResponse(response)
}

export async function getBorrowingActivity({ from, to, groupBy = 'day' }) {
	const response = await axiosInstance.get(`${STATISTICS_BASE_PATH}/borrowing-activity`, {
		params: { from, to, groupBy },
	})
	return unwrapResponse(response)
}

export async function getTopBorrowedBooks(limit = 10) {
	const response = await axiosInstance.get(`${STATISTICS_BASE_PATH}/top-books`, {
		params: { limit },
	})
	return unwrapResponse(response)
}

export async function getTopBorrowedCategories({ from, to, limit = 5 }) {
	const response = await axiosInstance.get(`${STATISTICS_BASE_PATH}/top-categories`, {
		params: { from, to, limit },
	})
	return unwrapResponse(response)
}