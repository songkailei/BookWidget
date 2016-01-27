package net.jreader.bookwidget.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Map;

/**
 * Created by songkl on 2015/12/29.
 */
public class DragLayout extends FrameLayout {

    private ViewDragHelper dragHelper;

    private View edgeView;
    private View curView;
    private View nextView;

    private static final float DEFAULT_SCROLL_THRESHOLD = 0.1f;
    private float mScrollThreshold = DEFAULT_SCROLL_THRESHOLD;
    private static final int DEFAULT_EDGE_SIZE = 500;
    private float mScrollPercent;

    private int dragState = ViewDragHelper.STATE_IDLE;
    private boolean dragRecovery = false;

    private PageDragCallback listener;

    public DragLayout(Context context) {
        super(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        dragHelper = ViewDragHelper.create(this, mCallback);
        dragHelper.setEdgeSize(DEFAULT_EDGE_SIZE);
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(this, mCallback);
    }

    public void setOnPageDragCallback(PageDragCallback listener) {
        this.listener = listener;
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child != edgeView && child != nextView;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            super.clampViewPositionHorizontal(child, left, dx);
            if (child == edgeView) {
                if (left < 0) {
                    return left;
                } else {
                    return 0;
                }
            } else {
                if (left > 0) {
                    return 0;
                } else {
                    return left;
                }
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mScrollPercent = (float) left / changedView.getWidth();
            dragState = dragHelper.getViewDragState();
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();
            final int childHeight = releasedChild.getHeight();

            if (dragState != ViewDragHelper.STATE_SETTLING) {
                int left = 0, top = 0;

                if (mScrollPercent < 0) {
                    if (releasedChild == edgeView) {
                        if (mScrollPercent == -1) {
                            left = 0;
                        } else {
                            left = 1 + mScrollPercent > mScrollThreshold ? 0 : -childWidth;
                        }
                    } else {
                        left = Math.abs(mScrollPercent) > mScrollThreshold ? -childWidth : 0;
                    }
                } else {
                    if (mScrollPercent == 0 && releasedChild == curView) {
                        left = -childWidth;
                    } else {
                        left = mScrollPercent > mScrollThreshold ? childWidth : 0;
                    }

                }
                dragHelper.settleCapturedViewAt(left, top);
                if ((releasedChild == edgeView && left == -childWidth) || (releasedChild == curView && left == 0)) {
                    dragRecovery = true;
                } else {
                    dragRecovery = false;
                }
            }
            invalidate();
        }

        public void onEdgeTouched(int edgeFlags, int pointerId) {
            if (dragState != ViewDragHelper.STATE_SETTLING) {
                dragHelper.captureChildView(edgeView, pointerId);
            }
        }

        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
            dragHelper.captureChildView(edgeView, pointerId);
        }
    };

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    public boolean doTouchEvent(MotionEvent event) {
        try {
            dragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            if (mScrollPercent == -1 && dragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE
                    && dragState == ViewDragHelper.STATE_SETTLING && !dragRecovery) {
                // for next page
                Map<String, Bitmap> map = listener.pageDown();
                ((PageView) curView).setPageBitmap(map.get("cur"));
                ((PageView) nextView).setPageBitmap(map.get("next"));
                ((PageView) edgeView).setPageBitmap(map.get("pre"));
                curView.layout(0, 0, curView.getWidth(), curView.getHeight());
                dragState = ViewDragHelper.STATE_IDLE;
            } else if (mScrollPercent == 0 && dragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE
                    && dragState == ViewDragHelper.STATE_SETTLING && !dragRecovery) {
                // for pre page
                Map<String, Bitmap> map = listener.pageUp();
                ((PageView) curView).setPageBitmap(map.get("cur"));
                ((PageView) nextView).setPageBitmap(map.get("next"));
                ((PageView) edgeView).setPageBitmap(map.get("pre"));
                edgeView.layout(-edgeView.getWidth(), 0, 0, edgeView.getHeight());
                dragState = ViewDragHelper.STATE_IDLE;
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        edgeView = (View) getChildAt(2);
        curView = (View) getChildAt(1);
        nextView = (View) getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == edgeView) {
                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();
                int childLeft = -width;
                int childTop = 0;
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
    }

}
