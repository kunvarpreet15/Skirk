package com.kunvarpreet.skirk.domain.model

/**
 * Functional category for organizing widgets in pickers and discovery views.
 */
enum class WidgetCategory(val displayName: String) {
    TIME("Clocks & Time"),
    MEDIA("Media & Audio"),
    SYSTEM("System & Hardware"),
    PRODUCTIVITY("Productivity & Events"),
    ENTERTAINMENT("Entertainment & Quotes"),
    SHORTCUTS("Quick Shortcuts")
}

/**
 * Metadata definition for a registered widget type.
 * Declares the capabilities, supported layouts, and available visual designs
 * without coupling the core dashboard to any specific widget implementation.
 */
data class WidgetDefinition(
    val id: String,
    val displayName: String,
    val description: String,
    val category: WidgetCategory,
    val availableDesigns: List<WidgetDesign>,
    val defaultDesignId: String,
    val isInteractive: Boolean = false
) {
    fun findDesign(designId: String): WidgetDesign {
        return availableDesigns.find { it.id == designId }
            ?: availableDesigns.firstOrNull()
            ?: WidgetDesign(id = "default", displayName = "Default")
    }
}
