package androidx.lifecycle;

import android.annotation.SuppressLint;

// TODO: convert this to Kotlin once Android Test Fixtures support Kotlin
//       https://youtrack.jetbrains.com/issue/KT-50667
//       https://issuetracker.google.com/issues/259523353
public class ViewModelTestInternals {
    @SuppressLint("RestrictedApi")
    public static void clear(ViewModel viewModel) {
        ViewModelStore store = new ViewModelStore();
        store.put("tmp", viewModel);
        store.clear();
    }
}
