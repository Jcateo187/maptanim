package com.maptanim.app.data.repository

import com.maptanim.app.data.model.Profile
import com.maptanim.app.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {

    private val client = SupabaseClient.client

    private val profileRepository = ProfileRepository()

    suspend fun signUp(

        firstName: String,

        lastName: String,

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

            val profile = Profile(

                id = user.id,

                first_name = firstName,

                last_name = lastName,

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