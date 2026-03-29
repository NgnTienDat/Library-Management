function FormInput({ label, id, type = 'text', ...props }) {
  return (
    <label htmlFor={id} className='block'>
      <span className='mb-1.5 block text-sm font-medium text-slate-700'>{label}</span>
      <input
        id={id}
        type={type}
        className='w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200'
        {...props}
      />
    </label>
  )
}

export default FormInput
