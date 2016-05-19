package com.nestedworld.nestedworld.customView.monsterSelector;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nestedworld.nestedworld.R;

public class ViewPagerArrowIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {

    private ImageView mRightArrow = null;
    private ImageView mLeftArrow = null;
    private int mRightArrowId = R.drawable.ic_arrow_right;
    private int mLeftArrowId = R.drawable.ic_arrow_left;
    private ViewPager mViewPager;

    /*
    ** Constructor
     */
    public ViewPagerArrowIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerArrowIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ViewPagerArrowIndicator, 0, 0);
        try {
            mLeftArrowId = a.getResourceId(R.styleable.ViewPagerArrowIndicator_left_arrow,  R.drawable.ic_arrow_left);
            mRightArrowId = a.getResourceId(R.styleable.ViewPagerArrowIndicator_right_arow, R.drawable.ic_arrow_right);
        } finally {
            a.recycle();
        }

        init(context);
    }

    /*
    ** Private method
     */
    private void init(Context context) {
        this.setOrientation(HORIZONTAL);
        mLeftArrow = new ImageView(context);
        mRightArrow = new ImageView(context);

        // default arrow indicators
        setArrowIndicatorRes(R.drawable.ic_arrow_left,
                R.drawable.ic_arrow_right);

    }

    public void setViewPager(@NonNull final ViewPager viewPager) {
        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);

        mLeftArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnFirstPage()) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                }
            }
        });

        mRightArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnLastPage()) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }
            }
        });

        arrange();
    }

    private void handleVisibility() {
        if (isOnFirstPage()) {
            mLeftArrow.setVisibility(INVISIBLE);
        } else {
            mLeftArrow.setVisibility(VISIBLE);
        }

        if (isOnLastPage()) {
            mRightArrow.setVisibility(INVISIBLE);
        } else {
            mRightArrow.setVisibility(VISIBLE);
        }
    }

    private void arrange() {
        //Make arrow center
        setGravity(Gravity.CENTER_VERTICAL);

        //Retrieve the viewPager for changing his weight
        View view = getChildAt(0);
        removeViewAt(0);
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);

        //Add arrow and pager in the correct order
        addView(mLeftArrow);
        addView(view);
        addView(mRightArrow);

        //Set correct arrow visibility
        handleVisibility();
    }

    public void setArrowIndicatorRes(int leftArrowResId, int rightArrowResId) {
        mLeftArrow.setImageResource(leftArrowResId);
        mRightArrow.setImageResource(rightArrowResId);
    }

    /*
    ** AddOnPageListener implementation
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        handleVisibility();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /*
    ** Utils
     */
    private boolean isOnLastPage() {
        return mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1;
    }

    private boolean isOnFirstPage() {
        return mViewPager.getCurrentItem() == 0;
    }


}
