package org.ccci.gto.android.common.testing.picasso

import com.squareup.picasso.Picasso
import com.squareup.picasso.picassoSingleton
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.kotlin.mock

class PicassoSingletonRule : TestWatcher() {
    val mock = mock<Picasso>(defaultAnswer = RETURNS_DEEP_STUBS)

    override fun starting(description: Description?) {
        super.starting(description)
        picassoSingleton = mock
    }

    override fun finished(description: Description?) {
        super.finished(description)
        picassoSingleton = null
    }
}
