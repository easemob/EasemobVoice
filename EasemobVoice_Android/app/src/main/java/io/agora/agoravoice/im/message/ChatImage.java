package io.agora.agoravoice.im.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import io.agora.agoravoice.R;

public class ChatImage extends androidx.appcompat.widget.AppCompatImageView {

    private Context mContext;
    private static final Xfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Bitmap mMaskBitmap;
    private Paint mPaint;
    private WeakReference<Bitmap> mSrcWeakBitmap;
    private int mLastWidth;
    private int mLastHeight;
    private int maskRes;

    public ChatImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.cmui_MaskImage);
        maskRes = a.getResourceId(R.styleable.cmui_MaskImage_cmui_mask, -1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void invalidate() {
        mSrcWeakBitmap = null;
        if (mMaskBitmap != null) {
            mMaskBitmap.recycle();
        }
        mLastWidth = 0;
        mLastHeight = 0;
        super.invalidate();
    }
    private float[] radiusArray = { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f };

    /**
     * 设置四个角的圆角半径
     */
    public void setRadius(float leftTop, float rightTop, float rightBottom, float leftBottom) {
        radiusArray[0] = leftTop;
        radiusArray[1] = leftTop;
        radiusArray[2] = rightTop;
        radiusArray[3] = rightTop;
        radiusArray[4] = rightBottom;
        radiusArray[5] = rightBottom;
        radiusArray[6] = leftBottom;
        radiusArray[7] = leftBottom;

        invalidate();
    }
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInEditMode()) {
            int width = getWidth();
            int height = getHeight();
            if (getDrawable() == null) {
                return;
            }

            int i = canvas.saveLayer(0.0F, 0.0F, width, height, null, Canvas.ALL_SAVE_FLAG);
            try {
                Bitmap srcBitmap = mSrcWeakBitmap != null ? mSrcWeakBitmap.get() : null;

                if (srcBitmap == null || srcBitmap.isRecycled()) {
                    Drawable srcDrawable = getDrawable();
                    if (srcDrawable != null) {
                        srcBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas srcBitmapCanvas = new Canvas(srcBitmap);
                        srcDrawable.setBounds(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
                        srcDrawable.draw(srcBitmapCanvas);
                        if (mMaskBitmap == null || mMaskBitmap.isRecycled()
                                || mLastWidth != width || mLastHeight != height) {
                            mMaskBitmap = getMask(width, height);
                        }
                        drawBitmap(srcBitmapCanvas, mMaskBitmap, mPaint);
                        mSrcWeakBitmap = new WeakReference<Bitmap>(srcBitmap);
                    }
                }

                if (srcBitmap != null) {
                    mPaint.setXfermode(null);
                    canvas.drawBitmap(srcBitmap, 0.0F, 0.0F, mPaint);
                }
            } catch (Exception e) {
                System.gc();
            } finally {
                canvas.restoreToCount(i);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    public void drawBitmap(Canvas canvas, Bitmap bitmap, Paint paint) {
        paint.reset();
        paint.setFilterBitmap(false);
        paint.setXfermode(sXfermode);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private Bitmap getMask(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        Drawable mask = mContext.getResources().getDrawable(maskRes);
        mask.setBounds(0, 0, width, height);
        mask.draw(canvas);
        return bitmap;
    }
}
