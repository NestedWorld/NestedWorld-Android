package com.nestedworld.nestedworld.CustomWidget.Typeface;

import android.content.Context;
import android.util.AttributeSet;

public class Toolbar extends android.support.v7.widget.Toolbar {
    public Toolbar(Context context) {
        this(context, null);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.toolbarStyle);
    }

    public Toolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //Permet d'éviter le crash dans Android studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }

        //TODO appliquer la typeface
    }
}
