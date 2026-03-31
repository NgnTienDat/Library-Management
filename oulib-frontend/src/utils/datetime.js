const VIETNAM_TIME_ZONE = 'Asia/Ho_Chi_Minh'
const UTC_DATETIME_WITHOUT_ZONE_REGEX = /^(\d{4}-\d{2}-\d{2})[ T](\d{2}:\d{2}:\d{2}(?:\.\d+)?)$/

function normalizeDateInput(value) {
	if (typeof value !== 'string') return value

	const trimmed = value.trim()
	if (!trimmed) return ''

	const match = trimmed.match(UTC_DATETIME_WITHOUT_ZONE_REGEX)
	if (!match) return trimmed

	const [, datePart, timePart] = match
	return `${datePart}T${timePart}Z`
}

function parseDate(value) {
	if (!value) return null
	const parsedDate = new Date(normalizeDateInput(value))
	if (Number.isNaN(parsedDate.getTime())) return null
	return parsedDate
}

export function formatDateTime(value) {
	const parsedDate = parseDate(value)
	if (!parsedDate) return ''

	return parsedDate.toLocaleString('vi-VN', {
		timeZone: VIETNAM_TIME_ZONE,
	})
}

export function formatDate(value) {
	const parsedDate = parseDate(value)
	if (!parsedDate) return ''

	return parsedDate.toLocaleDateString('vi-VN', {
		timeZone: VIETNAM_TIME_ZONE,
	})
}

export function formatTime(value) {
	const parsedDate = parseDate(value)
	if (!parsedDate) return ''

	return parsedDate.toLocaleTimeString('vi-VN', {
		timeZone: VIETNAM_TIME_ZONE,
	})
}
