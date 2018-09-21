package org.ccci.gto.android.common.util.view;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

public class MenuUtils {
    public static void setGroupVisibleRecursively(@Nullable final Menu menu, @IdRes final int group,
                                                  final boolean visible) {
        // short-circuit if there isn't a menu to process
        if (menu == null) {
            return;
        }

        // set direct items visible
        menu.setGroupVisible(group, visible);

        // cascade group visibility into submenus
        final int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                setGroupVisibleRecursively(item.getSubMenu(), group, visible);
            }
        }
    }
}
