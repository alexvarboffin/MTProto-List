package com.walhalla.mtprotocompose.update

import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InAppUpdateDecisionTest {

    private val available = UpdateAvailability.UPDATE_AVAILABLE

    @Test
    fun noUpdate_returnsNull() {
        assertNull(chooseUpdateType(UpdateAvailability.UPDATE_NOT_AVAILABLE, 5, 30, true, true))
        assertNull(chooseUpdateType(UpdateAvailability.UNKNOWN, 5, 30, true, true))
    }

    @Test
    fun highPriority_immediate() {
        assertEquals(AppUpdateType.IMMEDIATE, chooseUpdateType(available, 4, 0, true, true))
        assertEquals(AppUpdateType.IMMEDIATE, chooseUpdateType(available, 5, null, true, false))
    }

    @Test
    fun highPriority_butImmediateNotAllowed_fallsBackToFlexibleWhenStale() {
        assertEquals(AppUpdateType.FLEXIBLE, chooseUpdateType(available, 5, 10, false, true))
        assertNull(chooseUpdateType(available, 5, 1, false, true))
    }

    @Test
    fun stale_flexible() {
        assertEquals(AppUpdateType.FLEXIBLE, chooseUpdateType(available, 0, DAYS_FOR_FLEXIBLE_UPDATE, false, true))
    }

    @Test
    fun fresh_or_unknownStaleness_doesNothing() {
        assertNull(chooseUpdateType(available, 0, DAYS_FOR_FLEXIBLE_UPDATE - 1, true, true))
        assertNull(chooseUpdateType(available, 3, null, true, true))
    }

    @Test
    fun flexibleNotAllowed_doesNothing() {
        assertNull(chooseUpdateType(available, 0, 30, true, false))
    }
}