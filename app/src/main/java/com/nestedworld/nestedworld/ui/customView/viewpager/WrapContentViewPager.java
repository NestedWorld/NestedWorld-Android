package com.nestedworld.nestedworld.ui.customView.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class WrapContentViewPager extends ViewPager {

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public WrapContentViewPager(@NonNull final Context context) {
        super(context);
    }

    public WrapContentViewPager(@NonNull final Context context,
                                @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * #############################################################################################
     * # ViewPager implementation
     * #############################################################################################
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            final int h = child.getMeasuredHeight();
            if (h > height) {
                height = h;
            }
        }

        final int newMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, newMeasureSpec);
    }
}
