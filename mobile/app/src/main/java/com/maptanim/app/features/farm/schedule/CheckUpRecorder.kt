package com.maptanim.app.features.farm.schedule

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.CropLog
import com.maptanim.app.domain.model.LogContext
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.dialogs.dss_checkup.CheckUpActivity
import com.maptanim.app.features.farm.dialogs.dss_checkup.CheckUpOption
import com.maptanim.app.features.farm.dialogs.dss_checkup.DirectCropSolution
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

/** Severity of a check-up result, stored in CropLog.selectedChoice as A / B / C. */
enum class CheckUpSeverity(val code: String, val label: String, val color: Color) {
    NORMAL("A", "Maayos", Color(0xFF2E7D32)),
    NEEDS_CARE("B", "Kailangan ng Alaga", Color(0xFFE65100)),
    URGENT("C", "Agarang Aksyon", Color(0xFFC62828)),
    UNKNOWN("", "Naitala", Color(0xFF546E7A));

    companion object {
        fun fromCode(code: String?): CheckUpSeverity =
            entries.firstOrNull { it.code.isNotEmpty() && it.code == code } ?: UNKNOWN
    }
}

/** Human-readable view of a saved check-up log (handles new and legacy formats). */
data class CheckUpLogSummary(
    val category: String,
    val resultTitle: String?,
    val details: String?,
    val severity: CheckUpSeverity
)

/**
 * Single save path for Vegetables Check Up results.
 * Format (no DB migration needed):
 * - selectedChoice = severity code (A/B/C), compatible with DssLogEvaluator
 * - notes          = "Kategorya: …\nResulta: …\n<direct answer>"
 * - date           = today (check-ups are always for the plant as it is now)
 */
object CheckUpRecorder {

    private const val CATEGORY_PREFIX = "Kategorya:"
    private const val RESULT_PREFIX = "Resulta:"

    fun severityFor(selected: List<CheckUpOption>, solution: DirectCropSolution?): CheckUpSeverity {
        if (selected.isNotEmpty() && selected.all { it.isIdeal }) return CheckUpSeverity.NORMAL
        val s = solution?.severity.orEmpty().lowercase()
        if (s.contains("ideal") || s.contains("normal") || s.contains("maayos")) return CheckUpSeverity.NORMAL
        val urgentWords = listOf("critical", "kritikal", "agad", "immediate", "napakataas", "mataas", "high", "urgent", "danger")
        return if (urgentWords.any { s.contains(it) }) CheckUpSeverity.URGENT else CheckUpSeverity.NEEDS_CARE
    }

    fun buildLog(
        cropName: String,
        varietyName: String,
        gardenLabel: String,
        plotId: String,
        stage: ManagementStage,
        activity: CheckUpActivity,
        selected: List<CheckUpOption>,
        solution: DirectCropSolution?,
        today: LocalDate = LocalDate.now(),
        farmId: String = ""
    ): CropLog {
        val severity = severityFor(selected, solution)
        val resultTitle = solution?.title?.takeIf { it.isNotBlank() } ?: "Sinuri ang kalagayan ng pananim"
        val answer = solution?.directAnswer?.take(220).orEmpty()
        val resolvedFarmId = farmId.ifBlank {
            try {
                val db = RepositoryProvider.getDatabase()
                val plot = if (plotId.isNotBlank()) db?.cropPlotDao()?.getPlotById(plotId) else null
                plot?.farmId ?: db?.farmDao()?.getActiveFarm()?.id ?: ""
            } catch (_: Exception) {
                ""
            }
        }
        return CropLog(
            id = "log-${UUID.randomUUID().toString().take(8)}",
            cropPlantingId = plotId.ifBlank { "plot-1" },
            farmId = resolvedFarmId,
            bedId = gardenLabel,
            cropName = cropName,
            varietyName = varietyName,
            currentStage = stage,
            logContext = LogContext.OBSERVE,
            selectedChoice = severity.code,
            selectedCheckboxes = selected.map { it.label }.ifEmpty { listOf("Pangkalahatang Kalusugan ng Halaman") },
            notes = buildString {
                append(CATEGORY_PREFIX).append(' ').append(activity.title.trim()).append('\n')
                append(RESULT_PREFIX).append(' ').append(resultTitle)
                if (answer.isNotBlank()) append('\n').append(answer)
            },
            date = today.toString(),
            createdAt = Instant.now().toString()
        )
    }

    suspend fun save(log: CropLog) {
        RepositoryProvider.cropLogRepository.insertLog(log)
    }

    fun summarize(log: CropLog): CheckUpLogSummary {
        val notes = log.notes.orEmpty()
        if (notes.startsWith(CATEGORY_PREFIX)) {
            val lines = notes.lines()
            return CheckUpLogSummary(
                category = lines.first().removePrefix(CATEGORY_PREFIX).trim(),
                resultTitle = lines.getOrNull(1)?.removePrefix(RESULT_PREFIX)?.trim(),
                details = lines.drop(2).joinToString("\n").ifBlank { null },
                severity = CheckUpSeverity.fromCode(log.selectedChoice)
            )
        }
        // Legacy logs: selectedChoice held the category title, notes held "title: answer".
        val legacyCategory = log.selectedChoice.takeIf { it.length > 1 } ?: "Pagsusuri sa Kalusugan"
        val legacyTitle = notes.substringBefore(": ", missingDelimiterValue = "").ifBlank { null }
        return CheckUpLogSummary(
            category = legacyCategory,
            resultTitle = legacyTitle,
            details = notes.ifBlank { null },
            severity = CheckUpSeverity.fromCode(log.selectedChoice)
        )
    }
}

/**
 * Persists which calendar tasks the user ticked, per vegetable.
 * Lets past tasks stay "Nagawa ko ito" after the calendar is closed.
 */
object TaskCompletionStore {
    private const val PREFS = "maptanim_task_completion"

    private fun key(scheduleKey: String) = "done_$scheduleKey"

    fun load(context: Context, scheduleKey: String): Set<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(key(scheduleKey), emptySet())
            ?.toSet()
            ?: emptySet()

    fun save(context: Context, scheduleKey: String, ids: Set<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(key(scheduleKey), ids)
            .apply()
    }
}
