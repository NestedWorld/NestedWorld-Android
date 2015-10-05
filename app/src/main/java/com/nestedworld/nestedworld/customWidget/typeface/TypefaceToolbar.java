package com.nestedworld.nestedworld.customWidget.typeface;

import com.nestedworld.nestedworld.utils.typeface.FontManager;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Custom Toolbar implementation
 * it will change the typeface of the title
 * the typeface is define with a static field (DEFAULT_TOOLBAR_TYPEFACE)
 */
public class TypefaceToolbar extends android.support.v7.widget.Toolbar {

    private final static String TAG = TypefaceToolbar.class.getSimpleName();
    private final String DEFAULT_TOOLBAR_TYPEFACE = "ProximaNova-Reg.ttf";
    private Typeface mTypeface;

    /*
    ** Constructor
     */
    public TypefaceToolbar(Context context) {
        this(context, null);
    }

    public TypefaceToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TypefaceToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /*
    ** override every method linked to the title
     */
    @Override
    public void setTitle(int resId) {
        super.setTitle(resId);
        setTypeface(this, mTypeface);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        setTypeface(this, mTypeface);
    }

    /*
    ** Custom private method
     */
    private void init(final Context context) {
        mTypeface = FontManager.getInstance(context.getAssets()).getFont(DEFAULT_TOOLBAR_TYPEFACE);
    }

    // recursively apply typeface to our textviews
    private void setTypeface(ViewGroup viewGroup, Typeface typeface) {
        if (viewGroup == null) return;

        int children = viewGroup.getChildCount();
        for (int i = 0; i < children; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) setTypeface((ViewGroup) view, typeface);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setTypeface(typeface);
            }
        }
    }
}
