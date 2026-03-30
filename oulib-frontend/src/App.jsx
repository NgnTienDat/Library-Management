import { Toaster } from 'sonner'
import AppRouter from './routes/AppRouter'

function App() {
  return (
    <div className='min-h-screen bg-slate-100'>
      <AppRouter />
      <Toaster richColors position='bottom-right' />
    </div>
  )
}

export default App
