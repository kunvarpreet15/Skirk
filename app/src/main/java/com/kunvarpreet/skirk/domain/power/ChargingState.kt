package com.kunvarpreet.skirk.domain.power

/**
 * Distinguishes the power source supplying current to the device.
 */
enum class ChargingSource(val displayName: String) {
    NONE("Not Charging"),
    AC("AC Wall Charger"),
    USB("USB Port"),
    WIRELESS("Wireless Charger"),
    DOCK("Dock"),
    OTHER("Other Power Source")
}

/**
 * Represents the current power and charging state of the device.
 */
data class ChargingState(
    val isCharging: Boolean,
    val source: ChargingSource
) {
    companion object {
        val Disconnected = ChargingState(
            isCharging = false,
            source = ChargingSource.NONE
        )
    }
}
