function AuthLayout({
  title,
  description,
  children,
  footerText,
  footerLink,
}) {
  return (
    <div className='min-h-screen bg-slate-100 px-4 py-10'>
      <div className='mx-auto w-full max-w-md rounded-2xl border border-slate-200 bg-white p-8 shadow-sm'>
        <header className='mb-6 text-center'>
          <h1 className='text-2xl font-semibold text-slate-900'>{title}</h1>
          {description ? (
            <p className='mt-2 text-sm text-slate-600'>{description}</p>
          ) : null}
        </header>

        {children}

        {footerText || footerLink ? (
          <footer className='mt-6 text-center text-sm text-slate-600'>
            {footerText}
            {footerLink ? (
              <span>
                {' '}
                {footerLink}
              </span>
            ) : null}
          </footer>
        ) : null}
      </div>
    </div>
  )
}

export default AuthLayout
