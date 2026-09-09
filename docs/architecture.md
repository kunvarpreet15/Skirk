# Skirk Architecture Documentation

## 1. Overview

**Skirk** is a customizable Android StandBy dashboard inspired by iOS StandBy mode. When placed on a charger (or docked), Skirk turns Android devices into glanceable smart displays featuring customizable widgets, flexible layouts, multi-panel navigation, and fluid vertical/horizontal gesture interactions.

This document defines the foundational architecture established in Phase 0 to ensure long-term scalability, strict separation of concerns, and clean extension points for subsequent phases.

---

## 2. High-Level Architecture Diagram

```text
┌─────────────────────────────────────────────────────────────────────────────┐
│                            PRESENTATION LAYER                               │
│                                                                             │
│  ┌──────────────────────┐  ┌─────────────────────────────────────────────┐  │
│  │   MainActivity       │  │  SkirkNavGraph (Jetpack Navigation)          │  │
│  │   (Single Activity)  │  │  Routes: Dashboard, Settings, Editor, etc.    │  │
│  └──────────┬───────────┘  └──────────────────────┬──────────────────────┘  │
│             │                                     │                         │
│  ┌──────────▼─────────────────────────────────────▼──────────────────────┐  │
│  │   DashboardScreen                                                     │  │
│  │   - Observes DashboardUiState via StateFlow                           │  │
│  │   - Renders Panels, Layout Containers, and Widget Slots               │  │
│  └──────────────────────────────────┬────────────────────────────────────┘  │
│                                     │                                       │
│  ┌──────────────────────────────────▼────────────────────────────────────┐  │
│  │   DashboardViewModel                                                  │  │
│  │   - Unidirectional Data Flow (StateFlow / User Intents)               │  │
│  └──────────────────────────────────┬────────────────────────────────────┘  │
└─────────────────────────────────────┼───────────────────────────────────────┘
                                      │ Injected via AppContainer
┌─────────────────────────────────────▼───────────────────────────────────────┐
│                              DOMAIN LAYER                                   │
│                                                                             │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  Core Domain Models                                                   │  │
│  │  - Dashboard  →  Panel  →  PanelLayout  →  WidgetSlot  →  WidgetInstance│
│  │  - WidgetDefinition, WidgetDesign, WidgetConfig                       │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  ┌────────────────────────────────────┐  ┌───────────────────────────────┐  │
│  │  Repository Interfaces             │  │  Widget Subsystem Engine      │  │
│  │  - DashboardRepository             │  │  - WidgetRegistry             │  │
│  │  - UserSettingsRepository          │  │  - WidgetProvider Contract    │  │
│  └────────────────────────────────────┘  │  - WidgetContentRenderer      │  │
│                                          └───────────────────────────────┘  │
└─────────────────────────────────────▲───────────────────────────────────────┘
                                      │ Implemented by Data Layer
┌─────────────────────────────────────┴───────────────────────────────────────┐
│                               DATA LAYER                                    │
│                                                                             │
│  ┌──────────────────────────────────┐  ┌─────────────────────────────────┐  │
│  │  DashboardRepositoryImpl         │  │  UserSettingsRepositoryImpl     │  │
│  │  - In-memory StateFlow Cache     │  │  - DataStore Preferences        │  │
│  │  - Atomic Local Storage          │  │  - Theme & StandBy Config       │  │
│  └──────────────────┬───────────────┘  └─────────────────────────────────┘  │
│                     │                                                       │
│  ┌──────────────────▼────────────────────────────────────────────────────┐  │
│  │  Local Storage / Persistence                                          │  │
│  │  - JsonDashboardFileStorage (AtomicFile + Kotlinx Serialization)      │  │
│  │  - DefaultDashboardFactory (Default Out-of-the-Box Configuration)     │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Core Domain Hierarchy (Phase 2 Engine)

The dashboard domain is structured hierarchically:

```text
Dashboard
    │
    ├── Panel (Ordered list of horizontally navigable screens)
    │     │
    │     ├── Layout (PanelLayout defining slot geometry and capacity)
    │     │
    │     ├── Slot 1 (WidgetSlot with activeWidgetIndex and stack)
    │     │     ├── WidgetInstance (widgetTypeId, selectedDesignId, config, isEnabled)
    │     │     ├── WidgetInstance
    │     │     └── WidgetInstance
    │     │
    │     └── Slot 2 (WidgetSlot)
    │           ├── WidgetInstance
    │           └── WidgetInstance
    │
    └── Panel
          ├── Layout (e.g., Grid4, Single, TwoSplitVertical)
          └── Slot 1..N
```

### Domain Models Specification
| Model | Responsibility | Key Properties / Methods |
|---|---|---|
| `Dashboard` | Root container representing the user's dashboard configuration. | `id`, `schemaVersion`, `panels: List<Panel>`, `selectedPanelIndex: Int`, `addPanel()`, `removePanel()`, `reorderPanels()`, `selectPanel()` |
| `Panel` | A single horizontally navigable screen in the dashboard. | `id`, `name`, `layout: PanelLayout`, `slots: List<WidgetSlot>`, `getSlotById()`, `updateSlotById()`, `addSlot()`, `removeSlot()` |
| `PanelLayout` | Sealed class defining geometric arrangement and slot capacity. | `Single` (1 slot), `TwoSplitHorizontal` (2 slots), `TwoSplitVertical` (2 slots), `Grid4` (4 slots), `Grid6` (6 slots), `Custom` |
| `WidgetSlot` | An individual slot on a panel containing a vertically swipeable stack of widgets. | `id`, `slotIndex`, `widgets: List<WidgetInstance>`, `activeWidgetIndex`, `activeWidget`, `addWidget()`, `removeWidget()`, `nextWidget()`, `previousWidget()` |
| `WidgetInstance` | A configured widget assigned to a slot. | `id`, `widgetTypeId`, `selectedDesignId`, `config: WidgetConfig`, `isEnabled: Boolean` |
| `WidgetDefinition` | Static metadata registered in the engine for widget discovery. | `id`, `displayName`, `description`, `category: WidgetCategory`, `supportedLayouts`, `availableDesigns`, `defaultDesignId` |
| `WidgetDesign` | Declares a visual theme/variation (e.g. Minimal, Large, Retro, Modern). | `id`, `displayName`, `previewResId` |
| `WidgetConfig` | Immutable key-value store for widget-specific configuration. | `parameters: Map<String, String>` |

---

## 4. Presentation Engine: Compose Layout System

Phase 2 introduces the modular Jetpack Compose rendering system:

```text
DashboardView (Orchestrates panels, indicators, and panel navigation)
    │
    └── PanelLayoutContainer (Arranges slots according to PanelLayout)
          │
          ├── Single               ──► Full-bleed single slot
          ├── TwoSplitHorizontal   ──► Row: 2 equal-width slots
          ├── TwoSplitVertical     ──► Column: 2 equal-height slots
          ├── Grid4                ──► 2x2 Grid (4 equal slots)
          └── Grid6                ──► 2x3 or 3x2 Grid (6 slots)
                │
                └── WidgetSlotView (Renders active widget from stack or empty placeholder)
                      │
                      ├── Stack Indicator: "[1/3]" badge with ▲ / ▼ cycling controls
                      └── Widget Content: Decoupled WidgetContentRenderer via WidgetRegistry
```

### Components
1. **`DashboardView`**:
   - Reusable across `DashboardScreen` (portrait) and `StandByScreen` (landscape StandBy mode).
   - Displays active panel, animated panel indicator dots, and panel switching buttons.
2. **`PanelLayoutContainer`**:
   - Inspects `panel.layout` and builds the appropriate Compose arrangement (`Row`, `Column`, `Grid`).
   - Ensures consistent padding, spacing, and slot sizing regardless of orientation.
3. **`WidgetSlotView`**:
   - Handles multi-widget stack display, showing `activeWidgetIndex + 1 / widgets.size`.
   - Exposes temporary `▲` / `▼` buttons to cycle through stacked widgets.
   - Gracefully displays an empty slot call-to-action when `widgets.isEmpty()`.

---

## 5. Widget Engine & Decoupled Registry Pattern

To avoid a giant monolithic `when` statement and ensure independent maintainability, Skirk decouples:
1. **Widget Type**: The functional domain (e.g., `DIGITAL_CLOCK`).
2. **Widget Configuration**: Custom settings (e.g., 24h format, time zone).
3. **Widget Visual Design**: Visual rendering variant (e.g., `minimal`, `large`, `retro`, `modern`).

### Registry & Provider Structure

```text
               ┌────────────────────────┐
               │     WidgetRegistry     │
               └───────────┬────────────┘
                           │ registers
              ┌────────────┴────────────┐
              ▼                         ▼
   ┌─────────────────────┐   ┌─────────────────────┐
   │ DigitalClockProvider│   │ BatteryInfoProvider │  ...
   └──────────┬──────────┘   └─────────────────────┘
              │ resolves
       ┌──────┴───────────────┐
       ▼                      ▼
┌──────────────┐       ┌──────────────┐
│LargeRenderer │       │MinimalRenderer
└──────────────┘       └──────────────┘
       │                      │
       └──────────┬───────────┘
                  │ fallback if unassigned
                  ▼
       ┌────────────────────────┐
       │PlaceholderWidgetRenderer
       └────────────────────────┘
```

- Each widget implements `WidgetProvider`.
- `WidgetProvider` publishes a `WidgetDefinition` and provides a `WidgetContentRenderer` for any given `designId`.
- The dashboard container calls `registry.getRenderer(widgetTypeId, designId).Render(...)`, eliminating compile-time dependencies between the dashboard and individual widget implementations.
- If a widget or design is not registered, `WidgetRegistry` safely falls back to `PlaceholderWidgetRenderer`, rendering a styled card with category accent badges, widget title, and design name without crashing.

---

## 6. Gesture Navigation & Dashboard Interaction (Phase 3 Engine)

Phase 3 implements the interactive gesture handling and navigation architecture for the Skirk StandBy dashboard:

```text
                    Gesture
                       │
             ┌─────────┴─────────┐
             │                   │
        Horizontal            Vertical
             │                   │
             ↓                   ↓
       Panel Manager        Slot Manager
             │                   │
             ↓                   ↓
       Active Panel        Active Widget
```

### 1. Panel Pager & Horizontal Navigation
- Implemented via Jetpack Compose's foundation `HorizontalPager` with `rememberPagerState`.
- **Bidirectional State Synchronization**:
  - Pager page changes update `Dashboard.activePanelIndex` via `onSelectPanel(index)` and are atomically saved to persistent storage.
  - External updates to `dashboard.activePanelIndex` animate the pager to the target page via `pagerState.animateScrollToPage(index)`.
- **Boundaries & Edge Cases**:
  - Single panel dashboards disable scrolling automatically (`userScrollEnabled = false`).
  - Boundary overscroll is handled smoothly by native physics without invalid indices or crashes.

### 2. Widget Slot Vertical Navigation
- Each `WidgetSlotView` is equipped with `slotVerticalSwipe` based on `Modifier.draggable(Orientation.Vertical)`:
  - **Swipe UP**: Advances to the next widget in the slot stack cyclically.
  - **Swipe DOWN**: Returns to the previous widget in the slot stack cyclically.
- Slots with $\le 1$ widget safely disable vertical dragging.
- State is persisted immediately via `DashboardRepository.setActiveWidget(panelId, slotIndex, widgetIndex)`.

### 3. Directional Animated Transitions
- Switching widgets inside a slot uses `AnimatedContent` configured via `WidgetTransitionSpec`:
  - **Forward (Swipe UP)**: Outgoing widget slides out towards the top (`slideOutVertically { -height } + fadeOut()`), incoming widget slides in from the bottom (`slideInVertically { height } + fadeIn()`).
  - **Backward (Swipe DOWN)**: Outgoing widget slides out towards the bottom (`slideOutVertically { height } + fadeOut()`), incoming widget slides in from the top (`slideInVertically { -height } + fadeIn()`).

### 4. Gesture Conflict Resolution
- **Horizontal Swipe**: Handled by `HorizontalPager` for switching panels.
- **Vertical Swipe in Slot**: Handled by `slotVerticalSwipe` along `Orientation.Vertical` without capturing horizontal delta.
- **Tap**: Passes cleanly through to child interactive widgets (buttons, media toggles, checkboxes).
- **Long Press**: Reserved for dashboard customization mode without accidental activation.

### 5. Centralized Gesture Thresholds (`GestureConfig`)
- Displacement threshold: `40.dp`.
- Fling velocity threshold: `500f` px/sec.
- Directional dominance ratio: $1.25\times$ (i.e. $|dy| \ge |dx| \times 1.25$ for vertical; $|dx| \ge |dy| \times 1.25$ for horizontal).

### 6. StandBy Page Indicator & Accessibility
- **`StandByPageIndicator`**: Subtle animated pill indicator (`● ○ ○`) communicating active panel index without distracting from the StandBy display.
- **Accessibility Semantics**: `CustomAccessibilityAction`s for "Next Panel", "Previous Panel", "Next Widget", and "Previous Widget" alongside dynamic content descriptions.

---

## 7. Persistence Strategy

Skirk persists the entire dashboard hierarchy to disk so that custom configurations survive application updates and process termination:

1. **Structured Dashboard Config**:
   - Stored in internal app storage via `JsonDashboardFileStorage`.
   - Utilizes `kotlinx.serialization.json.Json` with `AtomicFile` to guarantee atomicity and avoid corrupted writes during abrupt power cuts.
   - Captures `schemaVersion = 1`, panel order, layouts, slots, stacked widget instances, selected design IDs, custom configurations, and active scroll indices.
2. **Global Preferences**:
   - Managed via `UserSettingsRepositoryImpl` using Jetpack `DataStore Preferences`.
   - Stores user preferences: theme mode (`DARK`, `LIGHT`, `SYSTEM`), dynamic color toggles, and screen sleep prevention flags.
3. **First-Run Factory**:
   - `DefaultDashboardFactory` provides an immediate, functional out-of-the-box configuration with 3 panels ("Clock & Battery", "Media & Focus", "Glance Grid") if no persisted state exists.

---

## 8. Navigation Architecture

Navigation is built on Jetpack Compose Navigation (`androidx.navigation.compose`) with strongly-typed destinations (`Screen` sealed class):

| Destination Route | Screen Target | Purpose |
|---|---|---|
| `standby_dashboard` | `DashboardScreen` | Main StandBy glanceable dashboard. |
| `settings` | `SettingsScreen` | App display, theme, and standby settings. |
| `dashboard_editor` | `DashboardEditorScreen` | Customization mode for panels and layouts. |
| `widget_picker/{panelId}/{slotIndex}` | `WidgetPickerScreen` | Selection interface for adding widgets to a slot. |
| `widget_configuration/{instanceId}` | `WidgetConfigurationScreen` | Detailed settings editor for a specific widget instance. |

---

## 9. Developer Guide: How to Add a New Widget

Adding a new widget requires **zero modifications** to the core dashboard, layout engine, or persistence layers.

### Step 1: Define the Widget Type ID
Add a unique identifier in `widget/model/WidgetTypeIds.kt`:
```kotlin
const val WEATHER = "widget_weather"
```

### Step 2: Create the WidgetProvider
Create a provider class implementing `WidgetProvider`:
```kotlin
class WeatherWidgetProvider : WidgetProvider {
    override val definition = WidgetDefinition(
        id = WidgetTypeIds.WEATHER,
        displayName = "Weather Forecast",
        description = "Current conditions and multi-day forecast",
        category = WidgetCategory.PRODUCTIVITY,
        availableDesigns = listOf(
            WidgetDesign(id = "compact_temp", displayName = "Compact Temperature"),
            WidgetDesign(id = "detailed_forecast", displayName = "Detailed Forecast")
        ),
        defaultDesignId = "compact_temp"
    )

    override fun getRenderer(designId: String): WidgetContentRenderer {
        return when (designId) {
            "detailed_forecast" -> WeatherDetailedRenderer()
            else -> WeatherCompactRenderer()
        }
    }
}
```

### Step 3: Implement Visual Renderers
Implement `WidgetContentRenderer` for each visual design:
```kotlin
class WeatherCompactRenderer : WidgetContentRenderer {
    @Composable
    override fun Render(instance: WidgetInstance, design: WidgetDesign, modifier: Modifier) {
        // Compose UI for compact weather
    }
}
```

### Step 4: Register in WidgetRegistry
Register the new provider inside `DefaultAppContainer` (or DI configuration):
```kotlin
widgetRegistry.register(WeatherWidgetProvider())
```

The new widget is now automatically:
- Discoverable in the `WidgetPicker`.
- Persisted and restored across application restarts.
- Renderable across any panel layout and slot stack.

---

## 10. Developer Guide: How to Add a New Panel Layout

Adding a new panel layout follows a standardized 3-step pattern:

1. **Extend `PanelLayout` sealed class**:
   ```kotlin
   @Serializable
   @SerialName("three_split_horizontal")
   data object ThreeSplitHorizontal : PanelLayout(
       id = "three_split_horizontal",
       displayName = "Three Columns",
       slotCount = 3
   )
   ```
2. **Add Layout Arrangement in `PanelLayoutContainer.kt`**:
   ```kotlin
   is PanelLayout.ThreeSplitHorizontal -> {
       Row(modifier = modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(spacing)) {
           slots.take(3).forEach { slot ->
               WidgetSlotView(slot = slot, modifier = Modifier.weight(1f).fillMaxHeight(), ...)
           }
       }
   }
   ```
3. **Register or Configure Panels**:
   Any panel can now assign `PanelLayout.ThreeSplitHorizontal` and host up to 3 independent widget slots with automatic state persistence and stack cycling.
