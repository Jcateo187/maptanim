package com.maptanim.backend.data.repository

import com.maptanim.backend.data.model.Profile
import com.maptanim.backend.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class ProfileRepository {

    private val client = SupabaseClient.client

    suspend fun createProfile(

        profile: Profile

    ): Result<Unit> {

        return try {

            client.postgrest["profiles"]

                .upsert(profile)

            Result.success(Unit)

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure(e)

        }

    }

    suspend fun getProfile(

        id: String

    ): Profile? {

        return try {

            client.postgrest["profiles"]

                .select {

                    filter {

                        eq("id", id)

                    }

                }

                .decodeSingleOrNull<Profile>()

        } catch (e: Exception) {

            null

        }

    }

    suspend fun hasProfile(

        userId: String

    ): Boolean {

        return try {

            val profile = getProfile(userId)

            profile != null

        } catch (e: Exception) {

            false

        }

    }

    suspend fun updateProfile(

        userId: String,

        nickname: String,

        avatar: String? = null

    ): Result<Unit> {

        return try {

            client.postgrest["profiles"]

                .update(

                    {
                        set("nickname", nickname)
                        set("avatar", avatar)
                    }

                ) {

                    filter {

                        eq("id", userId)

                    }

                }

            Result.success(Unit)

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure(e)

        }

    }
}