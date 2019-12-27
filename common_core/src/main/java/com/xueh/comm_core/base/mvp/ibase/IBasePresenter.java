package com.xueh.comm_core.base.mvp.ibase;


import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import org.greenrobot.greendao.annotation.NotNull;

/**
 * 类描述：Presenter模块的基类接口
 * 创建人：xueh
 * 创建时间：2016/7/6 10:18
 */
public interface IBasePresenter extends LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(@NotNull LifecycleOwner owner);
}