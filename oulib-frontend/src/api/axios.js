import axios from 'axios'
import { AUTH_TOKEN_COOKIE_KEY } from '../utils/constants'
import { getCookie } from '../utils/cookies'

const baseURL =
	import.meta.env.VITE_API_BASE_URL?.trim() || 'http://localhost:8080'

const axiosInstance = axios.create({
	baseURL,
})

axiosInstance.interceptors.request.use(
	(config) => {
		const token = getCookie(AUTH_TOKEN_COOKIE_KEY)

		if (token) {
			config.headers.Authorization = `Bearer ${token}`
		}

		return config
	},
	(error) => Promise.reject(error),
)

export default axiosInstance
