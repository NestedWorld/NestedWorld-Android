package com.nestedworld.nestedworld.customWidget.typeface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.utils.typeface.FontManager;

/**
 * Custom TextView implementation
 * it allow custom typeface via xml attribute
 */
public class TypefaceTextView extends AppCompatTextView {

    /*
    ** Constructor
     */
    public TypefaceTextView(final Context context) {
        this(context, null);
    }

    public TypefaceTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TypefaceTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        //Avoid crash under interface build
        if (this.isInEditMode()) {
            return;
        }

        applyTypeface(context, attrs);
    }

    /*
    ** Utils
     */
    private void applyTypeface(final Context context, final AttributeSet attrs) {
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TypefaceStyle);

        if (array != null) {
            final String typefaceName = array.getString(R.styleable.TypefaceStyle_typeface);

            if (!TextUtils.isEmpty(typefaceName)) {
                final Typeface typeface = FontManager.getInstance(context.getAssets()).getFont(typefaceName);
                setTypeface(typeface);

                //Render Optimisation
                setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
            array.recycle();
        }
    }
}