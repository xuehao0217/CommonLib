package com.xueh.comm_core.base.mvp.ibase;


import androidx.lifecycle.Lifecycle;

import io.reactivex.subjects.PublishSubject;

/**
 * 类描述：View模块的基类接口
 * 创建人：xueh
 * 创建时间：2016/7/6 10:18
 */
public interface IBaseMVPView {

    PublishSubject<Lifecycle.Event> getLifecycleSubject();

    void showLoading();

    void hideLoading();
}