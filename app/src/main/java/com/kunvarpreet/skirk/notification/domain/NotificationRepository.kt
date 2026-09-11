package com.kunvarpreet.skirk.notification.domain

import android.content.Context
import com.kunvarpreet.skirk.notification.model.NotificationAccessState
import com.kunvarpreet.skirk.notification.model.NotificationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Domain repository abstraction for observing Android system notifications.
 */
interface NotificationRepository {
    /**
     * Emits the reactive [NotificationState] including access status and active notifications.
     */
    fun observeNotificationState(): Flow<NotificationState>

    /**
     * Checks whether notification listener access is currently enabled in system settings.
     */
    fun isNotificationAccessGranted(): Boolean

    /**
     * Launches Android system settings to allow the user to grant notification listener access.
     */
    fun openNotificationAccessSettings(context: Context)
}

/**
 * Deterministic mock repository for Compose Previews and unit tests.
 */
class MockNotificationRepository(
    initialState: NotificationState = NotificationState.sample()
) : NotificationRepository {

    private val _state = MutableStateFlow(initialState)

    override fun observeNotificationState(): Flow<NotificationState> = _state.asStateFlow()

    override fun isNotificationAccessGranted(): Boolean =
        _state.value.accessState == NotificationAccessState.GRANTED

    override fun openNotificationAccessSettings(context: Context) {
        // No-op in mock
    }

    fun setState(state: NotificationState) {
        _state.value = state
    }
}
