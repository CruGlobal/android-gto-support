package org.ccci.gto.android.common.okta.oidc.net.response

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.oidc.net.response.UserInfo
import org.ccci.gto.android.common.base.TimeConstants.DAY_IN_MS
import org.ccci.gto.android.common.base.TimeConstants.HOUR_IN_MS
import org.ccci.gto.android.common.base.TimeConstants.WEEK_IN_MS
import org.ccci.gto.android.common.okta.oidc.RETRIEVED_AT
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.both
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.json.JSONObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistableUserInfoTest {
    @Test
    fun verifyNextRefreshTime() {
        assertThat(
            userInfo(null).nextRefreshTime,
            matchesTime(System.currentTimeMillis() + HOUR_IN_MS)
        )
        assertThat(
            userInfo(System.currentTimeMillis()).nextRefreshTime,
            matchesTime(System.currentTimeMillis() + DAY_IN_MS)
        )
    }

    @Test
    fun verifyIsStale() {
        assertFalse(userInfo(System.currentTimeMillis() - HOUR_IN_MS).isStale)
        assertTrue(userInfo(System.currentTimeMillis() - WEEK_IN_MS).isStale)
    }

    private fun userInfo(retrievedAt: Number? = null) =
        PersistableUserInfo("", UserInfo(JSONObject(mapOf(RETRIEVED_AT to retrievedAt))))

    private fun matchesTime(time: Long) =
        both(greaterThanOrEqualTo(time - 1000)).and(lessThanOrEqualTo(time + 1000))
}
