import { useNavigate } from 'react-router-dom'

function LandingPage() {
  const navigate = useNavigate()

  return (
    <div className='flex min-h-screen items-center justify-center bg-slate-100 px-4'>
      <div className='w-full max-w-md rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm'>
        <h1 className='text-3xl font-bold text-slate-900'>OU Library</h1>
        <p className='mt-2 text-sm text-slate-600'>Library management for staff and readers.</p>

        <div className='mt-8 grid grid-cols-1 gap-3 sm:grid-cols-2'>
          <button
            type='button'
            onClick={() => navigate('/login')}
            className='rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-slate-700'
          >
            Login
          </button>
          <button
            type='button'
            onClick={() => navigate('/register')}
            className='rounded-lg border border-slate-300 px-4 py-2.5 text-sm font-medium text-slate-900 transition hover:bg-slate-50'
          >
            Register
          </button>
        </div>
      </div>
    </div>
  )
}

export default LandingPage
