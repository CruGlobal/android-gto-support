package org.ccci.gto.android.common.util;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesUtils {
    public static SharedPreferences.Editor putStringSet(@NonNull final SharedPreferences.Editor prefs,
                                                        @NonNull final String key, @Nullable final Set<String> values) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            prefs.putStringSet(key, values);
        } else {
            // work around missing putStringSet in pre-Honeycomb
            prefs.putString(key, values != null ? new JSONArray(values).toString() : null);
        }

        return prefs;
    }

    public static Set<String> getStringSet(@NonNull final SharedPreferences prefs, @NonNull final String key,
                                           @Nullable final Set<String> defValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return prefs.getStringSet(key, defValue);
        } else {
            // work around missing getStringSet
            final String raw = prefs.getString(key, null);
            if (raw != null) {
                try {
                    final JSONArray json = new JSONArray(raw);
                    final Set<String> values = new HashSet<>();
                    for (int i = 0; i < json.length(); i++) {
                        values.add(json.getString(i));
                    }
                    return values;
                } catch (final JSONException ignored) {
                }
            }
            return defValue;
        }
    }
}
