import { Navigate, Route, Routes } from 'react-router-dom'
import MainLayout from '../components/layout/MainLayout'
import DashboardPage from '../pages/admin/DashboardPage'
import StatisticsPage from '../pages/admin/StatisticsPage'
import UserManagementPage from '../pages/admin/UserManagementPage'
import BorrowRecordsPage from '../pages/librarian/BorrowRecordsPage'
import LibrarianDashboardPage from '../pages/librarian/DashboardPage'
import ManageBooksPage from '../pages/librarian/ManageBooksPage'
import ReturnBooksPage from '../pages/librarian/ReturnBooksPage'
import UsersPage from '../pages/librarian/UsersPage'
import LoginPage from '../pages/LoginPage'
import RegisterPage from '../pages/RegisterPage'
import BookDetailPage from '../pages/user/BookDetailPage'
import BooksPage from '../pages/user/BooksPage'
import MyBorrowedPage from '../pages/user/MyBorrowedPage'
import ProfilePage from '../pages/user/ProfilePage'
import { ROLE_ROUTE_PATHS, ROLES } from '../utils/constants'
import ProtectedRoute from './ProtectedRoute'

const adminPaths = ROLE_ROUTE_PATHS[ROLES.SYSADMIN]
const librarianPaths = ROLE_ROUTE_PATHS[ROLES.LIBRARIAN]
const userPaths = ROLE_ROUTE_PATHS[ROLES.USER]

function AppRouter() {
	return (
		<Routes>
			<Route path='/' element={<Navigate to={userPaths.books} replace />} />
			<Route path='/login' element={<LoginPage />} />
			<Route path='/register' element={<RegisterPage />} />

			<Route element={<MainLayout />}>
				<Route path={userPaths.books} element={<BooksPage />} />
				<Route path={`${userPaths.books}/:bookId`} element={<BookDetailPage />} />

				<Route element={<ProtectedRoute />}>
					<Route path={adminPaths.dashboard} element={<DashboardPage />} />
					<Route path={adminPaths.users} element={<UserManagementPage />} />
					<Route path={adminPaths.statistics} element={<StatisticsPage />} />

					<Route path={librarianPaths.dashboard} element={<LibrarianDashboardPage />} />
					<Route path={librarianPaths.books} element={<ManageBooksPage />} />
					<Route path={librarianPaths.borrow} element={<BorrowRecordsPage />} />
					<Route path={librarianPaths.return} element={<ReturnBooksPage />} />
					<Route path={librarianPaths.users} element={<UsersPage />} />

					<Route path={userPaths.myBorrowed} element={<MyBorrowedPage />} />
					<Route path={userPaths.profile} element={<ProfilePage />} />
				</Route>
			</Route>

			<Route path='*' element={<Navigate to={userPaths.books} replace />} />
		</Routes>
	)
}

export default AppRouter
