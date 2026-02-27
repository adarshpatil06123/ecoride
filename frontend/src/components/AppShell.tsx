'use client'

import Link from 'next/link'
import { useAuth } from '@/context/AuthContext'
import { Leaf, Car, BarChart2, Settings } from 'lucide-react'
import { usePathname } from 'next/navigation'
import clsx from 'clsx'

const NAV = [
  { href: '/dashboard',          label: 'Dashboard',   icon: BarChart2 },
  { href: '/rides',              label: 'Rides',       icon: Car       },
  { href: '/subscription',       label: 'Subscriptions', icon: Settings },
]

export default function AppShell({ children }: { children: React.ReactNode }) {
  const { user, logout } = useAuth()
  const pathname = usePathname()

  return (
    <div className="min-h-screen flex flex-col">
      {/* Top nav */}
      <header className="bg-brand-700 text-white shadow-md">
        <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
          <div className="flex items-center gap-2 font-bold text-xl tracking-tight">
            <Leaf className="w-5 h-5" />
            EcoRide AI
          </div>
          <nav className="hidden md:flex gap-6 text-sm font-medium">
            {NAV.map(({ href, label }) => (
              <Link
                key={href}
                href={href}
                className={clsx(
                  'hover:text-brand-200 transition-colors',
                  pathname?.startsWith(href) && 'text-brand-200 underline'
                )}
              >
                {label}
              </Link>
            ))}
          </nav>
          <div className="flex items-center gap-3 text-sm">
            <span className="opacity-80">{user?.name}</span>
            <button
              onClick={logout}
              className="bg-white text-brand-700 font-semibold px-3 py-1 rounded hover:bg-brand-100 transition-colors"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      {/* Mobile nav */}
      <nav className="md:hidden bg-brand-800 text-white flex justify-around text-xs py-2">
        {NAV.map(({ href, label, icon: Icon }) => (
          <Link
            key={href}
            href={href}
            className={clsx(
              'flex flex-col items-center gap-1 px-3 py-1 rounded',
              pathname?.startsWith(href) ? 'text-brand-300' : 'text-brand-100'
            )}
          >
            <Icon className="w-5 h-5" />
            {label}
          </Link>
        ))}
      </nav>

      <main className="flex-1 max-w-6xl mx-auto w-full px-4 py-6">
        {children}
      </main>
    </div>
  )
}
