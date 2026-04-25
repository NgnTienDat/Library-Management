import axiosInstance from './axios'

const BOOKS_BASE_PATH = '/api/v1/books'

function unwrapResponse(response) {
	return response?.data?.result ?? response?.data
}

function cleanParams(params = {}) {
	return Object.fromEntries(
		Object.entries(params).filter(([, value]) => value !== undefined && value !== null && value !== ''),
	)
}

function normalizeAuthors(authors = []) {
	if (!Array.isArray(authors)) {
		return []
	}

	return authors
		.map((author) => ({
			id: author?.id?.trim() || undefined,
			name: author?.name?.trim() || undefined,
		}))
		.filter((author) => author.id || author.name)
}

function buildMultipartBody(metadata, thumbnail) {
	const formData = new FormData()
	formData.append(
		'metadata',
		new Blob([JSON.stringify(metadata)], { type: 'application/json' }),
	)

	if (thumbnail) {
		formData.append('thumbnail', thumbnail)
	}

	return formData
}

export async function getBooks(params = {}) {
	const response = await axiosInstance.get(BOOKS_BASE_PATH, {
		params: cleanParams(params),
	})
	return unwrapResponse(response)
}

export async function getBookById(id) {
	const response = await axiosInstance.get(`${BOOKS_BASE_PATH}/${id}`)
	return unwrapResponse(response)
}

export async function verifyBarcode(barcode) {
	const response = await axiosInstance.get(`${BOOKS_BASE_PATH}/verify-barcode`, {
		params: cleanParams({ barcode }),
	})
	return unwrapResponse(response)
}

export async function createBook(data) {
	const copyBarcodes = Array.isArray(data?.copyBarcodes)
		? data.copyBarcodes.map((barcode) => barcode?.trim()).filter(Boolean)
		: []

	const metadata = {
		isbn: data?.isbn?.trim(),
		title: data?.title?.trim(),
		publisher: data?.publisher?.trim(),
		numberOfPages: Number(data?.numberOfPages || 0),
		description: data?.description?.trim(),
		categoryId: data?.categoryId?.trim(),
		copyBarcodes,
		authors: normalizeAuthors(data?.authors),
	}

	const response = await axiosInstance.post(
		BOOKS_BASE_PATH,
		buildMultipartBody(metadata, data?.thumbnail),
	)

	return unwrapResponse(response)
}

export async function updateBook(id, data) {
	const metadata = {
		title: data?.title?.trim(),
		publisher: data?.publisher?.trim(),
		numberOfPages: data?.numberOfPages !== undefined ? Number(data.numberOfPages) : undefined,
		description: data?.description?.trim(),
		totalCopies: data?.totalCopies !== undefined ? Number(data.totalCopies) : undefined,
		categoryId: data?.categoryId?.trim() || undefined,
		authors: normalizeAuthors(data?.authors),
	}

	const response = await axiosInstance.patch(
		`${BOOKS_BASE_PATH}/${id}`,
		buildMultipartBody(metadata, data?.thumbnail),
	)

	return unwrapResponse(response)
}

export async function deleteBook(id) {
	const response = await axiosInstance.delete(`${BOOKS_BASE_PATH}/${id}`)
	return unwrapResponse(response)
}

export async function reactivateBook(id) {
	const response = await axiosInstance.patch(`${BOOKS_BASE_PATH}/${id}/reactivate`)
	return unwrapResponse(response)
}
