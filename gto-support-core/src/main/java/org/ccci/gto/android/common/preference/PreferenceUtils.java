package org.ccci.gto.android.common.preference;

import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.support.annotation.Nullable;

public class PreferenceUtils {
    @Nullable
    public static PreferenceGroup findParent(@Nullable final PreferenceGroup root,
                                             @Nullable final Preference preference) {
        // short-circuit if we don't have a valid root or preference
        if (root == null || preference == null) {
            return null;
        }

        // do a DFS for this preference
        for (int i = 0; i < root.getPreferenceCount(); i++) {
            // is this the preference we are looking for
            final Preference p = root.getPreference(i);
            if (p == preference) {
                return root;
            }

            // recurse if this is a PreferenceGroup
            if (p instanceof PreferenceGroup) {
                final PreferenceGroup parent = findParent((PreferenceGroup) p, preference);
                if (parent != null) {
                    return parent;
                }
            }
        }

        // the preference isn't in roots' hierarchy
        return null;
    }
}
