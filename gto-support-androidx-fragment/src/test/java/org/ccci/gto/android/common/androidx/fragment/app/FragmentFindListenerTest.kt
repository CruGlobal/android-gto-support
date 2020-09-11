package org.ccci.gto.android.common.androidx.fragment.app

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
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
        activity = mock()
        friend = mock { on { activity } doReturn activity }
        grandparent = mock { on { activity } doReturn activity }
        parent = mock {
            on { activity } doReturn activity
            on { parentFragment } doReturn grandparent
        }
        child = mock {
            on { activity } doReturn activity
            on { parentFragment } doReturn parent
            on { targetFragment } doReturn friend
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
