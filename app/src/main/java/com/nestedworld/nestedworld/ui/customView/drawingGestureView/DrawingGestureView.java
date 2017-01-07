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
import com.nestedworld.nestedworld.ui.customView.drawingGestureView.listener.DrawingGestureListener;
import com.nestedworld.nestedworld.ui.customView.drawingGestureView.listener.OnFinishMoveListener;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

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
    private final Paint circlePaint;
    private final Path circlePath;
    private int width;
    private int height;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private float mX, mY;
    private List<ImageView> mTiles = new ArrayList<>();
    private Integer mLastTilesTouch = null;
    @Nullable
    private DrawingGestureListener mOnTileTouchListener = null;
    @Nullable
    private OnFinishMoveListener mOnFinishMoveListener = null;

    /*
    ** Constructor
     */
    public DrawingGestureView(@NonNull final Context c) {
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

    /*
    ** Public method
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
    ** Life cycle
     */
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
        super.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

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
    ** Internal method
     */
    private void initPath(final float x, final float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void updatePath(final float x, final float y) {
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

    private void updateTiles(final float x, final float y) {
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
