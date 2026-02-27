'use client'

import { useState } from 'react'
import AppShell from '@/components/AppShell'
import ProtectedRoute from '@/components/ProtectedRoute'
import { rideApi, matchApi, CreateRideRequest, RideDto, MatchResultDto } from '@/lib/api'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { format } from 'date-fns'
import { Car, Search, Plus } from 'lucide-react'
import clsx from 'clsx'

// ── Create Ride Form ─────────────────────────────────────────────────────────
const createSchema = z.object({
  pickupZone:     z.string().min(1, 'Required'),
  departureTime:  z.string().min(1, 'Required'),  // datetime-local
  availableSeats: z.coerce.number().min(1).max(8),
})

// ── Search Form ───────────────────────────────────────────────────────────────
const searchSchema = z.object({
  zone: z.string().min(1, 'Required'),
  time: z.string().min(1, 'Required'),
})

type CreateForm = z.infer<typeof createSchema>
type SearchForm = z.infer<typeof searchSchema>

export default function RidesPage() {
  return (
    <ProtectedRoute>
      <AppShell>
        <RidesContent />
      </AppShell>
    </ProtectedRoute>
  )
}

function RidesContent() {
  const [tab, setTab] = useState<'search' | 'create'>('search')
  const [matches, setMatches] = useState<MatchResultDto[] | null>(null)
  const [createdRide, setCreatedRide] = useState<RideDto | null>(null)
  const [searchError, setSearchError] = useState<string | null>(null)
  const [createError, setCreateError] = useState<string | null>(null)

  // ── Create form ────────────────────────────────────────────────────────────
  const createForm = useForm<CreateForm>({ resolver: zodResolver(createSchema) })

  async function onCreateRide(data: CreateForm) {
    setCreateError(null)
    try {
      const body: CreateRideRequest = {
        pickupZone:    data.pickupZone,
        departureTime: new Date(data.departureTime).toISOString(),
        availableSeats: data.availableSeats,
      }
      const ride = await rideApi.create(body)
      setCreatedRide(ride)
      createForm.reset()
    } catch (e: unknown) {
      setCreateError(
        (e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Failed to create ride'
      )
    }
  }

  // ── Search form ────────────────────────────────────────────────────────────
  const searchForm = useForm<SearchForm>({ resolver: zodResolver(searchSchema) })

  async function onSearch(data: SearchForm) {
    setSearchError(null)
    setMatches(null)
    try {
      const isoTime = new Date(data.time).toISOString()
      const results = await matchApi.find(data.zone, isoTime)
      setMatches(results)
    } catch (e: unknown) {
      setSearchError(
        (e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Search failed'
      )
    }
  }

  async function handleJoin(rideId: string) {
    try {
      await rideApi.join(rideId)
      alert('Successfully requested to join the ride!')
    } catch (e: unknown) {
      alert((e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Join failed')
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
        <Car className="w-6 h-6 text-brand-600" /> Rides
      </h1>

      {/* Tab switcher */}
      <div className="flex rounded-xl overflow-hidden border w-fit">
        <button
          className={clsx('px-5 py-2 text-sm font-medium transition-colors flex items-center gap-1',
            tab === 'search' ? 'bg-brand-600 text-white' : 'bg-white text-gray-600 hover:bg-gray-50')}
          onClick={() => setTab('search')}
        >
          <Search className="w-4 h-4" /> Find a Ride
        </button>
        <button
          className={clsx('px-5 py-2 text-sm font-medium transition-colors flex items-center gap-1',
            tab === 'create' ? 'bg-brand-600 text-white' : 'bg-white text-gray-600 hover:bg-gray-50')}
          onClick={() => setTab('create')}
        >
          <Plus className="w-4 h-4" /> Offer a Ride
        </button>
      </div>

      {/* Search panel */}
      {tab === 'search' && (
        <div className="bg-white rounded-2xl shadow-sm border p-6 space-y-4">
          <h2 className="font-semibold text-gray-800">Find matching rides</h2>
          <form onSubmit={searchForm.handleSubmit(onSearch)} className="grid md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm text-gray-600 mb-1">Pickup Zone</label>
              <input
                {...searchForm.register('zone')}
                placeholder="e.g. A, B, C"
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500"
              />
              {searchForm.formState.errors.zone && (
                <p className="text-red-500 text-xs mt-1">{searchForm.formState.errors.zone.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm text-gray-600 mb-1">Departure Time</label>
              <input
                {...searchForm.register('time')}
                type="datetime-local"
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500"
              />
              {searchForm.formState.errors.time && (
                <p className="text-red-500 text-xs mt-1">{searchForm.formState.errors.time.message}</p>
              )}
            </div>
            <div className="flex items-end">
              <button
                type="submit"
                disabled={searchForm.formState.isSubmitting}
                className="w-full bg-brand-600 hover:bg-brand-700 text-white py-2 rounded-lg text-sm font-medium transition-colors disabled:opacity-60"
              >
                {searchForm.formState.isSubmitting ? 'Searching…' : 'Search'}
              </button>
            </div>
          </form>

          {searchError && (
            <div className="bg-red-50 text-red-700 rounded-lg px-3 py-2 text-sm">{searchError}</div>
          )}

          {/* Results */}
          {matches !== null && (
            <div className="space-y-3 mt-2">
              {matches.length === 0 ? (
                <p className="text-gray-500 text-sm">No matching rides found. Try a different zone or time.</p>
              ) : (
                <>
                  <p className="text-sm text-gray-500">Top {matches.length} match{matches.length > 1 ? 'es' : ''}</p>
                  {matches.map((m) => (
                    <MatchCard key={m.rideId} match={m} onJoin={handleJoin} />
                  ))}
                </>
              )}
            </div>
          )}
        </div>
      )}

      {/* Create panel */}
      {tab === 'create' && (
        <div className="bg-white rounded-2xl shadow-sm border p-6 space-y-4">
          <h2 className="font-semibold text-gray-800">Offer a ride</h2>

          {createdRide && (
            <div className="bg-brand-50 text-brand-700 rounded-lg px-4 py-3 text-sm font-medium">
              ✓ Ride created! ID: {createdRide.id}
            </div>
          )}

          <form onSubmit={createForm.handleSubmit(onCreateRide)} className="grid md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm text-gray-600 mb-1">Pickup Zone</label>
              <input
                {...createForm.register('pickupZone')}
                placeholder="e.g. A"
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500"
              />
              {createForm.formState.errors.pickupZone && (
                <p className="text-red-500 text-xs mt-1">{createForm.formState.errors.pickupZone.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm text-gray-600 mb-1">Departure Time</label>
              <input
                {...createForm.register('departureTime')}
                type="datetime-local"
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500"
              />
              {createForm.formState.errors.departureTime && (
                <p className="text-red-500 text-xs mt-1">{createForm.formState.errors.departureTime.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm text-gray-600 mb-1">Available Seats</label>
              <input
                {...createForm.register('availableSeats')}
                type="number"
                min={1}
                max={8}
                defaultValue={3}
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500"
              />
            </div>
            <div className="md:col-span-3">
              <button
                type="submit"
                disabled={createForm.formState.isSubmitting}
                className="bg-brand-600 hover:bg-brand-700 text-white px-6 py-2 rounded-lg text-sm font-medium transition-colors disabled:opacity-60"
              >
                {createForm.formState.isSubmitting ? 'Creating…' : 'Create Ride'}
              </button>
            </div>
          </form>

          {createError && (
            <div className="bg-red-50 text-red-700 rounded-lg px-3 py-2 text-sm">{createError}</div>
          )}
        </div>
      )}
    </div>
  )
}

function MatchCard({ match, onJoin }: { match: MatchResultDto; onJoin: (id: string) => void }) {
  const scoreColor =
    match.matchScore >= 80 ? 'text-green-700 bg-green-50' :
    match.matchScore >= 60 ? 'text-yellow-700 bg-yellow-50' :
    'text-gray-700 bg-gray-50'

  return (
    <div className="border rounded-xl p-4 flex items-center justify-between gap-4 hover:border-brand-300 transition-colors">
      <div className="flex-1">
        <p className="font-medium text-gray-800">{match.driverName}</p>
        <p className="text-xs text-gray-500 mt-0.5">
          Zone {match.pickupZone} · {format(new Date(match.departureTime), 'MMM d, HH:mm')} · {match.availableSeats} seat{match.availableSeats !== 1 ? 's' : ''}
        </p>
      </div>
      <div className={clsx('text-sm font-bold px-3 py-1.5 rounded-full', scoreColor)}>
        {match.matchScore}% match
      </div>
      <button
        onClick={() => onJoin(match.rideId)}
        className="bg-brand-600 hover:bg-brand-700 text-white text-sm px-4 py-1.5 rounded-lg font-medium transition-colors"
      >
        Join
      </button>
    </div>
  )
}
