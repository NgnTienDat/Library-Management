export const ROLES = Object.freeze({
	SYSADMIN: 'SYSADMIN',
	LIBRARIAN: 'LIBRARIAN',
	USER: 'USER',
})

const SYSADMIN_ROUTE_PATHS = Object.freeze({
	dashboard: '/admin/dashboard',
	users: '/admin/users',
	statistics: '/admin/statistics',
	roles: '/admin/roles',
	id: '/admin/users/:id'
})

const LIBRARIAN_ROUTE_PATHS = Object.freeze({
	dashboard: '/librarian/dashboard',
	books: '/librarian/books',
	booksCreate: '/librarian/books/new',
	borrow: '/librarian/borrow',
	borrowCreate: '/librarian/borrow/new',
	borrowReturn: '/librarian/borrow/:recordId/return',
	return: '/librarian/return',
	users: '/librarian/users',
})

const USER_ROUTE_PATHS = Object.freeze({
	books: '/books',
	myBorrowed: '/my-borrowed',
	profile: '/profile',
})

export const ROLE_ROUTE_PATHS = Object.freeze({
	[ROLES.SYSADMIN]: SYSADMIN_ROUTE_PATHS,
	[ROLES.LIBRARIAN]: LIBRARIAN_ROUTE_PATHS,
	[ROLES.USER]: USER_ROUTE_PATHS,
})

export const ROLE_REDIRECT_PATHS = Object.freeze({
	[ROLES.SYSADMIN]: SYSADMIN_ROUTE_PATHS.dashboard,
	[ROLES.LIBRARIAN]: LIBRARIAN_ROUTE_PATHS.dashboard,
	[ROLES.USER]: USER_ROUTE_PATHS.books,
})

export const AUTH_TOKEN_COOKIE_KEY = 'oulib_access_token'
export const AUTH_USER_STORAGE_KEY = 'oulib_user'
