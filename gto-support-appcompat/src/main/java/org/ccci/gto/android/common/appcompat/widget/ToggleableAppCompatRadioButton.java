package org.ccci.gto.android.common.appcompat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import org.ccci.gto.android.common.util.view.ViewUtils;

import androidx.appcompat.widget.AppCompatRadioButtonImpl;

public class ToggleableAppCompatRadioButton extends AppCompatRadioButtonImpl {
    public ToggleableAppCompatRadioButton(Context context) {
        super(context);
    }

    public ToggleableAppCompatRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleableAppCompatRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void toggle() {
        if (isChecked()) {
            final RadioGroup radioGroup = ViewUtils.findAncestor(this, RadioGroup.class);
            if (radioGroup != null) {
                radioGroup.clearCheck();
            }
        } else {
            super.toggle();
        }
    }
}
