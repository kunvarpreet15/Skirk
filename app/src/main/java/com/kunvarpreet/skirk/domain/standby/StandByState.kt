package com.kunvarpreet.skirk.domain.standby

/**
 * Explicit runtime states of the StandBy mode.
 */
enum class StandByState {
    /**
     * Normal application state or device not in StandBy mode.
     */
    INACTIVE,

    /**
     * Transitioning into StandBy (charging detected or manual trigger initiated).
     */
    ENTERING,

    /**
     * StandBy dashboard is currently active on screen.
     */
    ACTIVE,

    /**
     * StandBy is terminating (charger disconnected or manually dismissed).
     */
    EXITING
}
