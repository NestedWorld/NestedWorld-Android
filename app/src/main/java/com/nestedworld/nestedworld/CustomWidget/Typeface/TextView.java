package com.nestedworld.nestedworld.CustomWidget.Typeface;

import android.content.Context;
import android.util.AttributeSet;

public class TextView extends com.rey.material.widget.TextView {

    public TextView(final Context context) {
        this(context, null);
    }

    public TextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        //Permet d'éviter le crash dans Android studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }

        //TODO appliquer la typeface
    }
}