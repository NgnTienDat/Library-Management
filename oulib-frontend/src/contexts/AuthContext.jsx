import { createContext, useContext, useMemo, useState } from 'react'
import {
	AUTH_TOKEN_COOKIE_KEY,
	AUTH_USER_STORAGE_KEY,
} from '../utils/constants'
import { getCookie, removeCookie, setCookie } from '../utils/cookies'

const AuthContext = createContext(undefined)

function getStoredUser() {
	const rawUser = localStorage.getItem(AUTH_USER_STORAGE_KEY)

	if (!rawUser) {
		return null
	}

	try {
		return JSON.parse(rawUser)
	} catch {
		return null
	}
}

function getStoredToken() {
	return getCookie(AUTH_TOKEN_COOKIE_KEY)
}

export function AuthProvider({ children }) {
	const [user, setUser] = useState(() => getStoredUser())
	const [accessToken, setAccessToken] = useState(() => getStoredToken())

	const login = (authPayload) => {
		const nextUser = authPayload?.user ?? null
		const nextToken = authPayload?.token ?? null

		setUser(nextUser)
		setAccessToken(nextToken)

		if (nextUser) {
			localStorage.setItem(AUTH_USER_STORAGE_KEY, JSON.stringify(nextUser))
		} else {
			localStorage.removeItem(AUTH_USER_STORAGE_KEY)
		}

		if (nextToken) {
			setCookie(AUTH_TOKEN_COOKIE_KEY, nextToken)
		} else {
			removeCookie(AUTH_TOKEN_COOKIE_KEY)
		}
	}

	const logout = () => {
		setUser(null)
		setAccessToken(null)
		localStorage.removeItem(AUTH_USER_STORAGE_KEY)
		removeCookie(AUTH_TOKEN_COOKIE_KEY)
	}

	const value = useMemo(
		() => ({
			user,
			accessToken,
			isAuthenticated: Boolean(accessToken),
			login,
			logout,
		}),
		[accessToken, user],
	)

	return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuthContext() {
	const context = useContext(AuthContext)

	if (!context) {
		throw new Error('useAuthContext must be used within AuthProvider')
	}

	return context
}
