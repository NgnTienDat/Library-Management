import axiosInstance from './axios'

const BORROW_BASE_PATH = '/api/v1/borrowing'

function unwrapResponse(response) {
	return response?.data?.result ?? response?.data
}

function cleanParams(params = {}) {
	return Object.fromEntries(
		Object.entries(params).filter(([, value]) => value !== undefined && value !== null && value !== ''),
	)
}

export async function getBorrowRecords(params = {}) {
	const response = await axiosInstance.get(`${BORROW_BASE_PATH}/records`, {
		params: cleanParams(params),
	})

	return unwrapResponse(response)
}

export async function getBorrowRecordDetail(recordId) {
	const response = await axiosInstance.get(`${BORROW_BASE_PATH}/records/${recordId}`)
	return unwrapResponse(response)
}

export async function returnBorrow(payload) {
	const response = await axiosInstance.post(`${BORROW_BASE_PATH}/return`, payload)
	return unwrapResponse(response)
}

export async function createBorrow(payload) {
	const response = await axiosInstance.post(BORROW_BASE_PATH, payload)
	return unwrapResponse(response)
}
