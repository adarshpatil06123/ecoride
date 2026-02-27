'use client'

import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useAuth } from '@/context/AuthContext'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { useState } from 'react'
import { Leaf } from 'lucide-react'

const schema = z.object({
  email:    z.string().email('Invalid email'),
  password: z.string().min(1, 'Required'),
})

type FormData = z.infer<typeof schema>

export default function LoginPage() {
  const { login } = useAuth()
  const router = useRouter()
  const [error, setError] = useState<string | null>(null)

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  async function onSubmit(data: FormData) {
    setError(null)
    try {
      await login(data.email, data.password)
      router.push('/dashboard')
    } catch (e: unknown) {
      const msg = (e as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? 'Login failed'
      setError(msg)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="bg-white shadow-lg rounded-2xl p-8 w-full max-w-md">
        <div className="flex items-center gap-2 mb-6 text-brand-700">
          <Leaf className="w-6 h-6" />
          <span className="text-2xl font-bold">EcoRide AI</span>
        </div>

        <h1 className="text-xl font-semibold text-gray-800 mb-6">Sign in to your account</h1>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">College Email</label>
            <input
              {...register('email')}
              type="email"
              placeholder="you@nhce.edu"
              className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
            {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
            <input
              {...register('password')}
              type="password"
              className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
            {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password.message}</p>}
          </div>

          {error && (
            <div className="bg-red-50 text-red-700 rounded-lg px-3 py-2 text-sm">{error}</div>
          )}

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full bg-brand-600 hover:bg-brand-700 text-white font-semibold py-2 rounded-lg transition-colors disabled:opacity-60"
          >
            {isSubmitting ? 'Signing in…' : 'Sign In'}
          </button>
        </form>

        <p className="text-sm text-gray-500 mt-4 text-center">
          No account?{' '}
          <Link href="/register" className="text-brand-600 font-medium hover:underline">
            Register here
          </Link>
        </p>
      </div>
    </div>
  )
}
