package com.nestedworld.nestedworld.customWidget.typeface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.utils.typeface.FontManager;

public class TextView extends com.rey.material.widget.TextView {

    public TextView(final Context context) {
        this(context, null);
    }

    public TextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public TextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

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

                //Ajout d'un flag qui permet d'avoir un affichage correct de la police
                setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
            array.recycle();
        }
    }
}