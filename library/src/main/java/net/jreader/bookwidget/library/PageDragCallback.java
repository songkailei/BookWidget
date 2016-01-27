package net.jreader.bookwidget.library;

import android.graphics.Bitmap;

import java.util.Map;

/**
 * Created by songkl on 2015/12/31.
 */
public interface PageDragCallback {
    public Map<String, Bitmap> pageDown();
    public Map<String, Bitmap> pageUp();
}
