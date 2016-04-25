package com.nestedworld.nestedworld.fragment.fight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

import java.util.ArrayList;

public class FightFragment extends BaseFragment {

    private final ArrayList<ImageView> mTiles = new ArrayList<>();

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new FightFragment());
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_fight;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {

        /*We complete the list with every tile*/
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_top));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_top_right));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_right));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_bottom_right));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_bottom));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_bottom_left));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_left));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_top_left));

        ((RelativeLayout) rootView.findViewById(R.id.relativeLayout_fight)).addView(new DrawingGestureView(mContext));
    }

    /**
     * * Custom view used for drawing path simplification
     **/
    private class DrawingGestureView extends View {
        private static final float TOUCH_TOLERANCE = 4;
        private final Paint mPaint;
        private final Path mPath;
        private final Paint mBitmapPaint;
        private final Paint circlePaint;
        private final Path circlePath;
        public int width;
        public int height;
        protected Canvas mCanvas;
        private Bitmap mBitmap;
        private float mX, mY;

        public DrawingGestureView(Context c) {
            super(c);

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(12);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            width = w;
            height = h;

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(circlePath, circlePaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    init_path(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    updatePath(x, y);
                    updateTiles(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    invalidate();
                    clearDrawing();
                    clearTilesBackground();
                    break;
            }
            return true;
        }

        private void init_path(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void updatePath(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);

            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void updateTiles(float x, float y) {
            //We check if the user touch a tiles
            for (ImageView view : mTiles) {
                //hit test
                if ((x > view.getLeft() /*check left limit*/
                        && x < view.getLeft() + view.getWidth() /*check right limit*/
                        && y > view.getTop() /*check top limit*/
                        && y < view.getY() + view.getHeight()/*check bottom limit*/)) {
                    //if he touch, we change the tile background color
                    view.setBackgroundResource(R.drawable.background_rounded);
                }
            }
        }

        private void clearDrawing() {
            circlePath.reset();
            mPath.reset();

            setDrawingCacheEnabled(false);

            onSizeChanged(width, height, width, height);
            invalidate();

            setDrawingCacheEnabled(true);
        }

        private void clearTilesBackground() {
            for (View tiles : mTiles) {
                tiles.setBackgroundColor(Color.TRANSPARENT);
            }
        }

    }
}
