package com.refugeye.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;

import com.refugeye.R;
import com.refugeye.helper.BitmapHelper;

public class DrawingView extends View {

    public interface OnDrawingChangeListener {
        void onDrawingChange(Bitmap drawing);
    }

    final Handler handler = new Handler();

    private Path drawPath;
    private Paint canvasPaint;
    private Bitmap canvasBitmap = null;
    private Paint drawPaint;
    private Canvas drawCanvas;

    @Nullable
    private OnDrawingChangeListener onDrawingChangeListener;

    public void setOnDrawingChangeListener(@Nullable OnDrawingChangeListener listener) {
        onDrawingChangeListener = listener;
    }

    public Bitmap getBitmap() {
        return canvasBitmap;
    }

    public DrawingView(Context context) {
        super(context);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDrawing();
    }

    private void setupDrawing() {
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

    @SuppressLint("ClickableViewAccessibility")
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

    @Override
    public void invalidate() {
        super.invalidate();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onDrawingChangeListener != null) {
                    onDrawingChangeListener.onDrawingChange(canvasBitmap);
                }
            }
        }, 100);
    }

    public void restoreDrawing(@Nullable final Bitmap previousBitmap) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (previousBitmap == canvasBitmap) {
                    return;
                }
                if (previousBitmap == null) {
                    canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                    drawCanvas = new Canvas(canvasBitmap);
                    invalidate();
                    return;
                }
                int orientation = OrientationHelper.VERTICAL;
                if (getWidth() >= getHeight()) {
                    orientation = OrientationHelper.HORIZONTAL;
                }
                setupDrawing();
                if (orientation == OrientationHelper.VERTICAL) {
                    canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                } else {
                    canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                }
                drawCanvas = new Canvas(canvasBitmap);
                invalidate();

                Bitmap trimmedBitmap = BitmapHelper.trimBitmap(previousBitmap);
                if (trimmedBitmap.getWidth() > getWidth() || trimmedBitmap.getHeight() > getHeight()) {
                    trimmedBitmap = BitmapHelper.resizeBitmap(trimmedBitmap, getWidth(), getHeight());
                }
                float left = (getWidth() - trimmedBitmap.getWidth()) / 2;
                float top = (getHeight() - trimmedBitmap.getHeight()) / 2;
                drawCanvas.drawBitmap(trimmedBitmap, left, top, canvasPaint);
                invalidate();
            }
        }, 100);
    }
}
