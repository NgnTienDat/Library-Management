import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import AuthForm from '../components/common/AuthForm'
import FormInput from '../components/common/FormInput'
import PrimaryButton from '../components/common/PrimaryButton'
import AuthLayout from '../components/layout/AuthLayout'
import { useRegister } from '../hooks/useAuth'

function extractFieldErrors(error, fieldMapping = {}) {
  const validationResult = error?.response?.data?.result

  if (!validationResult || typeof validationResult !== 'object' || Array.isArray(validationResult)) {
    return {}
  }

  return Object.entries(validationResult).reduce((errors, [field, message]) => {
    const mappedField = fieldMapping[field] ?? field

    if (mappedField && typeof message === 'string' && message.trim()) {
      errors[mappedField] = message
    }

    return errors
  }, {})
}

function RegisterPage() {
  const navigate = useNavigate()
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
  })
  const [fieldErrors, setFieldErrors] = useState({})

  const registerMutation = useRegister({
    onSuccess: () => {
      navigate('/login', { replace: true })
    },
    onError: (error) => {
      setFieldErrors(
        extractFieldErrors(error, {
          fullName: 'username',
        }),
      )
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

    registerMutation.mutate({
      fullName: formData.username,
      email: formData.email,
      password: formData.password,
    })
  }

  return (
    <AuthLayout
      title='Create Account'
      description='Register a new account to access the library platform.'
      footerText='Already have an account?'
      footerLink={<Link to='/login' className='font-medium text-slate-900'>Login</Link>}
    >
      <AuthForm onSubmit={handleSubmit}>
        <FormInput
          id='username'
          name='username'
          label='Full Name'
          value={formData.username}
          onChange={handleChange}
          error={fieldErrors.username}
          placeholder='Enter your name'
          required
          autoComplete='name'
        />

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
          placeholder='Minimum 6 characters'
          required
          minLength={6}
          autoComplete='new-password'
        />

        <PrimaryButton type='submit' isLoading={registerMutation.isPending}>
          Register
        </PrimaryButton>
      </AuthForm>
    </AuthLayout>
  )
}

export default RegisterPage
