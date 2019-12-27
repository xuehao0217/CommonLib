package com.xueh.comm_core.base.mvp;


import androidx.lifecycle.Lifecycle;

import com.blankj.utilcode.util.ActivityUtils;
import com.xueh.comm_core.base.mvp.ibase.IBaseModel;
import com.xueh.comm_core.net.BaseModel;
import com.xueh.comm_core.net.mvp.BaseObserver;
import com.xueh.comm_core.net.ModelCallbacks;
import com.xueh.comm_core.net.mvp.RxSchedulers;
import com.xueh.comm_core.weight.ViewLoading;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * 类描述：
 * 创建人：xueh
 */
public abstract class MvpBaseModel<E> implements IBaseModel {

    private E api;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public MvpBaseModel() {
        api = initApi();
    }

    protected abstract E initApi();

    protected E getApi() {
        return api;
    }

    protected <T> void deploy(Observable mObservable, PublishSubject<Lifecycle.Event> mPublishSubject, final ModelCallbacks<T> modelCallbacks) {
        deploy(mObservable, mPublishSubject, modelCallbacks, true);
    }

    protected <T> void deploy(Observable mObservable, PublishSubject<Lifecycle.Event> mPublishSubject, final ModelCallbacks<T> modelCallbacks, final boolean showLoading) {
        mObservable
                .compose(RxSchedulers.<BaseModel<T>>compose(mPublishSubject))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (showLoading) {
                            ViewLoading.show(ActivityUtils.getTopActivity());
                        }
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (showLoading) {
                            ViewLoading.dismiss(ActivityUtils.getTopActivity());
                        }
                    }
                })
                .subscribe(new BaseObserver<T>() {
                    @Override
                    protected void onSuccess(T mT) {
                        modelCallbacks.onSuccess(mT);
                    }

                    @Override
                    protected void onException(BaseModel model) {
                        super.onException(model);
                        modelCallbacks.onException(model);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        modelCallbacks.onError(e);
                    }


                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                        super.onSubscribe(d);
                    }
                });
    }

    @Override
    public void onDestroy() {
        if (null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.clear();
        }
    }

}
