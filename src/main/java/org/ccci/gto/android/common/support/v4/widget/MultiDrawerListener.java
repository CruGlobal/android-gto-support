package org.ccci.gto.android.common.support.v4.widget;

import android.support.v4.widget.DrawerLayout;
import android.view.View;

public final class MultiDrawerListener implements DrawerLayout.DrawerListener {
    private final DrawerLayout.DrawerListener[] listeners;

    public MultiDrawerListener(final DrawerLayout.DrawerListener... listeners) {
        this.listeners = listeners != null ? listeners : new DrawerLayout.DrawerListener[0];
    }

    @Override
    public void onDrawerSlide(final View drawerView, final float slideOffset) {
        for (final DrawerLayout.DrawerListener listener : this.listeners) {
            listener.onDrawerSlide(drawerView, slideOffset);
        }
    }

    @Override
    public void onDrawerOpened(final View drawerView) {
        for (final DrawerLayout.DrawerListener listener : this.listeners) {
            listener.onDrawerOpened(drawerView);
        }
    }

    @Override
    public void onDrawerClosed(final View drawerView) {
        for (final DrawerLayout.DrawerListener listener : this.listeners) {
            listener.onDrawerClosed(drawerView);
        }
    }

    @Override
    public void onDrawerStateChanged(final int newState) {
        for (final DrawerLayout.DrawerListener listener : this.listeners) {
            listener.onDrawerStateChanged(newState);
        }
    }
}
