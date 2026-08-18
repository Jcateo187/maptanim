package com.maptanim.app.data.remote

/**
 * Re-exports the backend module's shared SupabaseClient so that all mobile remote
 * data sources (BedRemoteDataSource, FarmRemoteRepository, etc.) use the same
 * authenticated Supabase session as AuthRepository.
 *
 * Previously this was a separate client instance which did NOT share the auth
 * session from login, causing all Postgrest writes to fail silently under RLS
 * because auth.uid() was null.
 */
object SupabaseClient {
    const val SUPABASE_URL = "https://ojilvcglpzbtpjxguhzj.supabase.co"
    const val SUPABASE_ANON_KEY = "sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU"

    val client get() = com.maptanim.backend.data.remote.SupabaseClient.client
}
