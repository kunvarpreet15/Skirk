package com.kunvarpreet.skirk.widget.system

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.system.domain.MockSystemDashboardRepository
import com.kunvarpreet.skirk.system.domain.SystemDashboardRepository
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Production provider for the built-in System Dashboard widget.
 */
class SystemDashboardWidgetProvider(
    private val systemDashboardRepository: SystemDashboardRepository = MockSystemDashboardRepository()
) : WidgetProvider {

    private val renderer = SystemDashboardWidgetRenderer(systemDashboardRepository)

    override val definition: WidgetDefinition = WidgetDefinition(
        id = WidgetTypeIds.SYSTEM_DASHBOARD,
        displayName = "System Dashboard",
        description = "RAM, storage, network, and battery statistics",
        category = WidgetCategory.SYSTEM,
        availableDesigns = listOf(
            WidgetDesign(
                id = SystemDashboardConfig.DESIGN_GRID,
                displayName = "Grid Gauges"
            ),
            WidgetDesign(
                id = SystemDashboardConfig.DESIGN_RINGS,
                displayName = "Progress Rings"
            ),
            WidgetDesign(
                id = SystemDashboardConfig.DESIGN_MINIMAL,
                displayName = "Minimal Matrix"
            )
        ),
        defaultDesignId = SystemDashboardConfig.DESIGN_GRID
    )

    override fun getRenderer(designId: String): WidgetContentRenderer = renderer
}
