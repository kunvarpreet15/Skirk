package com.kunvarpreet.skirk.widget.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.system.domain.SystemDashboardRepository
import com.kunvarpreet.skirk.system.model.SystemDashboardData
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer

/**
 * Production renderer for the System Dashboard widget.
 */
class SystemDashboardWidgetRenderer(
    private val systemDashboardRepository: SystemDashboardRepository
) : WidgetContentRenderer {

    @Composable
    override fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    ) {
        val config = SystemDashboardConfig.fromWidgetConfig(instance.config)
        val data by systemDashboardRepository.observeSystemDashboard()
            .collectAsState(initial = SystemDashboardData())

        when (design.id) {
            SystemDashboardConfig.DESIGN_RINGS -> {
                RingsSystemDashboardDesign(
                    data = data,
                    config = config,
                    modifier = modifier
                )
            }
            SystemDashboardConfig.DESIGN_MINIMAL -> {
                MinimalSystemDashboardDesign(
                    data = data,
                    config = config,
                    modifier = modifier
                )
            }
            else -> {
                GridSystemDashboardDesign(
                    data = data,
                    config = config,
                    modifier = modifier
                )
            }
        }
    }
}
