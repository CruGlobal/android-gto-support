package org.ccci.gto.android.common.picasso.view

import android.net.Uri
import android.widget.ImageView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.squareup.picasso.Picasso
import java.io.File
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class PicassoImageViewTest {
    private lateinit var imageView: ImageView
    private lateinit var picasso: Picasso

    private lateinit var helper: PicassoImageView.Helper

    @Before
    fun setup() {
        imageView = mock { on { context } doReturn ApplicationProvider.getApplicationContext() }
        picasso = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
        helper = PicassoImageView.Helper(imageView, picasso = picasso)
    }

    @Test
    fun testPicassoFile() {
        val file = mock<File>()
        helper.setPicassoUri(mock())
        clearInvocations(picasso)

        // changing the file should trigger a single update
        helper.setPicassoFile(file)
        verify(picasso).load(file)
        verifyNoMoreInteractions(picasso)

        // setting the same uri a second time shouldn't trigger a new update
        helper.setPicassoFile(file)
        verifyZeroInteractions(picasso)
    }

    @Test
    fun testPicassoUri() {
        val uri = mock<Uri>()
        helper.setPicassoFile(mock())
        clearInvocations(picasso)

        // changing the uri should trigger a single update
        helper.setPicassoUri(uri)
        verify(picasso).load(uri)
        verifyNoMoreInteractions(picasso)

        // setting the same uri a second time shouldn't trigger a new update
        helper.setPicassoUri(uri)
        verifyZeroInteractions(picasso)
    }
}
