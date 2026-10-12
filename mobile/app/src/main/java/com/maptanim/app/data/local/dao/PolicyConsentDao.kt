package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.PolicyConsentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PolicyConsentDao {

    @Query("SELECT * FROM policy_consents WHERE consentKey = :key LIMIT 1")
    fun getConsent(key: String): PolicyConsentEntity?

    @Query("SELECT * FROM policy_consents WHERE consentKey = 'terms_and_privacy' LIMIT 1")
    fun getDefaultConsent(): PolicyConsentEntity?

    @Query("SELECT * FROM policy_consents WHERE consentKey = :key LIMIT 1")
    fun observeConsent(key: String): Flow<PolicyConsentEntity?>

    @Query("SELECT * FROM policy_consents WHERE consentKey = 'terms_and_privacy' LIMIT 1")
    fun observeDefaultConsent(): Flow<PolicyConsentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveConsent(consent: PolicyConsentEntity)

    @Query("DELETE FROM policy_consents WHERE consentKey = :key")
    fun deleteConsent(key: String): Int
}
