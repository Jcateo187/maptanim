package com.maptanim.app.data.remote

import com.maptanim.app.data.remote.dto.DssRuleDto
import io.github.jan.supabase.postgrest.from

class DssRuleRemoteRepository {

    suspend fun getAllRules(): Result<List<DssRuleDto>> {
        return try {
            val rules = SupabaseClient.client
                .from("dss_rules")
                .select()
                .decodeList<DssRuleDto>()
            Result.success(rules)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
