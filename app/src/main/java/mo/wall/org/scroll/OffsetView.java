package mo.wall.org.scroll;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * 作者 : moziqi
 * 邮箱 : 709847739@qq.com
 * 时间   : 2019/7/23-18:04
 * desc   : 利用offsetLeftAndRight/offsetLeftAndRight 实现view的滑动
 * version: 1.0
 */
public class OffsetView extends View {

    private float lastY;
    private float lastX;

    private int height;
    private int width;

    public OffsetView(Context context) {
        super(context);
        initView();
    }

    public OffsetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public OffsetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public OffsetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
//        MeasureSpec.makeMeasureSpec()
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        height = dm.heightPixels;
        width = dm.widthPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取当前点击
        float y = event.getY();
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                lastX = x;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) (x - lastX);
                int offsetY = (int) (y - lastY);
                int left = getLeft() + offsetX;
                //int right = getRight() + offsetX;
                //Log.i("mo", String.format("left is %d", left));
                if (left < 0 || left > width - getMeasuredWidth()) {
                    break;
                }
                offsetLeftAndRight(offsetX);
                offsetLeftAndRight(offsetY);
                break;
            default:
                break;
        }
        return true;
    }
}
