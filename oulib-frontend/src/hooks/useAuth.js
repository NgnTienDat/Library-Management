import { useMutation } from '@tanstack/react-query'
import { toast } from 'sonner'
import { login as loginApi, register as registerApi } from '../api/auth.api'

function getErrorMessage(error) {
	return (
		error?.response?.data?.message ||
		error?.response?.data?.result?.message ||
		error?.message ||
		'Something went wrong. Please try again.'
	)
}

export function useLogin(options = {}) {
	return useMutation({
		mutationFn: loginApi,
		onSuccess: (data, variables, context) => {
			toast.success('Login successful')
			options.onSuccess?.(data, variables, context)
		},
		onError: (error, variables, context) => {
			toast.error(getErrorMessage(error))
			options.onError?.(error, variables, context)
		},
	})
}

export function useRegister(options = {}) {
	return useMutation({
		mutationFn: registerApi,
		onSuccess: (data, variables, context) => {
			toast.success('Registration successful. Please login.')
			options.onSuccess?.(data, variables, context)
		},
		onError: (error, variables, context) => {
			toast.error(getErrorMessage(error))
			options.onError?.(error, variables, context)
		},
	})
}
