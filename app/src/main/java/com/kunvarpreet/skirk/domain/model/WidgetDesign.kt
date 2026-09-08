package com.kunvarpreet.skirk.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a distinct visual design / theme for a widget type.
 * A single widget (e.g. Digital Clock) can offer multiple designs (e.g. Minimal, Large, Retro, Modern).
 */
@Serializable
data class WidgetDesign(
    val id: String,
    val displayName: String,
    val description: String = ""
)
