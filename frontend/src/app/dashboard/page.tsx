'use client'

import { useEffect, useState } from 'react'
import { useAuth } from '@/context/AuthContext'
import { carbonApi, predictionApi, trustApi, WalletDto, TrustProfileDto, PredictionDto } from '@/lib/api'
import AppShell from '@/components/AppShell'
import ProtectedRoute from '@/components/ProtectedRoute'
import { Leaf, Award, TrendingUp, Lightbulb } from 'lucide-react'
import clsx from 'clsx'

const BADGE_COLORS: Record<string, string> = {
  BRONZE:   'bg-amber-100 text-amber-700',
  SILVER:   'bg-slate-100 text-slate-700',
  GOLD:     'bg-yellow-100 text-yellow-700',
  PLATINUM: 'bg-cyan-100 text-cyan-700',
}

export default function DashboardPage() {
  return (
    <ProtectedRoute>
      <AppShell>
        <DashboardContent />
      </AppShell>
    </ProtectedRoute>
  )
}

function DashboardContent() {
  const { user } = useAuth()
  const [wallet, setWallet] = useState<WalletDto | null>(null)
  const [trust, setTrust] = useState<TrustProfileDto | null>(null)
  const [predictions, setPredictions] = useState<PredictionDto[]>([])
  const [loadingData, setLoadingData] = useState(true)

  useEffect(() => {
    if (!user) return
    Promise.all([
      carbonApi.wallet(),
      trustApi.getProfile(user.id),
      predictionApi.suggestions(),
    ])
      .then(([w, t, p]) => {
        setWallet(w)
        setTrust(t)
        setPredictions(p)
      })
      .catch(console.error)
      .finally(() => setLoadingData(false))
  }, [user])

  if (!user) return null

  const badge = user.badge ?? 'BRONZE'

  return (
    <div className="space-y-6">
      {/* Welcome */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Welcome back, {user.name} 👋</h1>
        <p className="text-gray-500 text-sm mt-1">{user.email} · {user.department} · Year {user.year}</p>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <StatCard
          icon={<Leaf className="w-5 h-5 text-brand-600" />}
          label="Carbon Credits"
          value={user.carbonCredits.toString()}
          sub="earned"
          color="brand"
        />
        <StatCard
          icon={<Award className="w-5 h-5 text-yellow-600" />}
          label="Trust Score"
          value={user.trustScore.toString()}
          sub={
            <span className={clsx('text-xs font-semibold px-2 py-0.5 rounded-full', BADGE_COLORS[badge])}>
              {badge}
            </span>
          }
          color="yellow"
        />
        <StatCard
          icon={<TrendingUp className="w-5 h-5 text-blue-600" />}
          label="Rides Completed"
          value={user.ridesCompleted.toString()}
          sub="total"
          color="blue"
        />
        <StatCard
          icon={<Leaf className="w-5 h-5 text-green-600" />}
          label="CO₂ Saved"
          value={wallet ? `${(wallet.totalCarbonSavedGrams / 1000).toFixed(2)} kg` : '—'}
          sub="lifetime"
          color="green"
        />
      </div>

      {/* Carbon wallet */}
      {wallet && (
        <section className="bg-white rounded-2xl shadow-sm border p-5">
          <h2 className="font-semibold text-gray-800 mb-3 flex items-center gap-2">
            <Leaf className="w-4 h-4 text-brand-600" /> Carbon Wallet
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
            <div>
              <p className="text-gray-500">Total Credits</p>
              <p className="text-xl font-bold text-brand-700">{wallet.totalCarbonSavedGrams ? Math.floor(wallet.totalCarbonSavedGrams / 100) : 0}</p>
            </div>
            <div>
              <p className="text-gray-500">CO₂ Saved</p>
              <p className="text-xl font-bold text-green-700">{(wallet.totalCarbonSavedGrams / 1000).toFixed(2)} kg</p>
            </div>
            <div>
              <p className="text-gray-500">Transactions</p>
              <p className="text-xl font-bold text-gray-700">{wallet.transactionCount ?? (wallet as { recentTransactions?: unknown[] }).recentTransactions?.length ?? 0}</p>
            </div>
          </div>
        </section>
      )}

      {/* Trust profile */}
      {trust && (
        <section className="bg-white rounded-2xl shadow-sm border p-5">
          <h2 className="font-semibold text-gray-800 mb-3 flex items-center gap-2">
            <Award className="w-4 h-4 text-yellow-600" /> Trust Profile
          </h2>
          <div className="flex items-center gap-4">
            <div className={clsx('px-4 py-2 rounded-xl font-bold text-lg', BADGE_COLORS[trust.badge ?? 'BRONZE'])}>
              {trust.badge ?? 'BRONZE'}
            </div>
            <div className="text-sm">
              <p><span className="text-gray-500">Score: </span><strong>{trust.trustScore}</strong></p>
              <p><span className="text-gray-500">Unique ride partners: </span><strong>{trust.uniqueRidePartners}</strong></p>
            </div>
          </div>
        </section>
      )}

      {/* Predictions */}
      {predictions.length > 0 && (
        <section className="bg-white rounded-2xl shadow-sm border p-5">
          <h2 className="font-semibold text-gray-800 mb-3 flex items-center gap-2">
            <Lightbulb className="w-4 h-4 text-purple-600" /> Smart Suggestions
          </h2>
          <div className="space-y-2">
            {predictions.map((p, i) => (
              <div key={i} className="bg-purple-50 border border-purple-100 rounded-lg px-4 py-3 text-sm text-purple-800">
                {p.message}
              </div>
            ))}
          </div>
        </section>
      )}

      {loadingData && (
        <div className="text-center text-gray-400 text-sm py-4 animate-pulse">Loading your stats…</div>
      )}
    </div>
  )
}

function StatCard({
  icon, label, value, sub, color,
}: {
  icon: React.ReactNode
  label: string
  value: string
  sub: React.ReactNode
  color: string
}) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border p-4">
      <div className="flex items-center gap-2 mb-2">{icon}<span className="text-xs text-gray-500 font-medium">{label}</span></div>
      <p className={clsx('text-2xl font-bold', {
        'text-brand-700': color === 'brand',
        'text-yellow-700': color === 'yellow',
        'text-blue-700':  color === 'blue',
        'text-green-700': color === 'green',
      })}>{value}</p>
      <div className="text-xs text-gray-400 mt-1">{sub}</div>
    </div>
  )
}
