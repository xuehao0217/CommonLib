package com.xueh.comm_core.net.mvp;


import android.util.Log;

import com.xueh.comm_core.net.BaseResult;
import com.xueh.comm_core.net.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 创建日期: 2017/7/4 19:19
 * 备注：
 */
public abstract class BaseObserver<T> implements Observer<BaseResult<T>> {

    private static final String TAG = "HTTP";

    protected BaseObserver() {
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(BaseResult<T> value) {
        T t;
        if (BaseResult.STATE_SUCCESS == value.getErr()) {
            t = value.getData();
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

    protected void onException(BaseResult model) {
        Log.e(TAG, "onException:" + model.toString());
    }
}
