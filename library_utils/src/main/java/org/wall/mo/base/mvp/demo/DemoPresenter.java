package org.wall.mo.base.mvp.demo;

import java.lang.ref.WeakReference;

/**
 * Copyright (C), 2018-2019
 * Author: ziqimo
 * Date: 2019-08-18 15:34
 * Description: ${DESCRIPTION}
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class DemoPresenter extends DemoContract.Presenter {

    @Override
    public void postMsg() {
        if (getView() != null) {
            getView().onRequestStart(1, "");
        }
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onPause() {

    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}