package com.maptanim.app.features.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.maptanim.app.R
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
    val bgTint: Color,
    val heroDrawableResId: Int? = null,
    val assetImageUrl: String? = null
)

/**
 * HomeVegetablesGridCard — High-Resolution Photographic Hero Carousel on the Home Screen.
 * Showcases Philippine agricultural hero photography with scalable vector fallbacks.
 * Automatically loops through all crops every 5 seconds.
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
                bgTint = Color(0xFFFFF6F6),
                heroDrawableResId = R.drawable.ph_tomato_hero
            ),
            FamiliarVegetable(
                name = "Carrot",
                localName = "Karot",
                queryName = "Carrot",
                category = "Apiaceae • Root",
                description = "Rich in Vitamin A. Best planted in loose, well-aerated organic soil.",
                accentColor = Color(0xFFE65100),
                bgTint = Color(0xFFFFF8F0),
                heroDrawableResId = R.drawable.ph_carrot_hero
            ),
            FamiliarVegetable(
                name = "Eggplant",
                localName = "Talong",
                queryName = "Eggplant",
                category = "Solanaceae • Fruit",
                description = "Philippine household staple. Requires full sun and sturdy bamboo staking.",
                accentColor = Color(0xFF6A1B9A),
                bgTint = Color(0xFFF9F5FC),
                heroDrawableResId = R.drawable.ph_eggplant_hero
            ),
            FamiliarVegetable(
                name = "Pechay",
                localName = "Petsay / Bok Choy",
                queryName = "Pechay",
                category = "Brassicaceae • Leafy",
                description = "Fast-growing lowland green leafy vegetable ready for harvest in 25–30 days.",
                accentColor = Color(0xFF2E7D32),
                bgTint = Color(0xFFF1F8F1),
                heroDrawableResId = R.drawable.ph_pechay_hero
            ),
            FamiliarVegetable(
                name = "Green Onion",
                localName = "Dahon ng Sibuyas / Scallion",
                queryName = "Green Onion",
                category = "Amaryllidaceae • Allium",
                description = "Prized aromatic bunching scallion with crisp white stalks and continuous ratoon harvesting.",
                accentColor = Color(0xFF2E7D32),
                bgTint = Color(0xFFF1F8F1),
                heroDrawableResId = R.drawable.ph_green_onion_hero
            ),
            FamiliarVegetable(
                name = "Potato",
                localName = "Patatas",
                queryName = "Potato",
                category = "Solanaceae • Tuber",
                description = "Highland energy tuber powerhouse. Thrives in cool mountain air on deep volcanic ridges.",
                accentColor = Color(0xFFD48806),
                bgTint = Color(0xFFFFFBE6),
                heroDrawableResId = R.drawable.ph_potato_hero
            ),
            FamiliarVegetable(
                name = "Cabbage",
                localName = "Repolyo",
                queryName = "Cabbage",
                category = "Brassicaceae • Head",
                description = "Forms dense, crisp, nutrient-rich heads. Best in cool lowland or highland air.",
                accentColor = Color(0xFF388E3C),
                bgTint = Color(0xFFF2F9F2),
                heroDrawableResId = R.drawable.ph_cabbage_hero
            ),
            FamiliarVegetable(
                name = "Lettuce",
                localName = "Litsugas",
                queryName = "Lettuce",
                category = "Asteraceae • Leafy",
                description = "Tender salad green. Ideal for container gardens and protected shaded beds.",
                accentColor = Color(0xFF43A047),
                bgTint = Color(0xFFF3FAF3),
                heroDrawableResId = R.drawable.ph_lettuce_hero
            ),
            FamiliarVegetable(
                name = "Ampalaya",
                localName = "Bitter Gourd",
                queryName = "Ampalaya",
                category = "Cucurbitaceae • Vine",
                description = "Medicinal climbing gourd rich in charantin. Requires high trellis support.",
                accentColor = Color(0xFF1B5E20),
                bgTint = Color(0xFFF0F7F0),
                heroDrawableResId = R.drawable.ph_ampalaya_hero
            ),
            FamiliarVegetable(
                name = "Squash",
                localName = "Kalabasa",
                queryName = "Squash",
                category = "Cucurbitaceae • Squash",
                description = "Nutritious sprawling vine rich in beta-carotene. Long post-harvest shelf life.",
                accentColor = Color(0xFFEF6C00),
                bgTint = Color(0xFFFFF7ED),
                heroDrawableResId = R.drawable.ph_squash_hero
            ),
            FamiliarVegetable(
                name = "Pipino",
                localName = "Cucumber",
                queryName = "Pipino",
                category = "Cucurbitaceae • Vine",
                description = "Refreshing high-moisture vine crop. Fast harvest cycle on sturdy A-frame trellises.",
                accentColor = Color(0xFF33691E),
                bgTint = Color(0xFFF4F9EE),
                heroDrawableResId = R.drawable.ph_cucumber_hero
            ),
            FamiliarVegetable(
                name = "Sitaw",
                localName = "Yardlong Bean",
                queryName = "Sitaw",
                category = "Fabaceae • Legume",
                description = "Nitrogen-fixing climbing pole bean. High protein pods harvested every other day.",
                accentColor = Color(0xFF2E7D32),
                bgTint = Color(0xFFF1F8F1),
                heroDrawableResId = R.drawable.ph_sitaw_hero
            ),
            FamiliarVegetable(
                name = "Bell Pepper",
                localName = "Siling Atsal",
                queryName = "Bell Pepper",
                category = "Solanaceae • Fruit",
                description = "High-value crisp sweet pepper. Thrives in cool highland mountain air on sturdy bamboo stakes.",
                accentColor = Color(0xFFC62828),
                bgTint = Color(0xFFFDF4F4),
                heroDrawableResId = R.drawable.ph_bell_pepper_hero,
                assetImageUrl = "file:///android_asset/metadata/crops_images/bell_pepper.png"
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

        // ── Auto-Looping Photographic Hero Carousel ──
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            shadowElevation = 2.dp
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) { page ->
                val veg = vegetables[page]
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onVegetableClick(veg.queryName) }
                ) {
                    // 1. High-Detail Photographic Hero Image (with fallback to asset image or Vector SVG)
                    if (veg.heroDrawableResId != null) {
                        Image(
                            painter = painterResource(id = veg.heroDrawableResId),
                            contentDescription = veg.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (veg.assetImageUrl != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF263238), Color(0xFF1B241C))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = veg.assetImageUrl,
                                contentDescription = veg.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        // Scalable Vector SVG Illustration fallback
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(veg.bgTint)
                        ) {
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
                        }
                    }

                    // 2. Cinematic Vignette Gradient Overlay for high-contrast legibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.38f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.50f),
                                        Color.Black.copy(alpha = 0.88f)
                                    ),
                                    startY = 0f
                                )
                            )
                    )

                    // 3. Top Row: Category badge & slide index counter
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
                            shadowElevation = 3.dp
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
                            color = DeepBlack.copy(alpha = 0.65f),
                            shadowElevation = 2.dp
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

                    // 4. Bottom Content Row with high-contrast text and info chip
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = veg.name,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${veg.localName} • Philippine Variety",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA5D6A7)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.20f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "View Dossier",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Text(
                            text = veg.description,
                            fontSize = 11.sp,
                            color = Color(0xFFE6E6E6),
                            maxLines = 1,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // ── Carousel Indicator Dots ─────────────────────────────────────────
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
