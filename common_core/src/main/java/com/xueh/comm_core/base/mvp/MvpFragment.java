package com.xueh.comm_core.base.mvp;//package com.example.library.mvp;


import android.content.Context;

import androidx.lifecycle.Lifecycle;

import com.xueh.comm_core.base.DFragment;
import com.xueh.comm_core.base.ibase.IBasePresenter;
import com.xueh.comm_core.base.ibase.IBaseView;

import io.reactivex.subjects.PublishSubject;

/**
 * 类描述：MVP架构下的Fragment基类
 * 创建人：xueh
 */
public abstract class MvpFragment<P extends IBasePresenter> extends DFragment implements IBaseView {

    protected P mPresenter;
    protected final PublishSubject<Lifecycle.Event> lifecycleSubject = PublishSubject.create();
    protected abstract P createPresenter(IBaseView view);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = createPresenter(this);
        getLifecycle().addObserver(mPresenter);//添加LifecycleObserver
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getLifecycle().removeObserver(mPresenter);
        if (null != mPresenter) {
            mPresenter.onDestroy(this);
        }
    }

    @Override
    public PublishSubject<Lifecycle.Event> getLifecycleSubject() {
        return lifecycleSubject;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public P getPresenter() {
        return mPresenter;
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

