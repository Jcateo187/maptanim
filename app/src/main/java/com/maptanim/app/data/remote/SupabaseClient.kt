package com.maptanim.app.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {

    val client = createSupabaseClient(

        supabaseUrl = "https://chihjcgqqvjembnlcudw.supabase.co",

        supabaseKey = "sb_publishable_nQXBi2EhfIbUpjZr2Wx1_Q_T2Duxiq1"

    ) {

        install(Auth) {

            autoLoadFromStorage = true

            alwaysAutoRefresh = true

        }

        install(Postgrest)

        install(Storage)

    }

}