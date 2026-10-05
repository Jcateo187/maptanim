package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.viewmodel.TopTab
import com.maptanim.app.ui.theme.White

@Composable
fun CropManagementHeader(
    currentStage: ManagementStage,
    selectedTopTab: TopTab,
    onBack: () -> Unit,
    onSelectTopTab: (TopTab) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = Color(0xFF161E14),
            border = BorderStroke(1.dp, Color(0xFF2B3825)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Crop Management",
                        color = White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "DSS Stage ${currentStage.stageNumber}: ${currentStage.label}",
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFF2B3825), thickness = 1.dp)

        // ── TOP TABS: Overview | Recommendation ─────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF10160F))
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TopTab.entries.forEach { tab ->
                val isSelected = tab == selectedTopTab
                Surface(
                    onClick = { onSelectTopTab(tab) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color(0xFF2E7D32) else Color.Transparent,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.label,
                            color = if (isSelected) White else Color(0xFF8B9B85),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFF2B3825), thickness = 0.5.dp)
    }
}
