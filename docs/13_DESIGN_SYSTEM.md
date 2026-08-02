# 13. Design System — Visual Design Tokens

## 📌 Overview
MapTanim's design system defines all visual tokens — colors, typography, shape, elevation, and spacing — derived from the exact UI elements visible in both PNG screenshots.

---

## 🎨 Color Palette

### Primary Brand Colors
```kotlin
// Color.kt
val PrimaryGreen       = Color(0xFF2E7D32)  // Top bar, active nav, SAVE button, EDIT MODE badge
val PrimaryGreenDark   = Color(0xFF1B5E20)  // PLOT label chips, SAVE CHANGES button background
val PrimaryGreenLight  = Color(0xFFE8F5E9)  // Active tool row background in Edit Mode
val PrimaryGreenAccent = Color(0xFF43A047)  // Green ⊕ handle button, Fertilize badge pin
```

### Task / Badge Colors
```kotlin
val TaskBlue           = Color(0xFF1565C0)  // WATER task icon background
val TaskBlueBadge      = Color(0xFF1E88E5)  // Water badge pin on canvas, drag handle
val TaskGreen          = Color(0xFF388E3C)  // FERTILIZE task icon background
val TaskGreenBadge     = Color(0xFF43A047)  // Fertilize badge pin on canvas
val TaskAmber          = Color(0xFFF57F17)  // HARVEST task icon background
val TaskAmberBadge     = Color(0xFFFFA000)  // Harvest badge pin on canvas
val AlertRed           = Color(0xFFC62828)  // PEST_ALERT task icon background, EXIT button
val AlertRedBadge      = Color(0xFFE53935)  // Pest badge pin on canvas
val WarningYellow      = Color(0xFFFDD835)  // WARNING badge pin on canvas
```

### Surface & Background
```kotlin
val SurfaceWhite       = Color(0xFFFFFFFF)  // Panel cards, left panel, right toolbar
val BackgroundLight    = Color(0xFFF5F5F5)  // Screen background
val SurfaceVariant     = Color(0xFFF1F8E9)  // Subtle tinted panels
```

### Edit Mode Selection Colors
```kotlin
val SelectionBlue      = Color(0xFF1E88E5)  // Dashed selection border, drag handle
val SelectionHandle    = Color(0xFFFFFFFF)  // White corner/midpoint handles
val DeleteHandleRed    = Color(0xFFEF5350)  // Quick-delete ✕ handle
```

### Soil Swatch Colors (Left-to-Right Order)
```kotlin
val SoilLoam           = Color(0xFF3E2723)  // Dark reddish-brown
val SoilClay           = Color(0xFF795548)  // Medium warm brown
val SoilSandy          = Color(0xFFD7CCC8)  // Light tan/beige
val SoilSilty          = Color(0xFF90A4AE)  // Blue-gray
val SoilPeaty          = Color(0xFF212121)  // Near-black
val SoilChalky         = Color(0xFFECEFF1)  // Off-white/light gray
```

### Text Colors
```kotlin
val TextPrimary        = Color(0xFF1C1B1F)  // Primary text (dark)
val TextSecondary      = Color(0xFF49454F)  // Muted sub-labels
val TextOnPrimary      = Color(0xFFFFFFFF)  // Text on green backgrounds
val TextDeleteRed      = Color(0xFFC62828)  // Delete tool label in Edit Mode
```

---

## 📝 Typography

```kotlin
// Typography.kt — using Outfit or Inter from Google Fonts
val MapTanimTypography = Typography(
    // Screen title
    titleLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    // Card section headers (e.g., "TODAY'S TASKS", "EDIT TOOLS")
    titleMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    ),
    // Task title, tool name
    bodyLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Sub-labels, descriptions
    bodyMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        color = TextSecondary
    ),
    // Tip text, smallest labels
    bodySmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = TextSecondary
    ),
    // Soil swatch labels
    labelSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    )
)
```

---

## 📐 Shape Tokens

```kotlin
val Shapes = Shapes(
    // Pill buttons (EDIT MODE badge, task icon badges)
    extraSmall = RoundedCornerShape(20.dp),
    // Buttons (SAVE CHANGES, tool chips)
    small = RoundedCornerShape(8.dp),
    // Cards (panels, toolbars)
    medium = RoundedCornerShape(12.dp),
    // Bottom sheets
    large = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
)
```

---

## 🪜 Elevation Tokens

| Component | Elevation | Shadow |
|-----------|----------|--------|
| Screen background | 0dp | None |
| Panel cards (left panel) | 2dp | Subtle |
| Right floating toolbar | 8dp | Medium shadow |
| Selection handles | 12dp | Prominent |
| Dialogs / Bottom sheets | 16dp | Heavy |

---

## 📏 Spacing & Sizing

| Token | Value | Usage |
|-------|-------|-------|
| `spacing_xs` | 4dp | Icon-text gap, intra-chip |
| `spacing_sm` | 8dp | Between soil swatches, icon padding |
| `spacing_md` | 12dp | Card internal padding |
| `spacing_lg` | 16dp | Section padding |
| `spacing_xl` | 24dp | Screen-edge margins |
| `panel_width` | 220dp | Left panel fixed width |
| `toolbar_width` | 64dp | Right floating toolbar width |
| `top_bar_height` | 56dp | Top bar height |
| `bottom_nav_height` | 56dp | Bottom navigation bar height |
| `edit_bottom_bar_height` | 60dp | Edit mode action bar height |
| `task_row_height` | 52dp | Height of each TaskRow |
| `soil_swatch_size` | 40dp | Diameter of soil color swatch |
| `handle_drag_size` | 24dp | Blue drag handle diameter |
| `handle_corner_size` | 16dp | Corner resize handle diameter |
| `handle_mid_size` | 12dp | Midpoint resize handle diameter |
| `handle_action_size` | 32dp | Green ⊕ action button diameter |

---

## 🌓 Theme

```kotlin
// Theme.kt
@Composable
fun MapTanimTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = PrimaryGreen,
        onPrimary = Color.White,
        primaryContainer = PrimaryGreenLight,
        surface = SurfaceWhite,
        background = BackgroundLight,
        error = AlertRed,
        onSurface = TextPrimary,
        onSurfaceVariant = TextSecondary
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MapTanimTypography,
        shapes = Shapes,
        content = content
    )
}
```
