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

            // Insert into public.profiles (stores nickname, avatar, and onboarding state)
            val profile = Profile(
                id = user.id,
                nickname = null,
                avatar = null,
                onboarding_completed = false
            )

            val profileResult = profileRepository.createProfile(profile)

            if (profileResult.isFailure) {

                return profileResult

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

}