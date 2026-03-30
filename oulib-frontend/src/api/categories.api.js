import axiosInstance from './axios'

const CATEGORIES_BASE_PATH = '/api/v1/categories'

function unwrapResponse(response) {
	return response?.data?.result ?? response?.data
}

export async function getAllCategories() {
	const response = await axiosInstance.get(CATEGORIES_BASE_PATH)
	return unwrapResponse(response)
}

export async function createCategory(data) {
	const payload = {
		name: data?.name?.trim(),
	}

	const response = await axiosInstance.post(CATEGORIES_BASE_PATH, payload)
	return unwrapResponse(response)
}
