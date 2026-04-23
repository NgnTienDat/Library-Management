import axiosInstance from './axios'

const AUDIT_LOGS_BASE_PATH = '/api/v1/audit-logs'

function unwrapResponse(response) {
	return response?.data?.result ?? response?.data
}

function cleanParams(params = {}) {
	return Object.fromEntries(
		Object.entries(params).filter(([, value]) => value !== undefined && value !== null && value !== ''),
	)
}

export async function getAuditLogs(params = {}) {
	const response = await axiosInstance.get(AUDIT_LOGS_BASE_PATH, {
		params: cleanParams(params),
	})

	return unwrapResponse(response)
}
