package com.maptanim.app.features.farm.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import com.maptanim.app.features.farm.renderer.canvas.CardSvgRenderer

// ═══════════════════════════════════════════════════════════════════════════════
// Eye-Friendly, Glare-Free, High-Contrast Color Palette
// Soft organic mist background (eliminates bright white eye strain/distraction)
// Crisp white card surfaces with defined sage borders and deep readable text
// ═══════════════════════════════════════════════════════════════════════════════
private val ForestGreen = Color(0xFF1B5E20)
private val LushGreen = Color(0xFF2E7D32)
private val LightGreenBg = Color(0xFFE8F5E9)
private val DeepBlack = Color(0xFF141915)      // Rich high-contrast text
private val Charcoal = Color(0xFF28302A)       // Crisp secondary text
private val MutedText = Color(0xFF4C564D)      // High-contrast labels (no washed-out gray)
private val CardBorder = Color(0xFFD0D8CC)     // Defined, elegant card outline
private val CardBg = Color(0xFFFFFFFF)         // Clean card surface
private val SurfaceBg = Color(0xFFF2F5F0)      // Eye-friendly organic soft canvas (anti-glare)
private val AmberAlert = Color(0xFFE65100)
private val RedAlert = Color(0xFFC62828)
private val BlueAccent = Color(0xFF1565C0)
private val GoldAccent = Color(0xFFF57F17)

/**
 * CropInformationDialog — State-of-the-Art Comprehensive Vegetable Encyclopedia Dossier.
 * Specially optimized for:
 * 1. Eye-comfort: Glare-free, soothing organic light canvas with high-contrast text.
 * 2. Visual elegance: Real high-resolution stage photography & native crop vector iconography.
 * 3. Readability: Enlarged, comfortable typography (14–16sp body, 15–16sp quick info, 18–20sp headers).
 * 4. Philippine agricultural research dataset for local farmers and home gardeners.
 */
@Composable
fun CropInformationDialog(
    cropName: String,
    onDismiss: () -> Unit,
    onSelectCrop: ((String) -> Unit)? = null
) {
    val data = remember(cropName) { CropEncyclopediaRegistry.getCropEncyclopedia(cropName) }
    val favoriteManager = remember { com.maptanim.app.core.preferences.FavoriteCropsManager.getInstance() }
    val favorites by favoriteManager.favoriteCrops.collectAsState()
    val isFavorite = remember(favorites, data.cropName, cropName) {
        favoriteManager.isFavorite(data.cropName) || favoriteManager.isFavorite(cropName)
    }
    var activeExplanation by remember { mutableStateOf<ExplanationData?>(null) }
    var showVarietiesModal by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── 1. TOP APP BAR ──────────────────────────────────────────
                TopDossierAppBar(
                    title = data.cropName,
                    localSubtitle = data.localName,
                    isFavorite = isFavorite,
                    onToggleFavorite = { favoriteManager.toggleFavorite(data.cropName) },
                    onDismiss = onDismiss
                )

                // ── 2. SCROLLABLE DOSSIER BODY ──────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 36.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // HERO IMAGE CAROUSEL (Real Photos + Vector Fallbacks)
                    HeroImageCarousel(
                        slides = data.carouselSlides,
                        cropName = data.cropName
                    )

                    // HEADER IDENTITY & ACTION ROW
                    CropIdentitySection(
                        data = data,
                        isFavorite = isFavorite,
                        onOpenVarieties = { showVarietiesModal = true },
                        onToggleFavorite = { favoriteManager.toggleFavorite(data.cropName) }
                    )

                    // SELECTED VARIETY CARD
                    SelectedVarietyCard(variety = data.selectedVariety)

                    // QUICK INFO 3-COLUMN / 4-COLUMN GRID WITH CRISP ICONS
                    QuickInfoGridSection(
                        items = data.quickInfo,
                        onItemClick = { activeExplanation = it.explanation }
                    )

                    // COMPANION PLANTS (HALAMANG KASAMA) WITH MINI SVGS
                    ExpandableCropRelationGrid(
                        title = "Companion Plants (Halamang Kasama)",
                        subtitle = "Mga pananim na nagtataboy ng peste at nagpapaganda ng ani",
                        items = data.companionPlants,
                        isBeneficial = true,
                        onItemClick = { activeExplanation = it.explanation }
                    )

                    // COMBATIVE PLANTS (HALAMANG DI-DAPAT KATABI)
                    ExpandableCropRelationGrid(
                        title = "Combative Plants (Di-Dapat Katabi)",
                        subtitle = "Umiwas itabi upang hindi maglipat ng sakit o umagaw ng sustansya",
                        items = data.combativePlants,
                        isBeneficial = false,
                        onItemClick = { activeExplanation = it.explanation }
                    )

                    // NUTRITION BADGES
                    NutritionBadgesSection(
                        items = data.nutritionBadges,
                        onItemClick = { activeExplanation = it.explanation }
                    )

                    // PESTS (MGA PESTE SA PILIPINAS)
                    ExpandablePestDiseaseGrid(
                        title = "Pests (Mga Peste sa Taniman)",
                        subtitle = "Mga sumisipsip at nambubutas na insekto at tamang pamatay-peste",
                        items = data.pests,
                        isPest = true,
                        onItemClick = { activeExplanation = it.explanation }
                    )

                    // DISEASES (MGA SAKIT SA KAMATIS)
                    ExpandablePestDiseaseGrid(
                        title = "Diseases (Mga Sakit sa Halaman)",
                        subtitle = "Bacterial wilt, kulot, at fungal blights sa mainit at basing lupa",
                        items = data.diseases,
                        isPest = false,
                        onItemClick = { activeExplanation = it.explanation }
                    )

                    // BENEFICIAL CRITTERS (MGA KAIBIGANG NILALANG)
                    ExpandableCrittersGrid(
                        title = "Beneficial Critters (Kaibigang Nilalang)",
                        subtitle = "Mga katutubong bubuyog, mandurukot, at mangangaso sa hardin",
                        items = data.beneficialCritters,
                        onItemClick = { activeExplanation = it.explanation }
                    )

                    // AGRONOMIC GROWING GUIDES
                    GrowingGuidesSection(guides = data.growingGuides)

                    // PLANTING CALENDAR & PHILIPPINE CLIMATE ZONE
                    PlantingCalendarSection(
                        calendar = data.plantingCalendar,
                        location = data.locationData
                    )

                    // SOIL PREPARATION & EDAPHIC PROTOCOLS
                    SoilPreparationCard(soil = data.soilPrep)

                    // GROWTH TIMELINE (DAY-BY-DAY MILESTONES)
                    GrowthTimelineSection(timeline = data.growthTimeline)

                    // STEP-BY-STEP HOW-TOS (TRANSPLANTING & SEED SAVING)
                    HowToGuideSection(howTos = data.howTos)

                    // FAQ ACCORDION (LOCAL GROWER ANSWERS)
                    FaqAccordionSection(faqs = data.faqs)

                    // NUTRITION TABLE (100g SERVING FACTS)
                    NutritionTableCard(nutrition = data.nutritionTable)
                }
            }

            // ── 3. INTERACTIVE EXPLANATION MODAL OVERLAY ─────────────────
            activeExplanation?.let { exp ->
                ExplanationModalOverlay(
                    explanation = exp,
                    onDismiss = { activeExplanation = null }
                )
            }

            // ── 4. SUB-MODALS (Varieties, Notes, Edit) ────────────────────
            if (showVarietiesModal) {
                VarietiesListModal(
                    varieties = data.otherVarieties,
                    selectedVariety = data.selectedVariety.name,
                    onSelect = { showVarietiesModal = false },
                    onDismiss = { showVarietiesModal = false }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Top App Bar
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun TopDossierAppBar(
    title: String,
    localSubtitle: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        color = Color.White,
        border = BorderStroke(1.dp, CardBorder),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = DeepBlack,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepBlack
                )
                Text(
                    text = localSubtitle,
                    fontSize = 12.sp,
                    color = ForestGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) RedAlert else DeepBlack,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Hero Image Carousel (Real Photographs + Vector Fallbacks)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun HeroImageCarousel(
    slides: List<CarouselSlide>,
    cropName: String
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Auto-loop carousel every 5 seconds
    LaunchedEffect(pagerState, slides.size) {
        if (slides.size > 1) {
            while (true) {
                delay(5000L)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % slides.size
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(BorderStroke(1.2.dp, CardBorder), RoundedCornerShape(20.dp))
        ) { page ->
            val slide = slides[page]
            Box(modifier = Modifier.fillMaxSize()) {
                // If real photo exists, display it; else fallback to vector canvas
                if (slide.drawableResId != null) {
                    Image(
                        painter = painterResource(id = slide.drawableResId),
                        contentDescription = slide.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF2E3D30), Color(0xFF1B241C))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            CropSvgRenderer.drawCropSvg(
                                drawScope = this,
                                cropName = cropName,
                                center = Offset(size.width / 2f, size.height / 2f),
                                sizePx = minOf(size.width, size.height) * 0.92f
                            )
                        }
                    }
                }

                // Dark vignette gradient overlay for crystal-clear readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Black.copy(alpha = 0.45f),
                                    Color.Black.copy(alpha = 0.88f)
                                ),
                                startY = 40f
                            )
                        )
                )

                // Slide text details
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = slide.badgeColor,
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = slide.stage.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = slide.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = slide.subtitle,
                            fontSize = 13.5.sp,
                            color = Color.White.copy(alpha = 0.92f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Pager indicator dots
        Row(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(slides.size) { idx ->
                val isSelected = (pagerState.currentPage == idx)
                Box(
                    modifier = Modifier
                        .height(7.dp)
                        .width(if (isSelected) 22.dp else 7.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) LushGreen else Color(0xFFBDC7B9))
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Crop Identity & Action Row
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun CropIdentitySection(
    data: CropEncyclopedia,
    isFavorite: Boolean,
    onOpenVarieties: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.cropName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepBlack,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = data.scientificName,
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic,
                    color = ForestGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LightGreenBg,
                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "Category: ${data.category}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // Action Pills Row (Notes and Edit deleted)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionPillChip(
                label = "Varieties",
                icon = Icons.Default.Spa,
                onClick = onOpenVarieties,
                modifier = Modifier.weight(1f)
            )
            ActionPillChip(
                label = if (isFavorite) "Saved" else "Favorite",
                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                onClick = onToggleFavorite,
                tint = if (isFavorite) RedAlert else ForestGreen,
                modifier = Modifier.weight(1f)
            )
        }

        // Overview Narrative Text Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardBg,
            border = BorderStroke(1.2.dp, CardBorder),
            shadowElevation = 2.dp
        ) {
            Text(
                text = data.overview,
                fontSize = 14.5.sp,
                lineHeight = 22.sp,
                color = Charcoal,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun ActionPillChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    tint: Color = ForestGreen,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = CardBg,
        border = BorderStroke(1.2.dp, CardBorder),
        shadowElevation = 1.dp,
        modifier = modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Selected Variety Card
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun SelectedVarietyCard(variety: VarietyInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitleHeader(title = "Selected Variety")

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardBg,
            border = BorderStroke(1.2.dp, CardBorder),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = variety.producer,
                            fontSize = 12.sp,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = variety.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepBlack
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = variety.badge,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LightGreenBg
                        ) {
                            Text(
                                text = variety.type,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = variety.description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Charcoal
                )
            }
        }
    }
}



// ═══════════════════════════════════════════════════════════════════════════════
// Quick Info 3-Column Grid with Dedicated Colorful Icon Badges
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun QuickInfoGridSection(
    items: List<QuickInfoItem>,
    onItemClick: (QuickInfoItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitleHeader(title = "Quick Info")
            Text(
                text = "Pindutin para sa buong paliwanag",
                fontSize = 12.5.sp,
                color = ForestGreen,
                fontWeight = FontWeight.Bold
            )
        }

        // Layout in 3 columns
        val chunked = items.chunked(3)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            chunked.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        Surface(
                            onClick = { onItemClick(item) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (item.isAlert) Color(0xFFFFF8E1) else Color(0xFFF1F8E9),
                            border = BorderStroke(1.2.dp, if (item.isAlert) AmberAlert.copy(alpha = 0.6f) else CardBorder),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(105.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // SVG icon takes the whole card
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp)
                                ) {
                                    CardSvgRenderer.drawQuickInfoSvg(
                                        drawScope = this,
                                        key = item.key,
                                        center = Offset(size.width / 2f, (size.height / 2f) - 10.dp.toPx()),
                                        sizePx = minOf(size.width, size.height) * 0.72f,
                                        isAlert = item.isAlert
                                    )
                                }

                                // Top Label Badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.88f),
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = item.label,
                                        fontSize = 10.sp,
                                        color = if (item.isAlert) AmberAlert else ForestGreen,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Bottom Value Banner over Gradient Scrim
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color(0xB3000000),
                                                    Color(0xE6000000)
                                                )
                                            )
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = item.value,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Expandable Crop Relation Grid with Mini Vector Graphic Avatars
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun ExpandableCropRelationGrid(
    title: String,
    subtitle: String,
    items: List<PlantRelationItem>,
    isBeneficial: Boolean,
    onItemClick: (PlantRelationItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val initialItemCount = 6 // Exactly 2 rows of 3 columns
    val displayedItems = if (isExpanded || items.size <= initialItemCount) items else items.take(initialItemCount)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionTitleHeader(title = title)
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MutedText,
            fontWeight = FontWeight.Medium
        )

        val chunked = displayedItems.chunked(3)
        Column(
            modifier = Modifier.animateContentSize(animationSpec = tween(250)),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            chunked.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        Surface(
                            onClick = { onItemClick(item) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isBeneficial) Color(0xFFF1F8E9) else Color(0xFFFFEBEE),
                            border = BorderStroke(
                                1.2.dp,
                                if (isBeneficial) CardBorder else RedAlert.copy(alpha = 0.4f)
                            ),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // SVG takes the whole card
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                ) {
                                    CropSvgRenderer.drawCropSvg(
                                        drawScope = this,
                                        cropName = item.name,
                                        center = Offset(size.width / 2f, (size.height / 2f) - 10.dp.toPx()),
                                        sizePx = minOf(size.width, size.height) * 0.85f
                                    )
                                }

                                // Status Badge
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = if (isBeneficial) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = if (isBeneficial) ForestGreen else RedAlert,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }

                                // Bottom Gradient Scrim with Plant Name and Role
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color(0xB3000000),
                                                    Color(0xE6000000)
                                                )
                                            )
                                        )
                                        .padding(horizontal = 6.dp, vertical = 5.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = item.name,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.role,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isBeneficial) Color(0xFFA5D6A7) else Color(0xFFFFCDD2),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        if (items.size > initialItemCount) {
            DropdownToggleButton(
                isExpanded = isExpanded,
                totalCount = items.size,
                onClick = { isExpanded = !isExpanded }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Nutrition Badges Section
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun NutritionBadgesSection(
    items: List<NutritionBadgeItem>,
    onItemClick: (NutritionBadgeItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionTitleHeader(title = "Nutrition")
        Text(
            text = "Mahahalagang bitamina, mineral, at lycopene antioxidant",
            fontSize = 13.sp,
            color = MutedText,
            fontWeight = FontWeight.Medium
        )

        val chunked = items.chunked(3)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            chunked.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        Surface(
                            onClick = { onItemClick(item) },
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF1F8E9),
                            border = BorderStroke(1.2.dp, CardBorder),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Nutrition SVG takes the whole card
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                ) {
                                    CardSvgRenderer.drawNutritionSvg(
                                        drawScope = this,
                                        code = item.code,
                                        center = Offset(size.width / 2f, (size.height / 2f) - 10.dp.toPx()),
                                        sizePx = minOf(size.width, size.height) * 0.78f
                                    )
                                }

                                // Top Daily Value Badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = item.percentageDaily,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = ForestGreen,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }

                                // Bottom Gradient Scrim with Vitamin Code and Name
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color(0xB3000000),
                                                    Color(0xE6000000)
                                                )
                                            )
                                        )
                                        .padding(horizontal = 6.dp, vertical = 5.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = item.code,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.name,
                                            fontSize = 9.5.sp,
                                            color = Color(0xFFD4E6D2),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Pests & Diseases Grid with 2-Row Default & Dropdown
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun ExpandablePestDiseaseGrid(
    title: String,
    subtitle: String,
    items: List<PestDiseaseItem>,
    isPest: Boolean,
    onItemClick: (PestDiseaseItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val initialItemCount = 6 // Exactly 2 rows of 3 columns
    val displayedItems = if (isExpanded || items.size <= initialItemCount) items else items.take(initialItemCount)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionTitleHeader(title = title)
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MutedText,
            fontWeight = FontWeight.Medium
        )

        val chunked = displayedItems.chunked(3)
        Column(
            modifier = Modifier.animateContentSize(animationSpec = tween(250)),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            chunked.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        Surface(
                            onClick = { onItemClick(item) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (item.severity == "Kritikal" || item.severity == "Nakamamatay") Color(0xFFFFEBEE) else Color(0xFFFFF3E0),
                            border = BorderStroke(1.2.dp, CardBorder),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(115.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Pest / Disease SVG takes the whole card
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                ) {
                                    CardSvgRenderer.drawPestDiseaseSvg(
                                        drawScope = this,
                                        isPest = isPest,
                                        severity = item.severity,
                                        center = Offset(size.width / 2f, (size.height / 2f) - 10.dp.toPx()),
                                        sizePx = minOf(size.width, size.height) * 0.78f
                                    )
                                }

                                // Top Severity Badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = item.severity,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.severity == "Kritikal" || item.severity == "Nakamamatay") RedAlert else AmberAlert,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }

                                // Bottom Gradient Scrim with Pest / Disease Name
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color(0xB3000000),
                                                    Color(0xE6000000)
                                                )
                                            )
                                        )
                                        .padding(horizontal = 6.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = item.name,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        if (items.size > initialItemCount) {
            DropdownToggleButton(
                isExpanded = isExpanded,
                totalCount = items.size,
                onClick = { isExpanded = !isExpanded }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Beneficial Critters Grid
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun ExpandableCrittersGrid(
    title: String,
    subtitle: String,
    items: List<CritterItem>,
    onItemClick: (CritterItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val initialItemCount = 6
    val displayedItems = if (isExpanded || items.size <= initialItemCount) items else items.take(initialItemCount)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionTitleHeader(title = title)
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MutedText,
            fontWeight = FontWeight.Medium
        )

        val chunked = displayedItems.chunked(3)
        Column(
            modifier = Modifier.animateContentSize(animationSpec = tween(250)),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            chunked.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        Surface(
                            onClick = { onItemClick(item) },
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF1F8E9),
                            border = BorderStroke(1.2.dp, CardBorder),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(115.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Beneficial Critter SVG takes the whole card
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                ) {
                                    CardSvgRenderer.drawBeneficialCritterSvg(
                                        drawScope = this,
                                        name = item.name,
                                        center = Offset(size.width / 2f, (size.height / 2f) - 10.dp.toPx()),
                                        sizePx = minOf(size.width, size.height) * 0.78f
                                    )
                                }

                                // Top Check Status Icon
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = ForestGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                // Bottom Gradient Scrim with Critter Name and Role
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color(0xB3000000),
                                                    Color(0xE6000000)
                                                )
                                            )
                                        )
                                        .padding(horizontal = 6.dp, vertical = 5.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = item.name,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.role,
                                            fontSize = 9.5.sp,
                                            color = Color(0xFFA5D6A7),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        if (items.size > initialItemCount) {
            DropdownToggleButton(
                isExpanded = isExpanded,
                totalCount = items.size,
                onClick = { isExpanded = !isExpanded }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Planting Calendar & Location Section
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun PlantingCalendarSection(
    calendar: PlantingCalendarData,
    location: LocationDifficultyData
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionTitleHeader(title = "Planting Calendar (Kalendaryo)")

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardBg,
            border = BorderStroke(1.2.dp, CardBorder),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CalendarTimelineBadge(calendar.indoorLabel, calendar.indoorRange, ForestGreen)
                    CalendarTimelineBadge(calendar.outdoorLabel, calendar.outdoorRange, BlueAccent)
                    CalendarTimelineBadge(calendar.harvestLabel, calendar.harvestRange, GoldAccent)
                }

                HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))

                // Monthly Visual Schedule
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "BUWANANG SIKLO SA PILIPINAS (2026–2027)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        calendar.monthsSchedule.forEach { (month, status) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(month, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Charcoal)
                                Box(
                                    modifier = Modifier
                                        .size(18.dp, 8.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            when {
                                                status.contains("Ani") || status.contains("Harvest") -> GoldAccent
                                                status.contains("Lipat") || status.contains("Balag") || status.contains("Plant Outside") -> BlueAccent
                                                status.contains("Punla") || status.contains("Seedbed") || status.contains("Start Indoors") -> ForestGreen
                                                status.contains("Off-Season") || status.contains("Rain Shelter") -> Color(0xFF00897B)
                                                else -> Color(0xFFCFD8DC)
                                            }
                                        )
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))

                // Location & Difficulty Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("KAHIRAPAN", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.Bold)
                        Text(location.difficulty, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = ForestGreen, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("AGRO-CLIMATIC ZONE", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.Bold)
                        Text(location.hardinessZone, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepBlack, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TEMPERATURA", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.Bold)
                        Text(location.tempRange, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepBlack, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarTimelineBadge(label: String, range: String, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Charcoal)
        }
        Text(range, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Soil Preparation Card
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun SoilPreparationCard(soil: SoilPrepData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitleHeader(title = "Soil Preparation (Paghahanda ng Lupa)")

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardBg,
            border = BorderStroke(1.2.dp, CardBorder),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ASIDO NG LUPA (SOIL PH)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Text(soil.phRange, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    }
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("URI NG LUPA (SOIL TYPE)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Text(soil.soilTypes, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                    }
                }

                HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))
                Text(soil.recommendation, fontSize = 13.5.sp, color = Charcoal, lineHeight = 20.sp)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Growth Timeline Section (Day Milestones)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun GrowthTimelineSection(timeline: List<TimelineStageItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionTitleHeader(title = "Growth Timeline (Yugto ng Pagtubo)")

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardBg,
            border = BorderStroke(1.2.dp, CardBorder),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                timeline.forEachIndexed { idx, stage ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LightGreenBg,
                            modifier = Modifier.width(72.dp)
                        ) {
                            Text(
                                text = stage.dayRange,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ForestGreen,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(stage.stageName, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                            Text(stage.description, fontSize = 13.sp, color = Charcoal, lineHeight = 18.sp)
                            Text("Aksyon: ${stage.keyAction}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ForestGreen)
                        }
                    }

                    if (idx < timeline.size - 1) {
                        HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Agronomic Growing Guides Section
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun GrowingGuidesSection(guides: List<GuideSectionItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitleHeader(title = "Growing Guides (Mga Gabay sa Pagtatanim)")

        guides.forEach { guide ->
            var isExpanded by remember { mutableStateOf(false) }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardBg,
                border = BorderStroke(1.2.dp, CardBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = guide.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { isExpanded = !isExpanded }) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle",
                                tint = ForestGreen
                            )
                        }
                    }

                    Text(
                        text = guide.summary,
                        fontSize = 13.sp,
                        color = MutedText,
                        lineHeight = 18.sp
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))
                            Text(
                                text = guide.fullContent,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = Charcoal
                            )

                            if (guide.tips.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = LightGreenBg,
                                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.25f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Mahahalagang Tip:",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreen
                                        )
                                        guide.tips.forEach { tip ->
                                            Text(
                                                text = "• $tip",
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp,
                                                color = Charcoal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Step-by-Step How-To Guides
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun HowToGuideSection(howTos: List<HowToSectionItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitleHeader(title = "How-tos (Hakbang-sa-Hakbang na Gabay)")

        howTos.forEach { howTo ->
            var isExpanded by remember { mutableStateOf(false) }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardBg,
                border = BorderStroke(1.2.dp, CardBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = howTo.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { isExpanded = !isExpanded }) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle",
                                tint = ForestGreen
                            )
                        }
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            howTo.steps.forEachIndexed { idx, step ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = ForestGreen,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${idx + 1}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Text(
                                        text = step,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        color = Charcoal,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            howTo.safety?.let { safety ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFFF3E0),
                                    border = BorderStroke(1.dp, AmberAlert.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "Paunawa sa Kaligtasan: $safety",
                                        fontSize = 12.5.sp,
                                        color = AmberAlert,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// FAQ Accordion
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun FaqAccordionSection(faqs: List<FaqItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionTitleHeader(title = "Frequently Asked Questions (FAQ)")

        faqs.forEach { faq ->
            var isExpanded by remember { mutableStateOf(false) }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CardBg,
                border = BorderStroke(1.2.dp, CardBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = faq.question,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = ForestGreen
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            HorizontalDivider(
                                color = CardBorder.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Text(
                                text = faq.answer,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = Charcoal
                            )
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Nutrition Facts Table Card
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun NutritionTableCard(nutrition: NutritionTableData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitleHeader(title = "Nutritional Value (Bawat 100g)")

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardBg,
            border = BorderStroke(1.2.dp, CardBorder),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NutritionRow("Portion Size", nutrition.portionSize)
                    NutritionRow("Calories", nutrition.calories)
                    NutritionRow("Carbohydrates", nutrition.carbs)
                }

                HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NutritionRow("Sugar", nutrition.sugar)
                    NutritionRow("Dietary Fiber", nutrition.fiber)
                    NutritionRow("Sucrose", nutrition.sucrose)
                }

                HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))
                Text(nutrition.summary, fontSize = 13.sp, color = Charcoal, lineHeight = 19.sp)
            }
        }
    }
}

@Composable
private fun NutritionRow(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ForestGreen)
    }
}



// ═══════════════════════════════════════════════════════════════════════════════
// Section Header Component
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun SectionTitleHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = DeepBlack,
        letterSpacing = (-0.3).sp
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// Dropdown Toggle Button (Show All ▼ / Show Less ▲)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun DropdownToggleButton(
    isExpanded: Boolean,
    totalCount: Int,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = CardBg,
        border = BorderStroke(1.2.dp, CardBorder),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().height(42.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isExpanded) "Ipakita ang mas kaunti (Show Less ▲)" else "Ipakita ang lahat ng $totalCount (Show All ▼)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Interactive Explanation Modal Overlay
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun ExplanationModalOverlay(
    explanation: ExplanationData,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightGreenBg
                ) {
                    Text(
                        text = explanation.category.uppercase(),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = explanation.title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepBlack
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = explanation.description,
                    fontSize = 14.5.sp,
                    lineHeight = 21.sp,
                    color = Charcoal
                )

                if (explanation.details.isNotEmpty()) {
                    HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))
                    explanation.details.forEach { (k, v) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(k, fontSize = 12.5.sp, color = MutedText, fontWeight = FontWeight.SemiBold)
                            Text(v, fontSize = 13.sp, color = DeepBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                explanation.growerTip?.let { tip ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightGreenBg,
                        border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "Tip sa Pagtatanim: $tip",
                            fontSize = 13.sp,
                            color = ForestGreen,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Naintindihan Ko", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// Sub-Modals: Varieties, Notes, Edit
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun VarietiesListModal(
    varieties: List<String>,
    selectedVariety: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Mga Barayti sa Pilipinas", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = DeepBlack)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                varieties.forEach { v ->
                    val isCurrent = (v.contains(selectedVariety, ignoreCase = true))
                    Surface(
                        onClick = { onSelect(v) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrent) LightGreenBg else Color(0xFFF9FAF8),
                        border = BorderStroke(1.dp, if (isCurrent) ForestGreen else CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(v, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                            if (isCurrent) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ForestGreen)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Isara", fontWeight = FontWeight.Bold, color = ForestGreen)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}


