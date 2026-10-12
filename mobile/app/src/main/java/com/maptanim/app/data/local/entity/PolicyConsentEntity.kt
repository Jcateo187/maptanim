package com.maptanim.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persists user policy consent in Room SQL database.
 * Retains acceptance across user sign-outs and app sessions until
 * explicitly unchecked by the user or deleted from Room database.
 */
@Entity(tableName = "policy_consents")
data class PolicyConsentEntity(
    @PrimaryKey
    val consentKey: String = KEY_TERMS_AND_PRIVACY,
    val isAccepted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val KEY_TERMS_AND_PRIVACY = "terms_and_privacy"
    }
}
