package com.nestedworld.nestedworld.customWidget.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.internal.widget.TintTypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;

public class TypefaceToolbar extends android.support.v7.widget.Toolbar {

    private final String DEFAULT_TOOLBAR_TYPEFACE = "ProximaNova-Reg.ttf";

    private TextView mTitleTextView;
    private CharSequence mTitleText;
    private int mTitleTextAppearance;
    private int mTitleTextColor;
    private Typeface mTypeface;

    /*
    ** Constructor
     */
    public TypefaceToolbar(Context context) {
        this(context, null);
    }

    public TypefaceToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.toolbarStyle);
    }

    public TypefaceToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Need to use getContext() here so that we use the themed context
        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, android.support.v7.appcompat.R.styleable.Toolbar, defStyleAttr, 0);
        mTitleTextAppearance = a.getResourceId(android.support.v7.appcompat.R.styleable.Toolbar_titleTextAppearance, 0);
        mTitleTextColor = ContextCompat.getColor(context, R.color.apptheme_color);
        initView();
    }

    /*
    ** override every method linked to the title
     */
    @Override
    public void setTitleTextColor(int color) {
        mTitleTextColor = color;
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(color);
        }
    }

    @Override
    public void setTitle(int resId) {
        final String newTitle = getResources().getString(resId);

        if (!TextUtils.isEmpty(newTitle)) {
            mTitleText = newTitle;
        }
    }

    @Override
    public void setTitleTextAppearance(Context context, int resId) {
        mTitleTextAppearance = resId;
        if (mTitleTextView != null) {
            mTitleTextView.setTextAppearance(resId);
        }
    }

    @Override
    public CharSequence getTitle() {
        return mTitleText;
    }

    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        }
        mTitleText = title;
    }

    /*
    ** Custom private method
     */
    private void initView() {
        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(getContext().getAssets(), DEFAULT_TOOLBAR_TYPEFACE);
        }
        initTitleTextView();
    }

    private void initTitleTextView() {
        if (mTitleTextView != null) {
            return;
        }

        final Context context = getContext();
        mTitleTextView = new TextView(context);
        mTitleTextView.setSingleLine();
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        mTitleTextView.setTypeface(mTypeface);
        if (mTitleTextAppearance != 0) {
            mTitleTextView.setTextAppearance(context, mTitleTextAppearance);
        }
        if (mTitleTextColor != 0) {
            mTitleTextView.setTextColor(mTitleTextColor);
        }
        addView(mTitleTextView);
    }
}
