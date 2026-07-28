package com.maptanim.app.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {

    val client = createSupabaseClient(

        supabaseUrl = "https://ojilvcglpzbtpjxguhzj.supabase.co",

        supabaseKey = "sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU"

    ) {

        install(Auth) {

            autoLoadFromStorage = true

            alwaysAutoRefresh = true

        }

        install(Postgrest)

        install(Storage)

    }

}