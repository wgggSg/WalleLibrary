package mo.wall.org.nestedscrolling;

import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/**
 * Copyright (C), 2018-2019
 * Author: ziqimo
 * Date: 2019/8/14 3:41 PM
 * Description: ${DESCRIPTION}
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class MyNestedScrollChild extends LinearLayout implements NestedScrollingChild2 {


    private NestedScrollingChildHelper mNestedScrollingChildHelper;


    private final int[] offset = new int[2]; //偏移量

    private final int[] consumed = new int[2]; //消费


    private int lastY;
    private int showHeight;

    private int mTouchSlop;

    public MyNestedScrollChild(Context context) {
        super(context);
        init();
    }

    public MyNestedScrollChild(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyNestedScrollChild(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyNestedScrollChild(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //第一次测量，因为布局文件中高度是wrap_content，因此测量模式为atmost，即高度不超过父控件的剩余空间
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        showHeight = getMeasuredHeight();

        //第二次测量，对高度没有任何限制，那么测量出来的就是完全展示内容所需要的高度
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            //按下
            case MotionEvent.ACTION_DOWN:
                lastY = (int) event.getRawY();
                break;
            //移动
            case MotionEvent.ACTION_MOVE:
                int y = (int) (event.getRawY());
                int dy = y - lastY;
                float diff = Math.abs(dy);
                if (diff < mTouchSlop) {
                    return super.onTouchEvent(event);
                }
                //ACTION_DOWN调用了startNestedScroll；ACTION_MOVE中调用了dispatchNestedPreScroll
                if (startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL, ViewCompat.TYPE_TOUCH)
                        && dispatchNestedPreScroll(0, dy, consumed, offset, ViewCompat.TYPE_TOUCH)) {
                    //如果找到了支持嵌套滑动的父类,父类进行了一系列的滑动
                    //获取滑动距离
                    int remain = dy - consumed[1];
                    if (remain != 0) {
                        scrollBy(0, -remain);
                    }
                } else {
                    scrollBy(0, -dy);
                }
                break;
        }
        return true;
    }


    @Override
    public void scrollTo(int x, int y) {
        int maxY = getMeasuredHeight() - showHeight;
        if (y > maxY) {
            y = maxY;
        }
        if (y < 0) {
            y = 0;
        }
        super.scrollTo(x, y);
    }

    private void init() {
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mNestedScrollingChildHelper.setNestedScrollingEnabled(true);

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        // 获取TouchSlop值
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mNestedScrollingChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        mNestedScrollingChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mNestedScrollingChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow, int type) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                           int type) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    /**
     * 惯性滚动相关
     *
     * @param velocityX
     * @param velocityY
     * @param consumed
     * @return
     */
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
