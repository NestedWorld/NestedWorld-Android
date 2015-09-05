package com.nestedworld.nestedworld.customWidget.typeface;

import android.content.Context;
import android.util.AttributeSet;

public class EditText extends com.rey.material.widget.EditText {
    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //Permet d'éviter le crash dans Android studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }

        //TODO appliquer la typeface
    }
}
