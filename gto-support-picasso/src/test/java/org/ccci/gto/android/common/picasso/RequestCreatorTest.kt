package org.ccci.gto.android.common.picasso

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import java.util.concurrent.Executors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class RequestCreatorTest {
    private lateinit var request: RequestCreator
    private lateinit var dispatcher: ExecutorCoroutineDispatcher
    private lateinit var testDispatcher: TestCoroutineDispatcher

    @Before
    fun setup() {
        request = mock()
        dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        testDispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testGetBitmap() {
        val bitmap = mock<Bitmap>()
        whenever(request.into(any<Target>()))
            .thenAnswer { it.getArgument<Target>(0).onBitmapLoaded(bitmap, Picasso.LoadedFrom.DISK) }

        assertSame(bitmap, runBlocking { request.getBitmap() })
    }

    @Test(expected = ExpectedException::class)
    fun testGetBitmapError() {
        whenever(request.into(any<Target>()))
            .thenAnswer { it.getArgument<Target>(0).onBitmapFailed(ExpectedException(), null) }

        runBlocking { request.getBitmap() }
    }

    @Test
    fun testGetBitmapMainThreadUsage() {
        val thread = HandlerThread("").apply { start() }
        Dispatchers.setMain(Handler(thread.looper).asCoroutineDispatcher())
        val bitmap = mock<Bitmap>()
        whenever(request.into(any<Target>())).thenAnswer {
            check(thread === Thread.currentThread()) { "Not executing on the Main Thread" }
            it.getArgument<Target>(0).onBitmapLoaded(bitmap, Picasso.LoadedFrom.DISK)
        }

        runBlocking {
            launch(Dispatchers.Default) { request.getBitmap() }
            request.getBitmap()
        }
        thread.quit()
    }

    private class ExpectedException : Exception()
}
