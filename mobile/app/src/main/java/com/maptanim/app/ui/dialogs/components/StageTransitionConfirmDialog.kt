package com.maptanim.app.ui.dialogs.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.ui.theme.White

/**
 * Stage transition confirmation dialog.
 * Prompts the farmer to confirm advancing or stepping back across lifecycle stages.
 */
@Composable
fun StageTransitionConfirmDialog(
    currentStage: ManagementStage,
    targetStage: ManagementStage,
    isManual: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isAdvance = targetStage.stageNumber > currentStage.stageNumber

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF161F14),
            border = BorderStroke(1.dp, Color(0xFF2E3E28)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isAdvance) Color(0xFF1E2D1A) else Color(0xFF2E2416)
                ) {
                    Text(
                        text = if (isAdvance) {
                            if (isManual) "MANUAL STAGE ADVANCEMENT" else "STAGE TRANSITION CRITERIA MET"
                        } else {
                            "MANUAL STAGE ROLLBACK"
                        },
                        color = if (isAdvance) Color(0xFF81C784) else Color(0xFFFFB74D),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = if (isAdvance) {
                        "Advance to Stage ${targetStage.stageNumber}: ${targetStage.label}?"
                    } else {
                        "Move back to Stage ${targetStage.stageNumber}: ${targetStage.label}?"
                    },
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isAdvance) {
                        if (isManual) {
                            "Manually advancing to ${targetStage.label} will update recommended tasks, protocol guidelines, and allowed log contexts to Stage ${targetStage.stageNumber}."
                        } else {
                            "Your recent observation logs indicate this crop has reached ${targetStage.label}. Advancing will update active tasks and agronomic care protocols."
                        }
                    } else {
                        "Moving back to ${targetStage.label} will restore Stage ${targetStage.stageNumber} guidelines and allow you to record earlier stage observations."
                    },
                    color = Color(0xFFC0CDC0),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF384A33)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA0B09A))
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAdvance) Color(0xFF2E7D32) else Color(0xFFB78103),
                            contentColor = White
                        )
                    ) {
                        Text(
                            text = if (isAdvance) "Advance Stage" else "Move Back",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
