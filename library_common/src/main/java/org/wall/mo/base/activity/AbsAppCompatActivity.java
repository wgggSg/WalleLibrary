package org.wall.mo.base.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import org.wall.mo.base.interfaces.IAttachActivity;
import org.wall.mo.utils.BuildConfig;
import org.wall.mo.utils.ClickUtil;
import org.wall.mo.utils.StringUtils;
import org.wall.mo.utils.keyboard.KeyboardUtils;
import org.wall.mo.utils.log.WLog;
import org.wall.mo.utils.network.NetStateChangeObserver;
import org.wall.mo.utils.network.NetStateChangeReceiver;
import org.wall.mo.utils.network.NetworkType;

/**
 * Copyright (C), 2018-2019
 * Author: ziqimo
 * Date: 2019/5/30 上午9:53
 * Description:
 * <p>
 * 后面尽力考虑用AbsDataBindingAppCompatActivity类来实现逻辑
 * 后面尽力考虑用AbsDataBindingAppCompatActivity类来实现逻辑
 * 后面尽力考虑用AbsDataBindingAppCompatActivity类来实现逻辑
 * <p>
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
@Deprecated
public abstract class AbsAppCompatActivity extends AppCompatActivity
        implements IAttachActivity,
        View.OnClickListener,
        NetStateChangeObserver {

    protected final static String TAG = AbsAppCompatActivity.class.getSimpleName();

    protected Handler mHandler = null;

    private NetStateChangeReceiver mNetStateChangeReceiver;

    static {
        /**
         * https://www.jianshu.com/p/0972a0d290e9
         */
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onCreate savedInstanceState is " + StringUtils.isNULL(savedInstanceState));
        }
        mNetStateChangeReceiver = new NetStateChangeReceiver();
        mNetStateChangeReceiver.setNetStateChangeObserver(this);
        NetStateChangeReceiver.registerReceiver(this, mNetStateChangeReceiver);
        //创建一个handler
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    handleSubMessage(msg);
                }
            };
        }
        int layoutId = getLayoutId();
        if (layoutId != 0) {
            setContentView(layoutId);
        }
        initView(savedInstanceState);
        parseIntentData();
        initData();
        initClick();
    }

    @Override
    public void showTopBar(boolean status) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onRestart");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onStart");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onResume");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onPause");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onStop");
        }
    }

    @Override
    protected void onDestroy() {
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onDestroy");
        }
        NetStateChangeReceiver.unRegisterReceiver(this, mNetStateChangeReceiver);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        KeyboardUtils.hideKeyboard(this);
        super.onBackPressed();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onAttachFragment fragment is " + (fragment != null ? fragment.getClass().getSimpleName() : "--"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onSaveInstanceState outState is " + StringUtils.isNULL(outState));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onRestoreInstanceState savedInstanceState is " + StringUtils.isNULL(savedInstanceState));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onActivityResult");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onRequestPermissionsResult");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onConfigurationChanged");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onNewIntent");
        }
        parseIntentData();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + ".onLowMemory");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // ----------事件分发-------->-------事件处理--------->
        // activity -> 父view -> 子view -> 父view -> activity
        // 在activity的事件分发处对用户高频率点击进行判断拦截，避免用户拿app消遣
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 界面上多个控件都需要添加防抖操作
            if (!onFastDoubleClickEnable() && ClickUtil.isFastDoubleClick()) {
                if (BuildConfig.DEBUG) {
                    WLog.i(TAG, getName() + "dispatchTouchEvent 不允许（不启用）快速点击");
                }
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    protected boolean onFastDoubleClickEnable() {
        return true;
    }

    //控制startactivity 参考这个哥们https://www.jianshu.com/p/579f1f118161
    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + "》》》startActivityForResult -- 开始 --");
        }
        if (startActivitySelfCheck(intent)) {
            if (BuildConfig.DEBUG) {
                WLog.i(TAG, getName() + "》》》startActivityForResult -- 满足 --");
            }
            // 查看源码得知 startActivity 最终也会调用 startActivityForResult
            super.startActivityForResult(intent, requestCode, options);
        }
    }

    private String mActivityJumpTag;

    private long mActivityJumpTime;

    /**
     * 检查当前 Activity 是否重复跳转了，不需要检查则重写此方法并返回 true 即可
     *
     * @param intent 用于跳转的 Intent 对象
     * @return 检查通过返回true, 检查不通过返回false
     */
    protected boolean startActivitySelfCheck(Intent intent) {
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + "》》》startActivitySelfCheck -- 开始 --");
        }
        // 默认检查通过
        boolean result = true;
        if (BuildConfig.DEBUG) {
            WLog.i(TAG, getName() + "》》》startActivitySelfCheck -- intent --" + (intent == null));
        }
        if (intent == null) {
            return result;
        }
        // 标记对象
        String tag;
        if (intent.getComponent() != null) { // 显式跳转
            tag = intent.getComponent().getClassName();
            if (BuildConfig.DEBUG) {
                WLog.i(TAG, getName() + "》》》startActivitySelfCheck -- 显式跳转 --" + tag);
            }
        } else if (intent.getAction() != null) { // 隐式跳转
            tag = intent.getAction();
            if (BuildConfig.DEBUG) {
                WLog.i(TAG, getName() + "》》》startActivitySelfCheck -- 隐式跳转 --" + tag);
            }
        } else {
            return result;
        }
        if (tag.equals(mActivityJumpTag) && (mActivityJumpTime >= SystemClock.uptimeMillis() - 500)) {
            // 检查不通过
            result = false;
            if (BuildConfig.DEBUG) {
                WLog.i(TAG, getName() + "》》》startActivitySelfCheck -- result --" + result);
            }
        }
        // 记录启动标记和时间
        mActivityJumpTag = tag;
        mActivityJumpTime = SystemClock.uptimeMillis();
        return result;
    }


    public String getName() {
        return getClass().getSimpleName();
    }


    public abstract int getLayoutId();

    public abstract void initView(Bundle savedInstanceState);

    public abstract void parseIntentData();

    /**
     * 废弃这2个方法
     */
    @Deprecated
    public void initData() {

    }

    /**
     * 废弃这2个方法
     */
    @Deprecated
    public void initClick() {

    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        WLog.i(TAG, getName() + ".onNetConnected.networkType:" + networkType);
    }

    @Override
    public void onNetDisconnected() {
        WLog.i(TAG, getName() + ".onNetDisconnected");
    }

    public abstract void handleSubMessage(Message msg);
}
