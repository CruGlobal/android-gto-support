package org.ccci.gto.android.common.androidx.compose.ui.text

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParagraphTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val textStyle = TextStyle(lineHeight = 14.sp)

    @Test
    fun `computeHeightForDefaultText()`() {
        composeTestRule.setContent {
            assertEquals(0.dp, computeHeightForDefaultText(textStyle, 0))
            assertEquals(35.dp, computeHeightForDefaultText(textStyle, 1))
            assertEquals(49.dp, computeHeightForDefaultText(textStyle, 2))
        }
    }
}
