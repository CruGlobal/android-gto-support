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
import java.util.concurrent.Executors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
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
    private lateinit var request: RequestCreator
    private lateinit var dispatcher: ExecutorCoroutineDispatcher
    private lateinit var testDispatcher: TestCoroutineDispatcher

    private lateinit var bitmap: Bitmap

    @Before
    fun setup() {
        request = mock()
        dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        testDispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(testDispatcher)

        bitmap = mock()
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testGetBitmap() {
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

    @Test(timeout = 5000)
    fun `getBitmap() should protect it's target from garbage collection`() {
        val request = TargetCapturingRequestCreator()
        runBlocking {
            val task = launch(Dispatchers.Default) { request.getBitmap() }
            val ref = request.targets.receive()
            System.gc()
            val target = ref.get()
            if (target == null) task.cancel()
            assertNotNull(target)
            target!!.onBitmapLoaded(bitmap, Picasso.LoadedFrom.DISK)
        }
    }

    @Test
    fun `getBitmap() should cancel Picasso request on task cancellation`() {
        val picasso = mock<Picasso>()
        val request = TargetCapturingRequestCreator(picasso)
        val target = runBlocking {
            val task = launch(Dispatchers.Default) { request.getBitmap() }
            val target = request.targets.receive().get()!!
            task.cancel()
            return@runBlocking target
        }
        verify(picasso).cancelRequest(target)
    }

    @Test
    fun `getBitmap() should cancel Picasso request on main thread`() {
        val thread = HandlerThread("").apply { start() }
        assertTrue(thread.isAlive)
        Dispatchers.setMain(Handler(thread.looper).asCoroutineDispatcher())
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
        val target = runBlocking {
            val task = launch(TestCoroutineDispatcher()) { request.getBitmap() }
            val target = request.targets.receive().get()!!
            task.cancel()
            return@runBlocking target
        }
        thread.quitSafely()
        thread.join()
        assertEquals(thread, cancelRequestThread)
        verify(picasso).cancelRequest(target)
        verifyNoMoreInteractions(picasso)
    }

    private class TargetCapturingRequestCreator(picasso: Picasso? = null) : StubRequestCreator(picasso) {
        val targets = Channel<Reference<Target>>(1)

        override fun into(target: Target) {
            // into() stores a weak reference of the target
            assertTrue(targets.offer(WeakReference(target)))
        }
    }

    private class ExpectedException : Exception()
}
