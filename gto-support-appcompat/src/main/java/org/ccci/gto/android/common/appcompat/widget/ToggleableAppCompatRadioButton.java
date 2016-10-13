package org.ccci.gto.android.common.appcompat.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import org.ccci.gto.android.common.util.ViewUtils;

public class ToggleableAppCompatRadioButton extends AppCompatRadioButton {
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
