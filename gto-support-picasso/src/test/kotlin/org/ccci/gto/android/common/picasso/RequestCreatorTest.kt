package org.ccci.gto.android.common.picasso

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.StubRequestCreator
import com.squareup.picasso.Target
import java.lang.ref.Reference
import java.lang.ref.WeakReference
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class RequestCreatorTest {
    private lateinit var mainThread: HandlerThread
    private lateinit var bitmap: Bitmap
    private lateinit var request: RequestCreator

    @BeforeTest
    fun setup() {
        mainThread = HandlerThread("").apply { start() }
        Dispatchers.setMain(Handler(mainThread.looper).asCoroutineDispatcher())

        bitmap = mock()
        request = mock()
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
        mainThread.quit()
        mainThread.join()
    }

    @Test
    fun testGetBitmap() = runTest {
        whenever(request.into(any<Target>()))
            .thenAnswer { it.getArgument<Target>(0).onBitmapLoaded(bitmap, Picasso.LoadedFrom.DISK) }

        assertSame(bitmap, request.getBitmap())
    }

    @Test(expected = ExpectedException::class)
    fun testGetBitmapError() = runTest {
        whenever(request.into(any<Target>()))
            .thenAnswer { it.getArgument<Target>(0).onBitmapFailed(ExpectedException(), null) }

        request.getBitmap()
    }

    @Test
    fun testGetBitmapMainThreadUsage() = runTest {
        whenever(request.into(any<Target>())).thenAnswer {
            assertSame(mainThread, Thread.currentThread(), "Not executing on the Main Thread")
            it.getArgument<Target>(0).onBitmapLoaded(bitmap, Picasso.LoadedFrom.DISK)
        }

        assertNotSame(mainThread, Thread.currentThread())
        request.getBitmap()
    }

    @Test(timeout = 5000)
    fun `getBitmap() should protect it's target from garbage collection`() = runTest {
        val request = TargetCapturingRequestCreator()
        val task = launch { request.getBitmap() }
        val ref = request.targets.receive()
        System.gc()
        val target = ref.get()
        if (target == null) task.cancel()
        assertNotNull(target)
        target!!.onBitmapLoaded(bitmap, Picasso.LoadedFrom.DISK)
    }

    @Test
    fun `getBitmap() should cancel Picasso request on task cancellation`() = runTest {
        val picasso = mock<Picasso>()
        val request = TargetCapturingRequestCreator(picasso)

        val task = launch { request.getBitmap() }
        val target = request.targets.receive().get()!!
        task.cancelAndJoin()
        verify(picasso).cancelRequest(target)
    }

    @Test
    fun `getBitmap() should cancel Picasso request on main thread`() = runTest {
        var cancelRequestThread: Thread? = null
        val picasso = mock<Picasso> {
            // we capture the thread that cancelRequest is called on, that way we can assert later that it was the
            // "Main" thread.
            //
            // We can't directly throw an assertion because GlobalScope.launch() will swallow the exception and process
            // it with an UncaughtExceptionHandler not on the call stack for this test. The exception will print to the
            // console, but the test will still report as passing
            on { cancelRequest(any<Target>()) } doAnswer { cancelRequestThread = Thread.currentThread() }
        }

        val request = TargetCapturingRequestCreator(picasso)
        val task = launch(UnconfinedTestDispatcher()) { request.getBitmap() }
        val target = request.targets.receive().get()!!
        task.cancelAndJoin()
        assertSame(mainThread, cancelRequestThread)
        verify(picasso).cancelRequest(target)
        verifyNoMoreInteractions(picasso)
    }

    private class TargetCapturingRequestCreator(picasso: Picasso? = null) : StubRequestCreator(picasso) {
        val targets = Channel<Reference<Target>>(1)

        override fun into(target: Target) {
            // into() stores a weak reference of the target
            assertTrue(targets.trySend(WeakReference(target)).isSuccess)
        }
    }

    private class ExpectedException : Exception()
}
