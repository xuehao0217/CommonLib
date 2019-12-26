package com.xueh.comm_core.net;


import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 创建日期: 2017/7/4 19:19
 * 备注：
 */
public abstract class BaseObserver<T> implements Observer<BaseModel<T>> {

    private static final String TAG = "HTTP";

    protected BaseObserver() {
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(BaseModel<T> value) {
        T t;
        if (BaseModel.STATE_SUCCESS == value.getErr()) {
            t = value.getRet();
            if (t != null) {
                Logger.e(TAG, "onSuccess:" + t.toString());
            } else {

            }
            try {
                onSuccess(t);
            } catch (Throwable e) {
                Log.e(TAG, "error:" + "onSuccess" + ":" + e.getMessage());
            }
        } else {
            try {
                onException(value);
            } catch (Throwable e) {
                Log.e(TAG, "error:" + value);
            }
        }
    }


    @Override
    public void onComplete() {
        Logger.d(TAG, "onComplete");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError:" + e.toString());
    }

    protected abstract void onSuccess(T t);

    protected void onException(BaseModel model) {
        Log.e(TAG, "onException:" + model.toString());
    }
}
