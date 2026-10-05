package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.domain.model.AgroZone
import com.maptanim.app.domain.model.FarmEnvironment
import com.maptanim.app.domain.model.SiteConstraint
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.features.farm.components.BackyardScalePresetChips
import com.maptanim.app.features.farm.components.FarmBoundariesScaleSection
import com.maptanim.app.features.farm.components.SeasonDropdownSelector
import com.maptanim.app.features.farm.components.ZeroBudgetMaterialsSelector

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * FarmSetupDialog — Configure farm name, agro-zone (Highland vs Lowland),
 * base soil type, physical dimensions with basketball court calibration, and site constraints.
 * Adheres strictly to Daylight High-Contrast Theme (Pure White, Lush Green, Deep Black, ZERO emojis).
 */
@Composable
fun FarmSetupDialog(
    initialName: String,
    initialEnvironment: FarmEnvironment,
    onConfirm: (name: String, environment: FarmEnvironment) -> Unit,
    onDismiss: () -> Unit
) {
    var farmName by remember { mutableStateOf(initialName) }
    var selectedZone by remember { mutableStateOf(initialEnvironment.zone) }
    var selectedSoil by remember { mutableStateOf(initialEnvironment.defaultSoil) }
    var widthM by remember { mutableFloatStateOf(initialEnvironment.widthM) }
    var heightM by remember { mutableFloatStateOf(initialEnvironment.heightM) }
    var selectedConstraints by remember { mutableStateOf(initialEnvironment.constraints.toMutableSet()) }
    var selectedSeason by remember { mutableStateOf(initialEnvironment.season) }
    var selectedMaterials by remember { mutableStateOf(initialEnvironment.availableMaterials.toMutableSet()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // ── Header ───────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FARM SETUP & ENVIRONMENT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp,
                            color = LushGreen
                        )
                        Text(
                            text = "Calibrate location, zone & soil for precision DSS",
                            fontSize = 11.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DeepBlack)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorderColor)

                // ── Scrollable Body ──────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Meaningful Farm Name
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "FARM / BACKYARD NAME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = DeepBlack
                        )
                        OutlinedTextField(
                            value = farmName,
                            onValueChange = { farmName = it },
                            placeholder = { Text("e.g. Benguet Highland Plot or Murcia Garden", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LushGreen,
                                unfocusedBorderColor = CardBorderColor,
                                focusedTextColor = DeepBlack,
                                unfocusedTextColor = DeepBlack
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // 2. Agro-Ecological Zone (Highland vs Lowland)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "AGRO-ECOLOGICAL ZONE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = DeepBlack
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AgroZone.entries.forEach { zone ->
                                val isSelected = selectedZone == zone
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedZone = zone },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) LushGreen.copy(alpha = 0.08f) else LightSurface,
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) LushGreen else CardBorderColor
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = zone.label.uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) LushGreen else DeepBlack
                                        )
                                        Text(
                                            text = zone.elevationRange,
                                            fontSize = 10.sp,
                                            color = Color(0xFF666666)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = zone.description,
                                            fontSize = 9.sp,
                                            lineHeight = 12.sp,
                                            color = Color(0xFF555555)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2B. Current Weather / Season
                    SeasonDropdownSelector(
                        selectedSeason = selectedSeason,
                        onSelectSeason = { selectedSeason = it }
                    )

                    // 3. Base Soil Type
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "BASE FARM SOIL TYPE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = DeepBlack
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(SoilType.LOAM, SoilType.CLAY, SoilType.SANDY).forEach { soil ->
                                val isSelected = selectedSoil == soil
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedSoil = soil },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) LushGreen.copy(alpha = 0.08f) else LightSurface,
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) LushGreen else CardBorderColor
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = soil.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isSelected) LushGreen else DeepBlack
                                        )
                                        Text(
                                            text = when (soil) {
                                                SoilType.LOAM -> "Balanced 40-40-20"
                                                SoilType.CLAY -> "Heavy, waterlogged"
                                                SoilType.SANDY -> "Porous, leaches"
                                                else -> "Standard"
                                            },
                                            fontSize = 9.sp,
                                            color = Color(0xFF666666)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3B. Backyard Scale Presets (Basketball Benchmark)
                    BackyardScalePresetChips(
                        currentWidth = widthM,
                        currentHeight = heightM,
                        onSelectPreset = { w, h ->
                            widthM = w
                            heightM = h
                        }
                    )

                    // 4. Physical Dimensions & Basketball Court Calibration
                    FarmBoundariesScaleSection(
                        widthM = widthM,
                        heightM = heightM,
                        onWidthChange = { widthM = it },
                        onHeightChange = { heightM = it }
                    )

                    // 5. Site Challenge Flags
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "SITE CHALLENGE CONSTRAINTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = DeepBlack
                        )
                        SiteConstraint.entries.forEach { constraint ->
                            val isChecked = constraint in selectedConstraints
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedConstraints.remove(constraint)
                                        else selectedConstraints.add(constraint)
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) selectedConstraints.add(constraint)
                                        else selectedConstraints.remove(constraint)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = LushGreen,
                                        checkmarkColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = constraint.label,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        color = DeepBlack
                                    )
                                    Text(
                                        text = constraint.description,
                                        fontSize = 9.sp,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }
                        }
                    }

                    // 6. Available Zero-Budget Household Materials
                    ZeroBudgetMaterialsSelector(
                        selectedMaterials = selectedMaterials,
                        onToggleMaterial = { material ->
                            if (material in selectedMaterials) {
                                selectedMaterials.remove(material)
                            } else {
                                selectedMaterials.add(material)
                            }
                        }
                    )
                }

                // ── Footer Action Buttons ────────────────────────────────────
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorderColor)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Text("Cancel", color = DeepBlack, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val newEnv = FarmEnvironment(
                                zone = selectedZone,
                                defaultSoil = selectedSoil,
                                widthM = widthM,
                                heightM = heightM,
                                constraints = selectedConstraints,
                                season = selectedSeason,
                                availableMaterials = selectedMaterials
                            )
                            onConfirm(farmName.ifBlank { "My Farm" }, newEnv)
                        },
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                    ) {
                        Text("Save Farm Setup", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
