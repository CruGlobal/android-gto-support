package androidx.lifecycle;

// TODO: convert this to Kotlin once Android Test Fixtures support Kotlin
//       https://youtrack.jetbrains.com/issue/KT-50667
//       https://issuetracker.google.com/issues/259523353
public class ViewModelTestInternals {
    public static void clear(ViewModel viewModel) {
        viewModel.clear();
    }
}
