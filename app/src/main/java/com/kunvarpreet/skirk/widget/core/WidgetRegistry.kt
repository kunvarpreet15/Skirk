package com.kunvarpreet.skirk.widget.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * Fallback renderer used during Phase 2 to render placeholder widgets with clean,
 * responsive geometry and visual design tags.
 */
internal class PlaceholderWidgetRenderer(private val widgetTypeId: String) : WidgetContentRenderer {
    @Composable
    override fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    ) {
        val formattedTitle = widgetTypeId
            .removePrefix("widget_")
            .replace('_', ' ')
            .uppercase()

        val gradientColors = when {
            widgetTypeId.contains("clock") || widgetTypeId.contains("time") -> listOf(
                Color(0xFF1E293B),
                Color(0xFF0F172A)
            )
            widgetTypeId.contains("battery") -> listOf(
                Color(0xFF064E3B),
                Color(0xFF022C22)
            )
            widgetTypeId.contains("media") -> listOf(
                Color(0xFF581C87),
                Color(0xFF3B0764)
            )
            widgetTypeId.contains("calendar") || widgetTypeId.contains("schedule") -> listOf(
                Color(0xFF1E3A8A),
                Color(0xFF172554)
            )
            else -> listOf(
                Color(0xFF1F2937),
                Color(0xFF111827)
            )
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(gradientColors))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Category indicator dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38BDF8))
                    )
                    Text(
                        text = "WIDGET",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formattedTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = Color(0xFFF8FAFC),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Design Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = design.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
