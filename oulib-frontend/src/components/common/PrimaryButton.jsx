function PrimaryButton({ children, isLoading = false, disabled = false, ...props }) {
  return (
    <button
      type='button'
      disabled={isLoading || disabled}
      className='w-full rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:bg-slate-400'
      {...props}
    >
      {isLoading ? 'Please wait...' : children}
    </button>
  )
}

export default PrimaryButton
