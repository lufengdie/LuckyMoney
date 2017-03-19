package com.tencent.mm.luckymoney;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

/**
 * Created by HuangChengHua on 16/8/10.
 */
public class SwipeBackLayout extends FrameLayout {

    private static final float MIN_VELOCITY = 5f;

    private boolean mIsEnabled;
    private boolean mIsInLayout;
    private int mContentLeft;
    private int mContentTop;
    private int mScreenWidth;
    private int mTouchSlop;
    private float mInitX;
    private float mInitY;

    private Activity mActivity;
    private View mContentView;
    private ViewDragHelper mViewDragHelper;


    private ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (ViewDragHelper.STATE_IDLE == state && getChildAt(0).getLeft() != 0) {
                mActivity.finish();
                mActivity.overridePendingTransition(0, 0);
                mActivity = null;
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mContentLeft = left;
            mContentTop = top;
            float percent = 1f - ((float) left / mScreenWidth);
            int alpha = (int) (180 * percent);
            setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int left = releasedChild.getLeft();
            int settleLeft = 0;
            int leftThreshold = (int) (getWidth() * 0.3f);
            if (xvel > MIN_VELOCITY) {
                settleLeft = mScreenWidth;
            } else if (xvel < -MIN_VELOCITY) {
                settleLeft = 0;
            } else if (left > leftThreshold) {
                settleLeft = mScreenWidth;
            }
            mViewDragHelper.settleCapturedViewAt(settleLeft, releasedChild.getTop());
            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mScreenWidth;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return Math.min(mScreenWidth, Math.max(left, 0));
        }
    };

    public SwipeBackLayout(Activity activity) {
        super(activity);
        init(activity);
    }

    void setContentView(View contentView) {
        this.mContentView = contentView;
    }

    private void init(Activity activity) {
        this.mActivity = activity;
        this.mIsEnabled = true;
        ViewGroupCompat.setMotionEventSplittingEnabled(this, false);
        mTouchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();
        mScreenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        mViewDragHelper = ViewDragHelper.create(this, 0.5f, mDragCallback);
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mIsInLayout = true;
        if (mContentView != null)
            mContentView.layout(mContentLeft, mContentTop,
                    mContentLeft + mContentView.getMeasuredWidth(), mContentTop + mContentView.getMeasuredHeight());
        mIsInLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!mIsInLayout)
            super.requestLayout();
    }


    protected boolean canScrollLeft(View v, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight()
                        && y + scrollY >= child.getTop() && y + scrollY < child.getBottom()
                        && canScrollLeft(child, x + scrollX - child.getLeft(),
                        y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }
        return ViewCompat.canScrollHorizontally(v, -1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mInitX = ev.getX();
                mInitY = ev.getY();
                if (canScrollLeft(mContentView, (int) mInitX, (int) mInitY)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - mInitX;
                float dy = ev.getY() - mInitY;
                if (dx < mTouchSlop || Math.abs(dx) < Math.abs(dy)) {
                    return false;
                }
                break;
        }
        if (!mIsEnabled)
            return false;
        try {
            return mViewDragHelper.shouldInterceptTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsEnabled)
            return false;
        try {
            mViewDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setSwipeEnabled(boolean enabled) {
        mViewDragHelper.abort();
        mIsEnabled = enabled;
    }

    public static SwipeBackLayout attachTo(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Window window = activity.getWindow();
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View contentView = decorView.getChildAt(0);
        decorView.removeViewAt(0);
        SwipeBackLayout swipeBackLayout = new SwipeBackLayout(activity);
        swipeBackLayout.setContentView(contentView);
        swipeBackLayout.addView(contentView);
        decorView.addView(swipeBackLayout, 0);
        return swipeBackLayout;
    }
}
