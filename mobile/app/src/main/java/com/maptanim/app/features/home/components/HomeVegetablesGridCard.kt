package com.maptanim.app.features.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import kotlinx.coroutines.delay

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)

data class FamiliarVegetable(
    val name: String,
    val localName: String,
    val queryName: String,
    val category: String,
    val description: String,
    val accentColor: Color,
    val bgTint: Color
)

/**
 * HomeVegetablesGridCard — Vector SVG Carousel for Vegetables on the Home Screen.
 * Features all 15 Philippine vegetable SVGs rendered natively via CropSvgRenderer.
 * Automatically loops through the 15 crops every 5 seconds.
 * Tapping any vegetable slide directly opens CropInformationDialog.
 */
@Composable
fun HomeVegetablesGridCard(
    onVegetableClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vegetables = remember {
        listOf(
            FamiliarVegetable(
                name = "Tomato",
                localName = "Kamatis",
                queryName = "Tomato",
                category = "Solanaceae • Fruit",
                description = "High-value commercial crop. Tap for complete agronomic care & companion plants.",
                accentColor = Color(0xFFD32F2F),
                bgTint = Color(0xFFFFF6F6)
            ),
            FamiliarVegetable(
                name = "Carrot",
                localName = "Karot",
                queryName = "Carrot",
                category = "Apiaceae • Root",
                description = "Rich in Vitamin A. Best planted in loose, well-aerated organic soil.",
                accentColor = Color(0xFFE65100),
                bgTint = Color(0xFFFFF8F0)
            ),
            FamiliarVegetable(
                name = "Eggplant",
                localName = "Talong",
                queryName = "Eggplant",
                category = "Solanaceae • Fruit",
                description = "Philippine household staple. Requires full sun and sturdy bamboo staking.",
                accentColor = Color(0xFF6A1B9A),
                bgTint = Color(0xFFF9F5FC)
            ),
            FamiliarVegetable(
                name = "Pechay",
                localName = "Petsay / Bok Choy",
                queryName = "Pechay",
                category = "Brassicaceae • Leafy",
                description = "Fast-growing lowland green leafy vegetable ready for harvest in 25–30 days.",
                accentColor = Color(0xFF2E7D32),
                bgTint = Color(0xFFF1F8F1)
            ),
            FamiliarVegetable(
                name = "Corn",
                localName = "Mais",
                queryName = "Corn",
                category = "Poaceae • Grain",
                description = "High energy cereal grain. Thrives in full sun with consistent soil moisture.",
                accentColor = Color(0xFFF57F17),
                bgTint = Color(0xFFFFFDE7)
            ),
            FamiliarVegetable(
                name = "Onion",
                localName = "Sibuyas",
                queryName = "Onion",
                category = "Amaryllidaceae • Bulb",
                description = "Essential kitchen aromatic bulb. Requires well-drained sandy loam soil.",
                accentColor = Color(0xFF7B1FA2),
                bgTint = Color(0xFFFAF5FC)
            ),
            FamiliarVegetable(
                name = "Cabbage",
                localName = "Repolyo",
                queryName = "Cabbage",
                category = "Brassicaceae • Head",
                description = "Forms dense, crisp, nutrient-rich heads. Best in cool lowland or highland air.",
                accentColor = Color(0xFF388E3C),
                bgTint = Color(0xFFF2F9F2)
            ),
            FamiliarVegetable(
                name = "Lettuce",
                localName = "Litsugas",
                queryName = "Lettuce",
                category = "Asteraceae • Leafy",
                description = "Tender salad green. Ideal for container gardens and protected shaded beds.",
                accentColor = Color(0xFF43A047),
                bgTint = Color(0xFFF3FAF3)
            ),
            FamiliarVegetable(
                name = "Ampalaya",
                localName = "Bitter Gourd",
                queryName = "Ampalaya",
                category = "Cucurbitaceae • Vine",
                description = "Medicinal climbing gourd rich in charantin. Requires high trellis support.",
                accentColor = Color(0xFF1B5E20),
                bgTint = Color(0xFFF0F7F0)
            ),
            FamiliarVegetable(
                name = "Okra",
                localName = "Lady's Finger",
                queryName = "Okra",
                category = "Malvaceae • Pod",
                description = "Heat-loving and drought-tolerant pod vegetable. Produces continuous daily harvests.",
                accentColor = Color(0xFF558B2F),
                bgTint = Color(0xFFF5F9F0)
            ),
            FamiliarVegetable(
                name = "Kangkong",
                localName = "Water Spinach",
                queryName = "Kangkong",
                category = "Convolvulaceae • Leafy",
                description = "Hardy tropical water green. Rapid continuous shoot regeneration after cutting.",
                accentColor = Color(0xFF2E7D32),
                bgTint = Color(0xFFF1F8F1)
            ),
            FamiliarVegetable(
                name = "Pumpkin",
                localName = "Kalabasa",
                queryName = "Pumpkin",
                category = "Cucurbitaceae • Squash",
                description = "Nutritious sprawling vine rich in beta-carotene. Long post-harvest shelf life.",
                accentColor = Color(0xFFEF6C00),
                bgTint = Color(0xFFFFF7ED)
            ),
            FamiliarVegetable(
                name = "Chilli Pepper",
                localName = "Siling Labuyo",
                queryName = "Chili",
                category = "Solanaceae • Spice",
                description = "Native pungent hot pepper. Drought-hardy with year-round prolific fruiting.",
                accentColor = Color(0xFFC62828),
                bgTint = Color(0xFFFDF4F4)
            ),
            FamiliarVegetable(
                name = "Pipino",
                localName = "Cucumber",
                queryName = "Pipino",
                category = "Cucurbitaceae • Vine",
                description = "Refreshing high-moisture vine crop. Fast harvest cycle on sturdy A-frame trellises.",
                accentColor = Color(0xFF33691E),
                bgTint = Color(0xFFF4F9EE)
            ),
            FamiliarVegetable(
                name = "Sitaw",
                localName = "Yardlong Bean",
                queryName = "Sitaw",
                category = "Fabaceae • Legume",
                description = "Nitrogen-fixing climbing pole bean. High protein pods harvested every other day.",
                accentColor = Color(0xFF2E7D32),
                bgTint = Color(0xFFF1F8F1)
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { vegetables.size })

    // ── Auto-loop carousel every 5 seconds ──────────────────────────────────
    LaunchedEffect(pagerState, vegetables.size) {
        while (true) {
            delay(5000L) // 5 seconds interval
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % vegetables.size
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Section Heading Outside/On Top of Carousel ──
        Text(
            text = "VEGETABLES",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = Color(0xFF555555),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // ── Auto-Looping Vector SVG Carousel ──
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) { page ->
                val veg = vegetables[page]
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onVegetableClick(veg.queryName) }
                        .background(veg.bgTint)
                ) {
                    // 1. Pure Scalable Vector SVG Illustration taking the whole card!
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        val minDim = minOf(size.width, size.height)
                        CropSvgRenderer.drawCropSvg(
                            drawScope = this,
                            cropName = veg.queryName,
                            center = Offset(size.width / 2f, size.height * 0.38f),
                            sizePx = minDim * 0.95f
                        )
                    }

                    // 2. Top Row: Category tag and slide index badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = veg.accentColor,
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = veg.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DeepBlack.copy(alpha = 0.55f)
                        ) {
                            Text(
                                text = "${page + 1} / ${vegetables.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // 3. Bottom Frosted High-Contrast Overlay for Names and Description (without "View Dossier" button)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.85f),
                                        Color.White.copy(alpha = 0.98f)
                                    ),
                                    startY = 0f
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = veg.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepBlack
                            )
                            Text(
                                text = "${veg.localName} • Philippine Variety",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LushGreen
                            )
                            Text(
                                text = veg.description,
                                fontSize = 11.sp,
                                color = Color(0xFF444444),
                                maxLines = 1,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // ── Carousel Indicator Dots (for all 15 vegetables) ─────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(vegetables.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 16.dp else 5.dp,
                    label = "dot_width"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(width = width, height = 5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isSelected) LushGreen else Color(0xFFD0D0D0))
                )
            }
        }
    }
}
