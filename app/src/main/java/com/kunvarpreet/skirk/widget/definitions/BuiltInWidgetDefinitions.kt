package com.kunvarpreet.skirk.widget.definitions

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.core.WidgetRegistry
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

import com.kunvarpreet.skirk.media.domain.MediaSessionRepository
import com.kunvarpreet.skirk.widget.analogclock.AnalogClockProvider
import com.kunvarpreet.skirk.widget.battery.BatteryInfoProvider
import com.kunvarpreet.skirk.widget.battery.BatteryWidgetProvider
import com.kunvarpreet.skirk.widget.digitalclock.DigitalClockProvider
import com.kunvarpreet.skirk.widget.mediaplayer.MediaPlayerProvider

import com.kunvarpreet.skirk.calendar.domain.CalendarRepository
import com.kunvarpreet.skirk.widget.calendar.CalendarWidgetProvider
import com.kunvarpreet.skirk.widget.schedule.ScheduleWidgetProvider

import com.kunvarpreet.skirk.notification.domain.NotificationRepository
import com.kunvarpreet.skirk.widget.notification.NotificationWidgetProvider
import com.kunvarpreet.skirk.system.domain.SystemDashboardRepository
import com.kunvarpreet.skirk.widget.system.SystemDashboardWidgetProvider

/**
 * Declares the definitions and design variations for built-in widgets.
 * Digital Clock, Analog Clock, Battery, Media Player, Calendar, Schedule,
 * Notifications, and System Dashboard use real production implementations,
 * while other widgets delegate to placeholder renderers until subsequent phases.
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

    val digitalClock: WidgetProvider = DigitalClockProvider()

    val analogClock: WidgetProvider = AnalogClockProvider()

    val mediaPlayer: WidgetProvider = MediaPlayerProvider()

    val battery: WidgetProvider = BatteryWidgetProvider()

    val calendar: WidgetProvider = CalendarWidgetProvider()

    val schedule: WidgetProvider = ScheduleWidgetProvider()

    val notifications: WidgetProvider = NotificationWidgetProvider()

    val systemDashboard: WidgetProvider = SystemDashboardWidgetProvider()

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
        notifications,
        systemDashboard,
        countdown,
        stopwatch,
        quotes,
        photoSlideshow,
        memePlayer,
        quickShortcuts
    )

    fun createBuiltInProviders(
        batteryInfoProvider: BatteryInfoProvider? = null,
        mediaSessionRepository: MediaSessionRepository? = null,
        calendarRepository: CalendarRepository? = null,
        notificationRepository: NotificationRepository? = null,
        systemDashboardRepository: SystemDashboardRepository? = null
    ): List<WidgetProvider> {
        val activeBattery = if (batteryInfoProvider != null) {
            BatteryWidgetProvider(batteryInfoProvider)
        } else {
            battery
        }
        val activeMedia = if (mediaSessionRepository != null) {
            MediaPlayerProvider(mediaSessionRepository)
        } else {
            mediaPlayer
        }
        val activeCalendar = if (calendarRepository != null) {
            CalendarWidgetProvider(calendarRepository)
        } else {
            calendar
        }
        val activeSchedule = if (calendarRepository != null) {
            ScheduleWidgetProvider(calendarRepository)
        } else {
            schedule
        }
        val activeNotifications = if (notificationRepository != null) {
            NotificationWidgetProvider(notificationRepository)
        } else {
            notifications
        }
        val activeSystemDashboard = if (systemDashboardRepository != null) {
            SystemDashboardWidgetProvider(systemDashboardRepository)
        } else {
            systemDashboard
        }

        return listOf(
            digitalClock,
            analogClock,
            activeMedia,
            activeBattery,
            activeCalendar,
            activeSchedule,
            activeNotifications,
            activeSystemDashboard,
            countdown,
            stopwatch,
            quotes,
            photoSlideshow,
            memePlayer,
            quickShortcuts
        )
    }
}

