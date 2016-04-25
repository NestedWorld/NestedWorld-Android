package com.nestedworld.nestedworld.customView.drawingGestureView;

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
import com.nestedworld.nestedworld.customView.drawingGestureView.listener.DrawingGestureListener;
import com.nestedworld.nestedworld.customView.drawingGestureView.listener.OnFinishMoveListener;

import java.util.ArrayList;
import java.util.List;

/**
 * * Custom view used for drawing path simplification
 **/
public class DrawingGestureView extends View {

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
    private List<ImageView> mTiles = new ArrayList<>();
    @Nullable private DrawingGestureListener mOnTileTouchListener = null;
    @Nullable private OnFinishMoveListener mOnFinishMoveListener = null;

    public DrawingGestureView(@NonNull Context c) {
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

    public void setOnTileTouchListener(@NonNull DrawingGestureListener listener) {
        mOnTileTouchListener = listener;
    }

    public void setmOnFinishMoveListener(@NonNull OnFinishMoveListener listener) {
        mOnFinishMoveListener = listener;
    }

    public void setTiles(List<ImageView> tiles) {
        mTiles = tiles;
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
        super.onTouchEvent(event);

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
                if (mOnFinishMoveListener != null) {
                    mOnFinishMoveListener.onFinish();
                }
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

                //if he touch, we change the tile background color and we call the listener
                view.setBackgroundResource(R.drawable.background_rounded);
                if (mOnTileTouchListener != null) {
                    mOnTileTouchListener.onTouch(mTiles.indexOf(view));
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
