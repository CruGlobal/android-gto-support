package org.ccci.gto.android.common.androidx.fragment.app

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class FragmentFindListenerTest {
    private lateinit var activity: FragmentActivity
    private lateinit var friend: Friend
    private lateinit var grandparent: Grandparent
    private lateinit var parent: Parent
    private lateinit var child: Child

    @Before
    fun setupMocks() {
        activity = mockk()
        friend = mockk {
            every { activity } returns this@FragmentFindListenerTest.activity
            every { parentFragment } returns null
            every { targetFragment } returns null
        }
        grandparent = mockk {
            every { activity } returns this@FragmentFindListenerTest.activity
            every { parentFragment } returns null
            every { targetFragment } returns null
        }
        parent = mockk {
            every { activity } returns this@FragmentFindListenerTest.activity
            every { parentFragment } returns grandparent
            every { targetFragment } returns null
        }
        child = mockk {
            every { activity } returns this@FragmentFindListenerTest.activity
            every { parentFragment } returns parent
            every { targetFragment } returns friend
        }
    }

    @Test
    fun verifyFindListenerTargetFragment() {
        assertSame(friend, child.findListener<Friend>())
        assertSame(friend, child.findListener<Engineer>())
    }

    @Test
    fun verifyFindListenerParentFragment() {
        assertSame(grandparent, parent.findListener<Grandparent>())
        assertSame(grandparent, child.findListener<Grandparent>())
        assertSame(parent, child.findListener<Parent>())
        assertSame(parent, child.findListener<Ancestor>())
    }

    @Test
    fun verifyFindListenerActivity() {
        assertSame(activity, grandparent.findListener<Activity>())
        assertSame(activity, parent.findListener<Activity>())
        assertSame(activity, child.findListener<Activity>())
        assertSame(activity, friend.findListener<Activity>())
    }

    private interface Ancestor
    private interface Engineer
    private class Grandparent : Fragment(), Ancestor
    private class Parent : Fragment(), Ancestor, Engineer
    private class Child : Fragment()
    private class Friend : Fragment(), Engineer
}
