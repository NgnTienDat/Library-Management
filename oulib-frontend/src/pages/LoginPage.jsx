import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import AuthForm from '../components/common/AuthForm'
import FormInput from '../components/common/FormInput'
import PrimaryButton from '../components/common/PrimaryButton'
import AuthLayout from '../components/layout/AuthLayout'
import { useAuthContext } from '../contexts/AuthContext'
import { useLogin } from '../hooks/useAuth'
import { getRoleRedirectPath } from '../utils/helpers'

function LoginPage() {
	const navigate = useNavigate()
	const { login } = useAuthContext()
	const [formData, setFormData] = useState({
		email: '',
		password: '',
	})

	const loginMutation = useLogin({
		onSuccess: (response) => {
			if (!response?.authenticated || !response?.token) {
				toast.error('Invalid login response from server')
				return
			}

			login(response)
			navigate(getRoleRedirectPath(response?.user?.role), { replace: true })
		},
	})

	const handleChange = (event) => {
		const { name, value } = event.target
		setFormData((previous) => ({
			...previous,
			[name]: value,
		}))
	}

	const handleSubmit = (event) => {
		event.preventDefault()
		loginMutation.mutate(formData)
	}

	return (
		<AuthLayout
			title='Sign In'
			description='Use your account to continue to OULib.'
			footerText='Need an account?'
			footerLink={<Link to='/register' className='font-medium text-slate-900'>Register</Link>}
		>
			<AuthForm onSubmit={handleSubmit}>
				<FormInput
					id='email'
					name='email'
					type='email'
					label='Email'
					value={formData.email}
					onChange={handleChange}
					placeholder='you@example.com'
					required
					autoComplete='email'
				/>

				<FormInput
					id='password'
					name='password'
					type='password'
					label='Password'
					value={formData.password}
					onChange={handleChange}
					placeholder='Enter your password'
					required
					autoComplete='current-password'
				/>

				<PrimaryButton type='submit' isLoading={loginMutation.isPending}>
					Login
				</PrimaryButton>
			</AuthForm>
		</AuthLayout>
	)
}

export default LoginPage
