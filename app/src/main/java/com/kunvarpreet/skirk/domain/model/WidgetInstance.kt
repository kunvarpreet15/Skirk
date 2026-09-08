package com.kunvarpreet.skirk.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * An instantiated widget assigned to a slot.
 * Multiple instances of the same widget type can exist across different panels/slots,
 * each with their own unique ID, selected visual design, and configuration parameters.
 */
@Serializable
data class WidgetInstance(
    val id: String = UUID.randomUUID().toString(),
    val widgetTypeId: String,
    val selectedDesignId: String,
    val config: WidgetConfig = WidgetConfig()
)
