package com.maptanim.app.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.maptanim.app.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FarmPreferencesManager(context: Context? = null) {

    private val ctx: Context? = context?.applicationContext ?: RepositoryProvider.appContext
    private val prefs: SharedPreferences? = ctx?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _activeFarmChanges = MutableSharedFlow<Pair<String?, String>>(extraBufferCapacity = 5)
    val activeFarmChanges: SharedFlow<Pair<String?, String>> = _activeFarmChanges.asSharedFlow()

    fun getActiveFarmId(userId: String?): String? {
        val key = getKeyForUser(userId)
        return prefs?.getString(key, null)
    }

    fun setActiveFarmId(userId: String?, farmId: String) {
        val key = getKeyForUser(userId)
        prefs?.edit()?.putString(key, farmId)?.apply()
        _activeFarmChanges.tryEmit(Pair(userId, farmId))
    }

    fun clearActiveFarmId(userId: String?) {
        val key = getKeyForUser(userId)
        prefs?.edit()?.remove(key)?.apply()
    }

    fun getFarmEnvironment(farmId: String): com.maptanim.app.domain.model.FarmEnvironment {
        val zoneStr = prefs?.getString("farm_zone_$farmId", null)
        val soilStr = prefs?.getString("farm_soil_$farmId", null)
        val width = prefs?.getFloat("farm_w_$farmId", 10f) ?: 10f
        val height = prefs?.getFloat("farm_h_$farmId", 8f) ?: 8f
        val constraintsSet = prefs?.getStringSet("farm_constraints_$farmId", null)

        val zone = com.maptanim.app.domain.model.AgroZone.fromName(zoneStr)
        val soil = try {
            if (soilStr != null) com.maptanim.app.domain.model.SoilType.valueOf(soilStr) else com.maptanim.app.domain.model.SoilType.LOAM
        } catch (_: Exception) {
            com.maptanim.app.domain.model.SoilType.LOAM
        }
        val constraints = constraintsSet?.mapNotNull { com.maptanim.app.domain.model.SiteConstraint.fromCode(it) }?.toSet() ?: emptySet()
        val season = prefs?.getString("farm_season_$farmId", "Wet / Rainy Season") ?: "Wet / Rainy Season"
        val materials = prefs?.getStringSet("farm_materials_$farmId", null) ?: emptySet()

        return com.maptanim.app.domain.model.FarmEnvironment(
            zone = zone,
            defaultSoil = soil,
            widthM = width,
            heightM = height,
            constraints = constraints,
            season = season,
            availableMaterials = materials
        )
    }

    fun isFarmCalibrated(farmId: String): Boolean {
        return prefs?.getBoolean("farm_calibrated_$farmId", false) ?: false
    }

    fun setFarmCalibrated(farmId: String, calibrated: Boolean) {
        prefs?.edit()?.putBoolean("farm_calibrated_$farmId", calibrated)?.apply()
    }

    fun saveFarmEnvironment(farmId: String, env: com.maptanim.app.domain.model.FarmEnvironment) {
        prefs?.edit()
            ?.putString("farm_zone_$farmId", env.zone.name)
            ?.putString("farm_soil_$farmId", env.defaultSoil.name)
            ?.putFloat("farm_w_$farmId", env.widthM)
            ?.putFloat("farm_h_$farmId", env.heightM)
            ?.putStringSet("farm_constraints_$farmId", env.constraints.map { it.code }.toSet())
            ?.putString("farm_season_$farmId", env.season)
            ?.putStringSet("farm_materials_$farmId", env.availableMaterials)
            ?.putBoolean("farm_calibrated_$farmId", true)
            ?.apply()
    }

    private fun getKeyForUser(userId: String?): String {
        return if (userId.isNullOrBlank()) "active_farm_id_guest" else "active_farm_id_$userId"
    }

    companion object {
        private const val PREFS_NAME = "maptanim_farm_prefs"

        @Volatile
        private var instance: FarmPreferencesManager? = null

        fun getInstance(context: Context? = null): FarmPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: FarmPreferencesManager(context).also { instance = it }
            }
        }
    }
}
