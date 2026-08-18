package com.maptanim.backend.data.repository

import com.maptanim.backend.data.model.Profile
import com.maptanim.backend.data.model.User
import com.maptanim.backend.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

class AuthRepository {

    private val client = SupabaseClient.client

    private val profileRepository = ProfileRepository()

    suspend fun signUp(

        email: String,

        password: String

    ): Result<Unit> {

        return try {

            client.auth.signUpWith(Email) {

                this.email = email

                this.password = password

            }

            val user = client.auth.currentUserOrNull()
                ?: return Result.failure(Exception("User not found."))

            // Insert into public.users (matches the updated Supabase table schema without full_name)
            try {
                val userRecord = User(
                    id = user.id,
                    email = email
                )
                client.postgrest["users"].insert(userRecord)
            } catch (e: Exception) {
                // User record may already exist (e.g. duplicate sign-up attempt), continue
                e.printStackTrace()
            }

            val profile = Profile(
                id = user.id,
                nickname = null,
                avatar = null
            )

            val profileResult = profileRepository.createProfile(profile)

            if (profileResult.isFailure) {
                // Profile might already be created by DB trigger, log error but don't fail user registration
                profileResult.exceptionOrNull()?.printStackTrace()
            }

            Result.success(Unit)

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure(e)

        }

    }

    suspend fun signIn(

        email: String,

        password: String

    ): Result<Unit> {

        return try {

            client.auth.signInWith(Email) {

                this.email = email

                this.password = password

            }

            Result.success(Unit)

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure(e)

        }

    }

    suspend fun signInAnonymously(): Result<Unit> {
        return try {
            client.auth.signInAnonymously()

            val user = client.auth.currentUserOrNull()
                ?: return Result.failure(Exception("Guest user creation failed."))

            try {
                val userRecord = User(
                    id = user.id,
                    email = user.email
                )
                client.postgrest["users"].insert(userRecord)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val profile = Profile(
                id = user.id,
                nickname = null,
                avatar = null
            )

            val profileResult = profileRepository.createProfile(profile)
            if (profileResult.isFailure) {
                profileResult.exceptionOrNull()?.printStackTrace()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

}