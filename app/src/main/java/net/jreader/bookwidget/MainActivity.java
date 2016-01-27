package net.jreader.bookwidget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import net.jreader.bookwidget.library.DragLayout;
import net.jreader.bookwidget.library.PageDragCallback;
import net.jreader.bookwidget.library.PageView;
import net.jreader.bookwidget.library.Util;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PageDragCallback {

    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private DragLayout dragLayout;
    private int i = 0;

    private Bitmap nextPageBitmap;
    private Bitmap curPageBitmap;
    private Bitmap prePageBitmap;

    private Canvas nextCanvas;
    private Canvas curCanvas;
    private Canvas preCanvas;

    PageView nextView;
    PageView curView;
    PageView preView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        dragLayout = (DragLayout) findViewById(R.id.dragLayout);
        mWidth = Util.getWindowWidth(MainActivity.this);
        mHeight = Util.getWindowHeight(MainActivity.this);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(50);
        mPaint.setColor(getResources().getColor(R.color.black));

        nextPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        curPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        prePageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

        nextCanvas = new Canvas(nextPageBitmap);
        curCanvas = new Canvas(curPageBitmap);
        preCanvas = new Canvas(prePageBitmap);

        nextView = (PageView) findViewById(R.id.nextPage);
        curView = (PageView) findViewById(R.id.curPage);
        preView = (PageView) findViewById(R.id.prePage);

        initDragLayout();

        dragLayout.setOnPageDragCallback(this);
        dragLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return dragLayout.doTouchEvent(event);
            }
        });
    }

    private void initDragLayout() {
        drawPage(nextCanvas, i + 1 + "", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.bg_red));
        drawPage(curCanvas, i + "", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.bg_red));
        drawPage(preCanvas, i - 1 + "", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.bg_red));

        nextView.setPageBitmap(nextPageBitmap);
        curView.setPageBitmap(curPageBitmap);
        preView.setPageBitmap(prePageBitmap);
    }

    private void drawPage(Canvas canvas, String content, Bitmap m_book_bg) {
        Matrix matrix = new Matrix();//使用matri控制图形变换
        float w = ((float) mWidth / m_book_bg.getWidth());//屏幕宽度除图片宽度
        float h = ((float) mHeight / m_book_bg.getHeight());//屏幕高度除图片高度
        matrix.postScale(w, h);// 获取缩放比例
        // 根据缩放比例获得新的位图
        Bitmap background = Bitmap.createBitmap(m_book_bg, 0, 0, m_book_bg.getWidth(),
                m_book_bg.getHeight(), matrix, true);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawText(content, 0, 55, mPaint);
    }

    @Override
    public Map<String, Bitmap> pageDown() {
        Map<String, Bitmap> map = new HashMap<String, Bitmap>();
        i++;
        map.put("cur", nextView.getPageBitmap().copy(nextView.getPageBitmap().getConfig(), false));
        map.put("pre", curView.getPageBitmap());
        drawPage(nextCanvas, i + 1 + "", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.bg_red));
        map.put("next", nextPageBitmap);
        return map;
    }

    @Override
    public Map<String, Bitmap> pageUp() {
        Map<String, Bitmap> map = new HashMap<String, Bitmap>();
        i--;
        map.put("next", curView.getPageBitmap());
        map.put("cur", preView.getPageBitmap().copy(preView.getPageBitmap().getConfig(), false));
        drawPage(preCanvas, i - 1 + "", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.bg_red));
        map.put("pre", prePageBitmap);
        return map;
    }
}
