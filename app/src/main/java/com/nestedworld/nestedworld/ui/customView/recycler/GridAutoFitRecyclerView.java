package com.nestedworld.nestedworld.ui.customView.recycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class GridAutoFitRecyclerView extends RecyclerView {

    private GridAutoFitLayoutManager mGridAutoFitLayoutManager;

    /*
    ** Constructor
     */
    public GridAutoFitRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public GridAutoFitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /*
    ** Public method
     */
    public void setColumnWidth(@Dimension int width) {
        mGridAutoFitLayoutManager.setColumnWidth(width);
    }

    /*
    ** Internal method
     */
    private void init(Context context, AttributeSet attrs) {
        mGridAutoFitLayoutManager = new GridAutoFitLayoutManager(getContext());

        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            int columWidth = array.getDimensionPixelSize(0, -1);
            if (columWidth > 0) {
                mGridAutoFitLayoutManager.setColumnWidth(columWidth);
            }
            array.recycle();
        }

        setLayoutManager(mGridAutoFitLayoutManager);
    }

    /*
    ** Inner class
     */
    private static class GridAutoFitLayoutManager extends GridLayoutManager {
        private int mColumnWidth = -1;
        private boolean mColumnWidthChanged = true;

        /*
        ** Constructor
         */
        public GridAutoFitLayoutManager(@NonNull final Context context) {
            super(context, 1);
        }

        /*
        ** Public method
         */
        public void setColumnWidth(@Dimension final int newColumnWidth) {
            if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
                mColumnWidth = newColumnWidth;
                mColumnWidthChanged = true;
            }
        }

        /*
        ** Life cycle
         */
        @Override
        public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
            int width = getWidth();
            int height = getHeight();
            if (mColumnWidthChanged && mColumnWidth > 0 && width > 0 && height > 0) {
                int totalSpace;
                if (getOrientation() == VERTICAL) {
                    totalSpace = width - getPaddingRight() - getPaddingLeft();
                } else {
                    totalSpace = height - getPaddingTop() - getPaddingBottom();
                }

                int spanCount = Math.max(1, totalSpace / mColumnWidth);
                setSpanCount(spanCount);
                mColumnWidthChanged = false;
            }
            super.onLayoutChildren(recycler, state);
        }
    }
}
