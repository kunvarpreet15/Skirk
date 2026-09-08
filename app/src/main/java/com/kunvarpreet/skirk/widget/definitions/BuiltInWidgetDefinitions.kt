package com.kunvarpreet.skirk.widget.definitions

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.core.WidgetRegistry
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Declares the definitions and design variations for built-in widgets.
 * In Phase 0, each provider delegates rendering to the placeholder renderer,
 * preparing the registry for real Phase 1+ implementations.
 */
object BuiltInWidgetDefinitions {

    private fun createPlaceholderProvider(definition: WidgetDefinition): WidgetProvider {
        return object : WidgetProvider {
            override val definition: WidgetDefinition = definition
            override fun getRenderer(designId: String): WidgetContentRenderer {
                return WidgetRegistry.defaultPlaceholderRenderer(definition.id)
            }
        }
    }

    val digitalClock = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.DIGITAL_CLOCK,
            displayName = "Digital Clock",
            description = "Displays the current time digitally with customizable styles",
            category = WidgetCategory.TIME,
            availableDesigns = listOf(
                WidgetDesign(id = "minimal", displayName = "Minimal"),
                WidgetDesign(id = "large", displayName = "Large Bold"),
                WidgetDesign(id = "retro", displayName = "Retro Flip"),
                WidgetDesign(id = "modern", displayName = "Modern Neon")
            ),
            defaultDesignId = "large"
        )
    )

    val analogClock = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.ANALOG_CLOCK,
            displayName = "Analog Clock",
            description = "Classic dial clock with hour, minute, and second hands",
            category = WidgetCategory.TIME,
            availableDesigns = listOf(
                WidgetDesign(id = "classic", displayName = "Classic"),
                WidgetDesign(id = "bauhaus", displayName = "Bauhaus"),
                WidgetDesign(id = "chronograph", displayName = "Chronograph")
            ),
            defaultDesignId = "classic"
        )
    )

    val mediaPlayer = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.MEDIA_PLAYER,
            displayName = "Media Player",
            description = "Now playing metadata, album art, and playback controls",
            category = WidgetCategory.MEDIA,
            availableDesigns = listOf(
                WidgetDesign(id = "compact", displayName = "Compact"),
                WidgetDesign(id = "full_art", displayName = "Full Album Art")
            ),
            defaultDesignId = "compact"
        )
    )

    val battery = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.BATTERY,
            displayName = "Battery Info",
            description = "Charging status, percentage, and estimated time to full",
            category = WidgetCategory.SYSTEM,
            availableDesigns = listOf(
                WidgetDesign(id = "ring", displayName = "Circular Ring"),
                WidgetDesign(id = "bar", displayName = "Horizontal Bar")
            ),
            defaultDesignId = "ring"
        )
    )

    val calendar = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.CALENDAR,
            displayName = "Calendar",
            description = "Month glance and upcoming schedule markers",
            category = WidgetCategory.PRODUCTIVITY,
            availableDesigns = listOf(
                WidgetDesign(id = "month_view", displayName = "Month Grid"),
                WidgetDesign(id = "day_large", displayName = "Day of Month")
            ),
            defaultDesignId = "month_view"
        )
    )

    val schedule = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.SCHEDULE,
            displayName = "Schedule & Agenda",
            description = "Upcoming calendar events and timeline",
            category = WidgetCategory.PRODUCTIVITY,
            availableDesigns = listOf(
                WidgetDesign(id = "timeline", displayName = "Timeline"),
                WidgetDesign(id = "agenda_list", displayName = "Agenda List")
            ),
            defaultDesignId = "timeline"
        )
    )

    val countdown = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.COUNTDOWN,
            displayName = "Countdown",
            description = "Countdown timer to important dates and events",
            category = WidgetCategory.TIME,
            availableDesigns = listOf(
                WidgetDesign(id = "days_remaining", displayName = "Days Remaining"),
                WidgetDesign(id = "detailed", displayName = "Days, Hours & Minutes")
            ),
            defaultDesignId = "days_remaining"
        )
    )

    val stopwatch = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.STOPWATCH,
            displayName = "Stopwatch",
            description = "Precise stopwatch with lap timing",
            category = WidgetCategory.TIME,
            availableDesigns = listOf(
                WidgetDesign(id = "digital_split", displayName = "Digital Splits"),
                WidgetDesign(id = "analog_needle", displayName = "Dial Needle")
            ),
            defaultDesignId = "digital_split"
        )
    )

    val notifications = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.NOTIFICATIONS,
            displayName = "Notification Glance",
            description = "Recent notifications and unread badge counts",
            category = WidgetCategory.SYSTEM,
            availableDesigns = listOf(
                WidgetDesign(id = "badges", displayName = "App Badges"),
                WidgetDesign(id = "latest_card", displayName = "Latest Message")
            ),
            defaultDesignId = "badges"
        )
    )

    val quotes = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.QUOTES,
            displayName = "Daily Quotes",
            description = "Inspirational quotes and daily wisdom",
            category = WidgetCategory.ENTERTAINMENT,
            availableDesigns = listOf(
                WidgetDesign(id = "typographic", displayName = "Typographic"),
                WidgetDesign(id = "minimal_italic", displayName = "Minimal Italic")
            ),
            defaultDesignId = "typographic"
        )
    )

    val photoSlideshow = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.PHOTO_SLIDESHOW,
            displayName = "Photo Frame",
            description = "Slideshow of selected photos and albums",
            category = WidgetCategory.ENTERTAINMENT,
            availableDesigns = listOf(
                WidgetDesign(id = "full_bleed", displayName = "Full Bleed"),
                WidgetDesign(id = "framed", displayName = "Framed")
            ),
            defaultDesignId = "full_bleed"
        )
    )

    val memePlayer = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.MEME_PLAYER,
            displayName = "Meme of the Day",
            description = "Fresh curated memes while in StandBy",
            category = WidgetCategory.ENTERTAINMENT,
            availableDesigns = listOf(
                WidgetDesign(id = "card", displayName = "Meme Card")
            ),
            defaultDesignId = "card"
        )
    )

    val systemDashboard = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.SYSTEM_DASHBOARD,
            displayName = "System Dashboard",
            description = "RAM, CPU, storage, and network statistics",
            category = WidgetCategory.SYSTEM,
            availableDesigns = listOf(
                WidgetDesign(id = "gauges", displayName = "Gauges"),
                WidgetDesign(id = "matrix", displayName = "Metric Matrix")
            ),
            defaultDesignId = "gauges"
        )
    )

    val quickShortcuts = createPlaceholderProvider(
        WidgetDefinition(
            id = WidgetTypeIds.QUICK_SHORTCUTS,
            displayName = "Quick Shortcuts",
            description = "Fast tap shortcuts to favorite apps",
            category = WidgetCategory.SHORTCUTS,
            availableDesigns = listOf(
                WidgetDesign(id = "icon_grid", displayName = "App Icon Grid"),
                WidgetDesign(id = "action_buttons", displayName = "Quick Action Buttons")
            ),
            defaultDesignId = "icon_grid"
        )
    )

    val allBuiltInProviders: List<WidgetProvider> = listOf(
        digitalClock,
        analogClock,
        mediaPlayer,
        battery,
        calendar,
        schedule,
        countdown,
        stopwatch,
        notifications,
        quotes,
        photoSlideshow,
        memePlayer,
        systemDashboard,
        quickShortcuts
    )
}
