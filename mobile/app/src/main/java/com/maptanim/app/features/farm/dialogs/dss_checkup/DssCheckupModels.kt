package com.maptanim.app.features.farm.dialogs.dss_checkup

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CheckUpActivity(
    val id: String,
    val title: String,
    val tagalogSubtitle: String? = null,
    val tagalogTitle: String = tagalogSubtitle ?: "",
    val icon: ImageVector,
    val description: String = tagalogSubtitle ?: "",
    val options: List<CheckUpOption> = emptyList()
)

data class CheckUpOption(
    val id: String,
    val title: String? = null,
    val tagalogDesc: String? = null,
    val isIdeal: Boolean = false,
    val label: String = title ?: "",
    val description: String = tagalogDesc ?: ""
)

data class DirectCropSolution(
    val title: String = "",
    val tagalogTitle: String = title,
    val severity: String,
    val badgeColor: Color,
    val directAnswer: String,
    val actionSteps: List<String>,
    val materialsNeeded: List<String>,
    val organicRemedy: String? = null,
    val criticalRule: String? = null
)
