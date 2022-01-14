package org.ccci.gto.android.common.okta.oidc.net.response

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.okta.oidc.Tokens
import com.okta.oidc.net.response.TokenResponse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TokenResponseTest {
    @Test
    fun verifyRepair() {
        val tokenResponse = Gson().fromJson("{}", TokenResponse::class.java)

        assertThrows(NumberFormatException::class.java) { Tokens(tokenResponse) }
        assertNotNull(Tokens(tokenResponse.repair()))
    }
}
