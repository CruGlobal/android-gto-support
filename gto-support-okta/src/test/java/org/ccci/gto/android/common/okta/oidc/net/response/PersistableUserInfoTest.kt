package org.ccci.gto.android.common.okta.oidc.net.response

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.oidc.net.response.UserInfo
import org.ccci.gto.android.common.base.TimeConstants.DAY_IN_MS
import org.ccci.gto.android.common.base.TimeConstants.HOUR_IN_MS
import org.ccci.gto.android.common.base.TimeConstants.WEEK_IN_MS
import org.hamcrest.CoreMatchers.both
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistableUserInfoTest {
    @Test
    fun verifyNextRefreshDelay() {
        val userInfo = PersistableUserInfo("", UserInfo(null), System.currentTimeMillis())
        assertEquals(DAY_IN_MS, userInfo.nextRefreshDelay, 1000)
    }

    @Test
    fun verifyIsStale() {
        assertFalse(userInfo(System.currentTimeMillis() - HOUR_IN_MS).isStale)
        assertTrue(userInfo(System.currentTimeMillis() - WEEK_IN_MS).isStale)
    }

    private fun userInfo(retrievedAt: Long) = PersistableUserInfo("", UserInfo(null), retrievedAt)

    private fun assertEquals(expected: Long, actual: Long, delta: Long) {
        assertTrue(actual >= expected - delta)
        assertTrue(actual <= expected + delta)
    }

    private fun matchesTime(time: Long, delta: Long) =
        both(greaterThanOrEqualTo(time - delta)).and(lessThanOrEqualTo(time + delta))
}
