package com.nestedworld.nestedworld.customWidget.typeface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.utils.typeface.FontManager;

public class EditText extends com.rey.material.widget.EditText {

    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        //Permet d'éviter le crash dans Android studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TypefaceStyle);

        if (array != null) {
            final String typefaceName = array.getString(R.styleable.TypefaceStyle_typeface);

            if (!TextUtils.isEmpty(typefaceName)) {
                final Typeface typeface = FontManager.getInstance(context.getAssets()).getFont(typefaceName);
                setTypeface(typeface);
            }
            array.recycle();
        }
    }
}
