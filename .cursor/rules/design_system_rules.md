# Pera Wallet Android - Design System Rules

Comprehensive design system documentation for AI assistants integrating Figma designs via Model Context Protocol (MCP).

---

## Overview

**Architecture:** Hybrid Android app (Jetpack Compose + XML Legacy)

- **Primary UI:** Jetpack Compose with Material 3
- **Legacy:** Fragment-based XML layouts
- **Theme:** Dual-mode (Light/Dark) with semantic tokens
- **Build:** Gradle with Kotlin DSL

---

## Color System

### Locations

**Compose (Primary):**

- `app/src/main/kotlin/com/algorand/android/ui/compose/theme/Color.kt` - ColorPalette & PeraColor interface
- `app/src/main/kotlin/com/algorand/android/ui/compose/theme/PeraLightColor.kt` - Light theme
- `app/src/main/kotlin/com/algorand/android/ui/compose/theme/PeraDarkColor.kt` - Dark theme

**XML (Legacy):**

- `app/src/main/res/values/colors.xml` - Light mode
- `app/src/main/res/values-night/colors.xml` - Dark mode

### Color Families

10 families with V50-V900 variants:

- **Turquoise** - Primary accent, success
- **Purple** - Helper elements
- **Salmon** - Negative/error states
- **Gray** - Backgrounds, text, layers
- **Yellow** - Warnings, dark mode primary
- **Blue, Pink, Navy, Red** - Additional accents

### Semantic Mapping Examples

| Token                       | Light          | Dark        | Use             |
|-----------------------------|----------------|-------------|-----------------|
| `button.primary.background` | Gray.V800      | Yellow.V400 | Primary CTA     |
| `button.primary.text`       | Gray.V50       | Gray.V900   | Button text     |
| `text.main`                 | Gray.V900      | Gray.V100   | Primary text    |
| `text.secondary`            | Gray.V500      | Gray.V400   | Secondary text  |
| `background.primary`        | Gray.V50       | Gray.V900   | Main background |
| `link.primary`              | Turquoise.V600 | Yellow.V400 | Links           |

### Usage

```kotlin
// GOOD: Use semantic tokens
Box(modifier = Modifier.background(PeraTheme.colors.background.primary))
Text(text = "Hello", color = PeraTheme.colors.text.main)

// BAD: Hardcoded values
Box(modifier = Modifier.background(Color(0xFF3C3C3C)))
Text(text = "Hello", color = Color.Black)
```

### Adding New Colors

1. Add to `ColorPalette` in `Color.kt`
2. Add semantic interface to `PeraColor`
3. Implement in `PeraLightColor.kt`
4. Implement in `PeraDarkColor.kt`

---

## Typography System

### Locations

- `app/src/main/kotlin/com/algorand/android/ui/compose/typography/PeraTypography.kt` - Main structure
- `app/src/main/kotlin/com/algorand/android/ui/compose/typography/PeraTypography[Title|Body|Footnote|Caption].kt` -
  Implementations
- `app/src/main/res/font/` - Font files (DM Sans, DM Mono)

### Hierarchy

| Category      | Size | Line Height | Weight                   | Letter Spacing |
|---------------|------|-------------|--------------------------|----------------|
| Title Large   | 36sp | 48sp        | Regular/Medium/Mono      | -0.72sp        |
| Title Regular | 32sp | 40sp        | Regular/Medium/Bold      | -0.64sp        |
| Title Small   | 28sp | 32sp        | Regular/Medium           | -0.56sp        |
| Body Large    | 19sp | 28sp        | Regular/Medium/Mono      | 0sp            |
| Body Regular  | 15sp | 24sp        | Regular/Medium/Bold/Mono | 0sp            |
| Footnote      | 13sp | 20sp        | Regular/Bold/Medium/Mono | 0sp            |
| Caption       | 11sp | 16sp        | Regular/Bold/Medium/Mono | 0sp            |

### Font Variants

Each level has 3-4 variants:

- `.sans` - DM Sans Regular
- `.sansMedium` - DM Sans Medium
- `.sansBold` - DM Sans Bold
- `.mono` - DM Mono

### Usage

```kotlin
// GOOD: Use typography tokens
Text(
    text = "Headline",
    style = PeraTheme.typography.title.regular.sansMedium
)

// BAD: Hardcoded
Text(
    text = "Headline",
    fontSize = 32.sp,
    fontWeight = FontWeight.Medium
)
```

---

## Component Library

### Location

`app/src/main/kotlin/com/algorand/android/ui/compose/widget/`

### Categories (87+ components)

- `button/` - Primary, Secondary, Tertiary buttons
- `text/` - Headline, Title, Body, Link text
- `textfield/` - Input fields, slim variants
- `icon/` - Icon wrappers, round shapes
- `bottomsheet/` - Bottom sheet components
- `asset/` - Asset display components
- `chart/` - Chart visualizations
- `progress/` - Progress indicators, loaders
- `quickaction/` - Quick action buttons
- `modifier/` - Custom modifiers

### Core Components

**Buttons:**

```kotlin
PeraPrimaryButton(
    text: String,
    onClick: () -> Unit,
    state: PeraButtonState = ENABLED,  // ENABLED, DISABLED, LOADING
    iconRes: Int? = null,
    iconPosition: IconPosition = START,
    modifier: Modifier = Modifier
)
```

- Min height: 52dp (normal), 40dp (small)
- Corner radius: 4dp (normal), 32dp (pill)
- Primary: Dark bg (light), Yellow bg (dark)
- Secondary: Light bg (light), Dark bg (dark)

**Text:**

```kotlin
PeraHeadlineText(text, modifier, color, maxLines, overflow)
PeraTitleText(text, modifier, color)
PeraBodyText(text, modifier, color, maxLines)
PeraLinkText(text, modifier, onClick)
```

**TextFields:**

```kotlin
PeraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null,
    modifier: Modifier = Modifier
)
```

**Icons:**

```kotlin
PeraIcon(
    painter: Painter,
    contentDescription: String?,
    tint: Color = Color.Unspecified,
    modifier: Modifier = Modifier
)
```

- Standard sizes: 20dp (small), 24dp (normal), 36dp (large), 40dp (xlarge), 64dp (standalone)

### Component Naming Convention

- Prefix: `Pera`
- Descriptive: `PeraPrimaryButton` (not `PeraButton1`)
- One component per file
- File name matches component name

### Creating New Components

1. Create file in `widget/[category]/ComponentName.kt`
2. Use design tokens, not hardcoded values
3. Support states (enabled, disabled, loading, error)
4. Add previews for light/dark themes
5. Provide accessibility (contentDescription)

```kotlin
@Composable
fun PeraMyComponent(
    // Required params
    title: String,
    // Optional params with defaults
    modifier: Modifier = Modifier,
    description: String? = null,
    // Callbacks last
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .background(PeraTheme.colors.background.secondary)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = PeraTheme.typography.title.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    PeraTheme {
        PeraMyComponent(title = "Preview")
    }
}
```

---

## Asset Management

### Structure

```
app/src/main/res/
├── drawable/              # Default, vector drawables
├── drawable-hdpi/         # 1.5x density
├── drawable-mdpi/         # 1x density
├── drawable-xhdpi/        # 2x density
├── drawable-xxhdpi/       # 3x density
├── drawable-xxxhdpi/      # 4x density
├── drawable-night/        # Dark theme variants
└── font/                  # Custom fonts
```

### Counts

- 286+ backgrounds (`bg_*.xml`)
- 80+ icons (`ic_*.xml`)
- 349 XML layouts
- 75 navigation graphs

### Naming Convention

**Format:** `[type]_[description]_[details].xml`

**Prefixes:**

- `bg_` - Backgrounds, shapes
- `ic_` - Icons
- `img_` - Images
- `anim_` - Animations

**Examples:**

```
bg_rectangle_radius_4dp.xml
bg_layer_gray_lighter.xml
ic_staking.xml
ic_search.xml
img_feature_banner.png
```

### Adding Assets from Figma

**Icons (Vector):**

1. Export SVG from Figma
2. Convert to Android Vector Drawable (Android Studio: New → Vector Asset → Local file)
3. Save as `drawable/ic_[name].xml`
4. Use: `painterResource(R.drawable.ic_[name])`

**Images (Raster):**

1. Export PNG at 1x, 2x, 3x, 4x
2. Place in `drawable-mdpi/`, `drawable-xhdpi/`, `drawable-xxhdpi/`, `drawable-xxxhdpi/`
3. Use: `painterResource(R.drawable.img_[name])`

**Dark Variants:**

- Place dark theme variants in `drawable-night/`

---

## Icon System

### Sources

1. **Material Icons:**
   ```kotlin
   import androidx.compose.material.icons.Icons
   Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
   ```

2. **Custom Vector Drawables:**
   ```kotlin
   PeraIcon(
       painter = painterResource(R.drawable.ic_staking),
       contentDescription = "Staking"
   )
   ```

### Sizing

```kotlin
modifier = Modifier.size(20.dp)  // Small action
modifier = Modifier.size(24.dp)  // Normal
modifier = Modifier.size(36.dp)  // Large
modifier = Modifier.size(40.dp)  // Extra large
modifier = Modifier.size(64.dp)  // Feature icon
```

### Coloring

```kotlin
// Use semantic color
tint = PeraTheme.colors.icon.primary

// Preserve original colors
tint = Color.Unspecified
```

---

## Dimensions & Spacing

### File

`app/src/main/res/values/dimens.xml`

### Spacing Scale

```
2dp (xxxsmall)
4dp (xxsmall)
8dp (xsmall)
12dp (small)
16dp (normal)     ← Most common
20dp (large)
24dp (xlarge)
32dp (xxlarge)
36dp (xxxlarge)
40dp (xxxxlarge)
48dp (xxxxxlarge)
```

### In Compose

```kotlin
// Use dp values directly
modifier = Modifier.padding(16.dp)
modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
```

### Component Dimensions

| Component       | Height | Corner Radius | Padding |
|-----------------|--------|---------------|---------|
| Button (normal) | 52dp   | 4dp           | 16dp    |
| Button (small)  | 40dp   | 32dp          | 12dp    |
| TextField       | 48dp+  | 8dp           | 16dp    |
| Card            | Wrap   | 8dp           | 16dp    |
| Toolbar         | 44dp   | -             | -       |

---

## Project Structure

```
app/src/main/kotlin/com/algorand/android/ui/
├── compose/
│   ├── theme/
│   │   ├── Color.kt                    # ColorPalette, PeraColor interface
│   │   ├── PeraLightColor.kt          # Light theme implementation
│   │   ├── PeraDarkColor.kt           # Dark theme implementation
│   │   └── PeraTheme.kt               # Theme composition
│   ├── typography/
│   │   ├── PeraTypography.kt          # Main structure
│   │   ├── PeraTypographyTitle.kt     # Title styles
│   │   ├── PeraTypographyBody.kt      # Body styles
│   │   ├── PeraTypographyFootnote.kt  # Footnote styles
│   │   └── PeraTypographyCaption.kt   # Caption styles
│   └── widget/                         # 87+ components
│       ├── button/
│       ├── text/
│       ├── textfield/
│       ├── icon/
│       ├── bottomsheet/
│       ├── asset/
│       ├── chart/
│       ├── progress/
│       ├── quickaction/
│       └── modifier/
└── [feature modules]/
    └── *Fragment.kt                    # Feature screens

app/src/main/res/
├── values/
│   ├── colors.xml                      # Light colors
│   ├── dimens.xml                      # Dimensions
│   ├── styles.xml                      # XML styles (765 lines)
│   └── strings.xml                     # Text resources
├── values-night/
│   ├── colors.xml                      # Dark colors
│   └── styles.xml                      # Dark styles
├── drawable/                           # Vector drawables (286+)
├── drawable-night/                     # Dark theme drawables
├── font/                               # DM Sans, DM Mono
├── layout/                             # XML layouts (349)
└── navigation/                         # Nav graphs (75)
```

---

## Figma Integration Workflow

### 1. Analyze Figma Design

Extract:

- Colors (fill, stroke, background)
- Typography (font, size, weight, line height, letter spacing)
- Spacing/padding
- Corner radius, shadows, borders
- Component states
- Icons and assets

### 2. Update Design Tokens (if new)

**Colors:**

```kotlin
// 1. Add to ColorPalette (Color.kt)
val NewFamily = ColorFamily(
    V900 = Color(0xFF...),
    // ... V800 down to V50
)

// 2. Add to PeraColor interface
interface MyFeature {
    val primary: Color
    val secondary: Color
}

// 3. Implement in PeraLightColor.kt
override val myFeature = object : PeraColor.MyFeature {
    override val primary = ColorPalette.Turquoise.V600
    override val secondary = ColorPalette.Gray.V200
}

// 4. Implement in PeraDarkColor.kt
override val myFeature = object : PeraColor.MyFeature {
    override val primary = ColorPalette.Yellow.V400
    override val secondary = ColorPalette.Gray.V700
}
```

**Typography:**

```kotlin
// Add new style to PeraTypography*.kt if needed
data class NewStyle(
    val sans: TextStyle,
    val sansMedium: TextStyle
)
```

### 3. Create/Update Components

```kotlin
// widget/[category]/MyComponent.kt
@Composable
fun PeraMyComponent(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .background(PeraTheme.colors.background.secondary)
            .padding(16.dp)
            .clickable { onClick?.invoke() }
    ) {
        Text(
            text = title,
            style = PeraTheme.typography.title.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
}
```

### 4. Add Assets

**Icons:**

1. Export SVG from Figma
2. Convert to Vector Drawable
3. Save as `drawable/ic_[name].xml`

**Images:**

1. Export PNG at multiple densities
2. Place in `drawable-*dpi/` directories

### 5. Implement Screen

```kotlin
@Composable
fun MyFeatureScreen(
    viewModel: MyViewModel = hiltViewModel()
) {
    PeraTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PeraTheme.colors.background.primary)
                .padding(16.dp)
        ) {
            PeraHeadlineText("Title")
            PeraBodyText("Description")
            PeraPrimaryButton(
                text = "Action",
                onClick = { /* ... */ }
            )
        }
    }
}
```

### 6. Test Both Themes

```kotlin
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Preview() {
    PeraTheme {
        MyFeatureScreen()
    }
}
```

---

## Code Patterns

### ✅ DO

```kotlin
// Use design tokens
Box(modifier = Modifier.background(PeraTheme.colors.background.primary))
Text(
    text = "Hello",
    style = PeraTheme.typography.body.regular.sansMedium,
    color = PeraTheme.colors.text.main
)

// Support states
enum class ButtonState { ENABLED, DISABLED, LOADING }

// Add previews
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Preview() { /* ... */ }

// Provide accessibility
PeraIcon(
    painter = painterResource(R.drawable.ic_search),
    contentDescription = "Search"
)

// Use semantic naming
PeraPrimaryButton (not PeraButton1)
ic_staking.xml (not icon_1.xml)
```

### ❌ DON'T

```kotlin
// Hardcode values
Box(modifier = Modifier.background(Color(0xFF3C3C3C)))
Text(text = "Hello", fontSize = 16.sp, color = Color.Black)

// Skip states
// Components should support ENABLED, DISABLED, LOADING, ERROR

// Forget previews
// Always add light + dark previews

// Skip accessibility
Icon(/* no contentDescription */)

// Use generic naming
PeraButton1, color_1, icon_1
```

---

## Theme System

### PeraTheme Setup

```kotlin
@Composable
fun PeraTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val peraColors = if (isDarkTheme) {
        PeraDarkColor()
    } else {
        PeraLightColor()
    }

    CompositionLocalProvider(
        localPeraColors provides peraColors
    ) {
        MaterialTheme(
            colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme(),
            content = content
        )
    }
}
```

### Usage

```kotlin
// Wrap all screens
@Composable
fun MyScreen() {
    PeraTheme {
        // Content automatically uses theme
    }
}

// Access theme values
val bgColor = PeraTheme.colors.background.primary
val textStyle = PeraTheme.typography.body.regular.sansMedium
```

---

## Quick Reference

### File Paths

| Task                | Path                                                                 |
|---------------------|----------------------------------------------------------------------|
| Add color           | `app/src/main/kotlin/com/algorand/android/ui/compose/theme/Color.kt` |
| Light colors        | `.../theme/PeraLightColor.kt`                                        |
| Dark colors         | `.../theme/PeraDarkColor.kt`                                         |
| Typography          | `.../typography/PeraTypography*.kt`                                  |
| Button component    | `.../widget/button/`                                                 |
| Text component      | `.../widget/text/`                                                   |
| TextField component | `.../widget/textfield/`                                              |
| Add icon            | `app/src/main/res/drawable/ic_[name].xml`                            |
| Add dimension       | `app/src/main/res/values/dimens.xml`                                 |

### Color Usage

| Token                       | Light          | Dark        | Use          |
|-----------------------------|----------------|-------------|--------------|
| `background.primary`        | Gray.V50       | Gray.V900   | Main bg      |
| `text.main`                 | Gray.V900      | Gray.V100   | Primary text |
| `button.primary.background` | Gray.V800      | Yellow.V400 | Primary CTA  |
| `link.primary`              | Turquoise.V600 | Yellow.V400 | Links        |

### Common Specs

| Component      | Height | Radius | Padding |
|----------------|--------|--------|---------|
| Primary Button | 52dp   | 4dp    | 16dp    |
| Small Button   | 40dp   | 32dp   | 12dp    |
| TextField      | 48dp+  | 8dp    | 16dp    |
| Card           | Wrap   | 8dp    | 16dp    |

---

## Summary

**Key Principles:**

1. **Always use design tokens** - No hardcoded colors, typography, or dimensions
2. **Support both themes** - Light and dark mode
3. **Support all states** - Enabled, disabled, loading, error
4. **Add previews** - Light + dark for all components
5. **Provide accessibility** - Content descriptions for all icons/images
6. **Follow naming conventions** - Semantic, descriptive names
7. **One component per file** - Clear organization
8. **Reuse existing components** - Don't recreate what exists

**When implementing Figma designs:**

1. Extract design tokens
2. Update theme files if new tokens needed
3. Build/update components using tokens
4. Add assets as vector drawables
5. Compose screens using component library
6. Test both themes
7. Add accessibility

**Component Library:** 87+ reusable Compose widgets ready to use. Check `widget/` subdirectories before creating new
components.

**Design System:** Comprehensive semantic token system with 10 color families, 4-level typography hierarchy, and dual
theme support.
