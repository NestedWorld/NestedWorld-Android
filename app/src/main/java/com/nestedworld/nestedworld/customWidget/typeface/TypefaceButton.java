package com.nestedworld.nestedworld.customWidget.typeface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.utils.typeface.FontManager;

/**
 * Custom Button implementation
 * it allow custom typeface via xml attribute
 */
public class TypefaceButton extends AppCompatButton {

    /*
    ** Constructor
     */
    public TypefaceButton(Context context) {
        this(context, null);
    }

    public TypefaceButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.buttonStyle);
    }

    public TypefaceButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

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
