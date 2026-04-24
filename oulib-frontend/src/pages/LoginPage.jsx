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

function extractFieldErrors(error) {
	const validationResult = error?.response?.data?.result

	if (!validationResult || typeof validationResult !== 'object' || Array.isArray(validationResult)) {
		return {}
	}

	return Object.entries(validationResult).reduce((errors, [field, message]) => {
		if (typeof message === 'string' && message.trim()) {
			errors[field] = message
		}

		return errors
	}, {})
}

function LoginPage() {
	const navigate = useNavigate()
	const { login } = useAuthContext()
	const [formData, setFormData] = useState({
		email: '',
		password: '',
	})
	const [fieldErrors, setFieldErrors] = useState({})

	const loginMutation = useLogin({
		onSuccess: (response) => {
			if (!response?.authenticated || !response?.token) {
				toast.error('Invalid login response from server')
				return
			}

			login(response)
			navigate(getRoleRedirectPath(response?.user?.role), { replace: true })
		},
		onError: (error) => {
			setFieldErrors(extractFieldErrors(error))
		},
	})

	const handleChange = (event) => {
		const { name, value } = event.target

		setFormData((previous) => ({
			...previous,
			[name]: value,
		}))

		setFieldErrors((previous) => {
			if (!previous[name]) {
				return previous
			}

			const nextErrors = { ...previous }
			delete nextErrors[name]
			return nextErrors
		})
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
					error={fieldErrors.email}
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
					error={fieldErrors.password}
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
