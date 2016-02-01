package net.jreader.bookwidget.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by songkl on 2015/12/30.
 */
public class PageView extends View {

    Bitmap pageBitmap = null; //
    Drawable shadow = null;
    private Rect mTmpRect = new Rect();

    public PageView(Context context) {
        super(context);
    }

    public PageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        shadow = getResources().getDrawable(R.drawable.shadow_right);
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPageBitmap(Bitmap bitmap) {
        this.pageBitmap = bitmap;
        invalidate();
        //this.postInvalidate();
    }

    public Bitmap getPageBitmap() {
        return pageBitmap;
    }

    private void drawPage(Canvas canvas, Bitmap bitmap, float left, float top) {
        final Rect childRect = mTmpRect;
        getHitRect(childRect);
        canvas.save();
        canvas.drawBitmap(bitmap, left, top, null);
        shadow.setBounds(pageBitmap.getWidth(), childRect.top,
                pageBitmap.getWidth() + shadow.getIntrinsicWidth(), childRect.bottom);
        shadow.draw(canvas);

        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPage(canvas, pageBitmap, 0, 0);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(pageBitmap.getWidth() + shadow.getIntrinsicWidth(),
                heightSize);
    }

}
