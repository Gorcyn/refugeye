package com.refugeye.widget;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;

import com.refugeye.R;
import com.refugeye.helper.BitmapHelper;

public class DrawingView extends View {

    private static final String STATE_SELF = "self";
    private static final String STATE_CANVAS = "canvasBitmap";

    private Path drawPath;
    private Paint canvasPaint;
    private Bitmap canvasBitmap = null;
    private Paint drawPaint;
    private Canvas drawCanvas;

    private boolean restored = false;

    Handler handler = new Handler();

    public Bitmap getBitmap() {
        return canvasBitmap;
    }

    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle state = new Bundle();
        state.putParcelable(STATE_SELF, super.onSaveInstanceState());

        File file = BitmapHelper.saveToFile(getContext(), canvasBitmap, "state.png");
        if (file != null) {
            state.putString(STATE_CANVAS, file.getAbsolutePath());
        }
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle stateBundle = (Bundle) state;
            super.onRestoreInstanceState(stateBundle.getParcelable(STATE_SELF));

            String path = stateBundle.getString(STATE_CANVAS);
            if (path != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap previousBitmap = BitmapHelper.loadFromFile(getContext(), "state.png");
                        if (previousBitmap != null) {
                            restoreDrawing(BitmapHelper.trimBitmap(previousBitmap));
                        }
                    }
                }, 100);
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void onPause() {
        BitmapHelper.saveToFile(getContext(), canvasBitmap, "state.png");
    }

    public void onResume() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap previousBitmap = BitmapHelper.loadFromFile(getContext(), "state.png");
                restoreDrawing(previousBitmap);
            }
        }, 100);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setSaveEnabled(true);
    }

    public void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(getResources().getColor(R.color.orange_transp));
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (canvasBitmap == null) {
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPoint(touchX, touchY, drawPaint);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void addBitmap(Bitmap bitmap, float x, float y) {
        drawCanvas.drawBitmap(bitmap, x - bitmap.getWidth() / 2, y - bitmap.getHeight() / 2, drawPaint);
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
        invalidate();
    }

    public void reset() {
        canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        invalidate();
    }

    private void restoreDrawing(Bitmap previousBitmap) {
        if (restored) {
            restored = false;
            return;
        }
        int orientation = OrientationHelper.VERTICAL;
        if (getWidth() >= getHeight()) {
            orientation = OrientationHelper.HORIZONTAL;
        }
        if (previousBitmap != null) {
            setupDrawing();
            if (orientation == OrientationHelper.VERTICAL) {
                canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            }
            drawCanvas = new Canvas(canvasBitmap);
            invalidate();

            float left = (getWidth() - previousBitmap.getWidth()) / 2;
            float top = (getHeight() - previousBitmap.getHeight()) / 2;
            drawCanvas.drawBitmap(previousBitmap, left, top, canvasPaint);
            invalidate();
            restored = true;
        }
    }
}
