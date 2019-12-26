package com.xueh.comm_core.base.mvp;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

import com.xueh.comm_core.base.DActivity;
import com.xueh.comm_core.base.ibase.IBasePresenter;
import com.xueh.comm_core.base.ibase.IBaseView;

import io.reactivex.subjects.PublishSubject;


/**
 * 类描述：MVP架构下的Activity基类
 * 创建人：xueh
 */
public abstract class MvpActivity<P extends IBasePresenter> extends DActivity implements IBaseView {
    public final PublishSubject<Lifecycle.Event> lifecycleSubject = PublishSubject.create();
    public P getPresenter() {
        return mPresenter;
    }

    protected P mPresenter;

    protected abstract P createPresenter(IBaseView view);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = createPresenter(this);
        getLifecycle().addObserver(mPresenter);
        super.onCreate(savedInstanceState);

    }
    @Override
    protected void onDestroy() {
        //避免Presenter持有该对象，先销毁Presenter
        lifecycleSubject.onNext(Lifecycle.Event.ON_DESTROY);
        if (null != mPresenter) {
            mPresenter.onDestroy(this);
        }
        super.onDestroy();
        getLifecycle().removeObserver(mPresenter);
    }

    @Override
    public PublishSubject<Lifecycle.Event> getLifecycleSubject() {
        return lifecycleSubject;
    }

    @Override
    public void showLoading() {
        showProgressDialog();
    }
    @Override
    public void hideLoading() {
        hideProgressDialog();
    }
}