import Cookies from 'universal-cookie'

const cookies = new Cookies()

const defaultCookieOptions = {
  path: '/',
  maxAge: 60 * 60 * 24, // 1 day
  sameSite: 'lax',
}

export function getCookie(name) {
  return cookies.get(name) ?? null
}

export function setCookie(name, value, options = {}) {
  cookies.set(name, value, {
    ...defaultCookieOptions,
    ...options,
  })
}

export function removeCookie(name, options = {}) {
  cookies.remove(name, {
    path: defaultCookieOptions.path,
    ...options,
  })
}
