package com.nestedworld.nestedworld.ui.customView.drawingGestureView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.ui.customView.drawingGestureView.listener.DrawingGestureListener;
import com.nestedworld.nestedworld.ui.customView.drawingGestureView.listener.OnFinishMoveListener;

import java.util.ArrayList;
import java.util.List;

/**
 * * Custom view used for drawing path simplification
 **/
public class DrawingGestureView extends View {
    private final static String TAG = DrawingGestureView.class.getSimpleName();

    private static final float TOUCH_TOLERANCE = 4;

    private final Paint mPaint;
    private final Path mPath;
    private final Paint mBitmapPaint;
    private final Paint mCirclePaint;
    private final Path mCirclePath;

    private int mWidth;
    private int mHeight;
    private Bitmap mBitmap;
    private float mX, mY;
    private List<ImageView> mTiles = new ArrayList<>();
    private Integer mLastTilesTouch = null;
    @Nullable
    private DrawingGestureListener mOnTileTouchListener = null;
    @Nullable
    private OnFinishMoveListener mOnFinishMoveListener = null;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public DrawingGestureView(@NonNull final Context context) {
        super(context);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mCirclePaint = new Paint();
        mCirclePath = new Path();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.BLUE);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeJoin(Paint.Join.MITER);
        mCirclePaint.setStrokeWidth(4f);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    /*
     * #############################################################################################
     * # Public method
     * #############################################################################################
     */
    public void setOnTileTouchListener(@Nullable final DrawingGestureListener listener) {
        LogHelper.i(TAG, "setOnTileTouchListener");
        mOnTileTouchListener = listener;
    }

    public void setOnFinishMoveListener(@Nullable final OnFinishMoveListener listener) {
        LogHelper.i(TAG, "setOnFinishMoveListener");
        mOnFinishMoveListener = listener;
    }

    public void setTiles(@NonNull final List<ImageView> tiles) {
        LogHelper.i(TAG, "setTiles");
        mTiles = tiles;
    }

    /*
     * #############################################################################################
     * # View implementation
     * #############################################################################################
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mCirclePath, mCirclePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initPath(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                updatePath(x, y);
                updateTiles(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mOnFinishMoveListener != null) {
                    LogHelper.i(TAG, "onTouchEvent > mOnFinishMoveListener.onFinish()");
                    mOnFinishMoveListener.onFinish();
                }
                invalidate();
                clearDrawing();
                clearTilesBackground();
                break;
            default:
                break;
        }
        return true;
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void initPath(final float x,
                          final float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void updatePath(final float x,
                            final float y) {
        final float dx = Math.abs(x - mX);
        final float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            mCirclePath.reset();
            mCirclePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void updateTiles(final float x,
                             final float y) {
        //We check if the player touch a tiles
        for (ImageView view : mTiles) {

            //hit test
            if ((x > view.getLeft() /*check left limit*/
                    && x < (view.getLeft() + view.getWidth()) /*check right limit*/
                    && y > view.getTop() /*check top limit*/
                    && y < (view.getY() + view.getHeight())/*check bottom limit*/)) {

                //if he touch, we change the tile background color and we call the listener
                view.setBackgroundResource(R.drawable.circle_fill_apptheme);
                if (mOnTileTouchListener != null) {
                    LogHelper.i(TAG, "onTouchEvent > mOnFinishMoveListener.onTouch(id=" + view.getId() + ")");
                    if (mLastTilesTouch == null || mLastTilesTouch != view.getId()) {
                        mLastTilesTouch = view.getId();
                        mOnTileTouchListener.onTouch(view.getId());
                    }
                }
            }
        }
    }

    private void clearDrawing() {
        mCirclePath.reset();
        mPath.reset();

        setDrawingCacheEnabled(false);

        onSizeChanged(mWidth, mHeight, mWidth, mHeight);
        invalidate();

        setDrawingCacheEnabled(true);
    }

    private void clearTilesBackground() {
        for (final View tiles : mTiles) {
            tiles.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
