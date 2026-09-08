package com.kunvarpreet.skirk.widget.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import java.util.concurrent.ConcurrentHashMap

/**
 * Central registry for all available widget providers.
 * Decouples the dashboard presentation from individual widget implementations.
 */
class WidgetRegistry {
    private val providers = ConcurrentHashMap<String, WidgetProvider>()

    fun register(provider: WidgetProvider) {
        providers[provider.definition.id] = provider
    }

    fun registerAll(vararg providerList: WidgetProvider) {
        providerList.forEach { register(it) }
    }

    fun getProvider(widgetTypeId: String): WidgetProvider? = providers[widgetTypeId]

    fun getDefinition(widgetTypeId: String): WidgetDefinition? = providers[widgetTypeId]?.definition

    fun getAllDefinitions(): List<WidgetDefinition> =
        providers.values.map { it.definition }.sortedBy { it.displayName }

    fun getRenderer(widgetTypeId: String, designId: String): WidgetContentRenderer {
        val provider = providers[widgetTypeId]
        return provider?.getRenderer(designId) ?: PlaceholderWidgetRenderer(widgetTypeId)
    }

    companion object {
        fun defaultPlaceholderRenderer(widgetTypeId: String): WidgetContentRenderer =
            PlaceholderWidgetRenderer(widgetTypeId)
    }
}

/**
 * Fallback renderer used during Phase 0 or when a requested widget provider/design
 * has not yet been implemented or loaded.
 */
internal class PlaceholderWidgetRenderer(private val widgetTypeId: String) : WidgetContentRenderer {
    @Composable
    override fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = widgetTypeId.replace('_', ' ').uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Design: ${design.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
