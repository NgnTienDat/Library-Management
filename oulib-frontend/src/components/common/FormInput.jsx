function FormInput({ label, id, type = 'text', error, className = '', ...props }) {
  const inputClassName = `w-full rounded-lg border px-3 py-2 text-slate-900 outline-none transition ${
    error
      ? 'border-red-500 focus:border-red-500 focus:ring-2 focus:ring-red-100'
      : 'border-slate-300 focus:border-slate-500 focus:ring-2 focus:ring-slate-200'
  } ${className}`

  return (
    <label htmlFor={id} className='block'>
      <span className='mb-1.5 block text-sm font-medium text-slate-700'>{label}</span>
      <input
        id={id}
        type={type}
        className={inputClassName}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? `${id}-error` : undefined}
        {...props}
      />
      {error ? (
        <span id={`${id}-error`} className='mt-1.5 block text-sm text-red-600'>
          {error}
        </span>
      ) : null}
    </label>
  )
}

export default FormInput
