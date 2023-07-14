package org.ccci.gto.android.common.androidx.compose.material3.ui.navigationdrawer

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DrawerStateTest {
    @Test
    fun `toggle() - Drawer currently open`() = runTest {
        val state: DrawerState = mockk {
            every { targetValue } returns DrawerValue.Open
            coEvery { close() } just Runs

            excludeRecords { targetValue }
        }

        state.toggle()
        coVerifyAll { state.close() }
    }

    @Test
    fun `toggle() - Drawer currently closed`() = runTest {
        val state: DrawerState = mockk {
            every { targetValue } returns DrawerValue.Closed
            coEvery { open() } just Runs

            excludeRecords { targetValue }
        }

        state.toggle()
        coVerifyAll { state.open() }
    }
}
