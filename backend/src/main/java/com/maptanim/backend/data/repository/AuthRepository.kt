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
        val emailPrefix = email.substringBefore('@').takeIf { it.isNotBlank() }

        return try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val user = client.auth.currentUserOrNull()
                ?: return Result.failure(Exception("User not found."))

            // Insert into public.users with email and role
            try {
                val userRecord = User(
                    id = user.id,
                    email = email,
                    role = "FARMER"
                )
                client.postgrest["users"].insert(userRecord)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val profile = Profile(
                id = user.id,
                nickname = emailPrefix,
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
            val existingUser = client.auth.currentUserOrNull()
            if (existingUser != null) {
                return Result.success(Unit)
            }

            // Attempt standard anonymous sign-in first, or fallback to guest email provision
            val anonAttempt = runCatching {
                client.auth.signInAnonymously()
            }

            if (anonAttempt.isFailure) {
                // When anonymous sign-ins are restricted or database triggers require non-null email,
                // generate a dedicated local guest account:
                val guestSuffix = java.util.UUID.randomUUID().toString().replace("-", "").take(10)
                val guestEmail = "guest_${guestSuffix}@maptanim.local"
                val guestPassword = "Guest!_${guestSuffix}#2026"

                client.auth.signUpWith(Email) {
                    this.email = guestEmail
                    this.password = guestPassword
                }
            }

            val user = client.auth.currentUserOrNull()
                ?: return Result.failure(Exception("Guest user creation failed."))

            try {
                val userRecord = User(
                    id = user.id,
                    email = user.email ?: "guest_${user.id.take(8)}@maptanim.local",
                    role = "GUEST"
                )
                client.postgrest["users"].insert(userRecord)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val profile = Profile(
                    id = user.id,
                    nickname = "Guest Farmer",
                    avatar = "Avatar/Male_Avatar.png"
                )
                profileRepository.createProfile(profile)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            client.auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}