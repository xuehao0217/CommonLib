package com.xueh.comm_core.base.mvp;


import androidx.lifecycle.LifecycleOwner;

import com.xueh.comm_core.base.ibase.IBaseModel;
import com.xueh.comm_core.base.ibase.IBasePresenter;
import com.xueh.comm_core.base.ibase.IBaseView;

/**
 * 类描述：Presenter模块的基类，持有对应的View模块对象
 * 创建人：xueh
 */
public abstract class MvpBasePresenter<V extends IBaseView, M extends IBaseModel>
        implements IBasePresenter {

    private V mView;

    private M mModel;

    public MvpBasePresenter(V v) {
        mView = v;
        mModel = createModel();
    }

    protected abstract M createModel();

    public V getView() {

        return mView;
    }

    public M getModel() {
        return mModel;
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        if (mModel != null) {
            mModel.onDestroy();
            this.mModel = null;
        }
        this.mView = null;
    }
}
