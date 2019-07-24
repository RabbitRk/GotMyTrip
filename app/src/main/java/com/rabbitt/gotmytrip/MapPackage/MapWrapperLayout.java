package com.rabbitt.gotmytrip.MapPackage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class MapWrapperLayout extends FrameLayout {

    private static final String TAG = "MapWrapper";
    public Bitmap currentImage;
    public Paint paint;
    private Canvas mCanvas;
    private OnDragListener mOnDragListener;
    private View mView;

    public void setCurrentImage(Bitmap bitmap) {
        currentImage = bitmap;
    }

    public interface OnDragListener {
        void onDrag(MotionEvent motionEvent);
    }

    public MapWrapperLayout(Context context) {
        super(context);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mCanvas == null || mCanvas != canvas) {
            mCanvas = canvas;
        }
        if (currentImage != null) {
            canvas.drawBitmap(currentImage, (canvas.getWidth() - currentImage.getWidth()) / 2,
                    (canvas.getHeight() - 2 * currentImage.getHeight()) / 2, paint);
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long l) {
        if (mCanvas == null || mCanvas != canvas) {
            mCanvas = canvas;
        }
        if (mView == null || mView != view) {
            mView = view;
        }
        boolean flag = super.drawChild(canvas, view, l);
        if (currentImage != null) {
            canvas.drawBitmap(currentImage, (canvas.getWidth() - currentImage.getWidth()) / 2,
                    (canvas.getHeight() - 2 * currentImage.getHeight()) / 2, paint);
        }
        return flag;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mOnDragListener != null) {
            mOnDragListener.onDrag(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setOnDragListener(OnDragListener mOnDragListener) {
        Log.i(TAG, "setOnDragListener:  " );
        this.mOnDragListener = mOnDragListener;
    }
}