package org.ccci.gto.android.common.androidx.compose.ui.text.res

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.ccci.gto.android.common.androidx.compose.test.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnnotatedStringResourcesTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `annotatedStringResource() - String`() {
        composeTestRule.setContent {
            val string = annotatedStringResource(R.string.annotated_string_format, "middle")
            assertEquals("start middle end", string.toString())
        }
    }

    @Test
    fun `annotatedStringResource() - AnnotatedString`() {
        composeTestRule.setContent {
            val style = SpanStyle(color = Color.Cyan)
            val arg = buildAnnotatedString {
                withStyle(style) { append("middle") }
            }
            val string = annotatedStringResource(R.string.annotated_string_format, arg)
            assertEquals("start middle end", string.toString())

            string.spanStyles.single().let {
                assertEquals(6, it.start)
                assertEquals(style, it.item)
                assertEquals(12, it.end)
            }
        }
    }
}
