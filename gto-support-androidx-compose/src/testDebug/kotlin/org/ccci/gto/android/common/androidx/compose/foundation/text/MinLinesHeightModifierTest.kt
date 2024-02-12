package org.ccci.gto.android.common.androidx.compose.foundation.text

import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MinLinesHeightModifierTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val textStyle = TextStyle(lineHeight = 14.sp)

    @Test
    fun `minLinesHeight() - 0 lines`() {
        composeTestRule.setContent {
            Spacer(modifier = Modifier.minLinesHeight(0, textStyle))
        }
        composeTestRule.onRoot().assertHeightIsEqualTo(0.dp)
    }

    @Test
    fun `minLinesHeight() - 1 line`() {
        composeTestRule.setContent {
            Spacer(modifier = Modifier.minLinesHeight(1, textStyle))
        }
        composeTestRule.onRoot().assertHeightIsEqualTo(35.dp)
    }

    @Test
    fun `minLinesHeight() - 2 lines`() {
        composeTestRule.setContent {
            Spacer(modifier = Modifier.minLinesHeight(2, textStyle))
        }
        composeTestRule.onRoot().assertHeightIsEqualTo(49.dp)
    }
}
