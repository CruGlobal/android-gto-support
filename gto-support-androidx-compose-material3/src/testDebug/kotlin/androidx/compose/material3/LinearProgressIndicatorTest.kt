package androidx.compose.material3

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.Test
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = Application::class)
class LinearProgressIndicatorTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // https://issuetracker.google.com/issues/322214617
    @Test
    fun `issuetracker - 322214617`() {
        composeTestRule.setContent { LinearProgressIndicator() }
    }
}
