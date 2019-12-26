package com.xueh.comm_core.net;


import androidx.lifecycle.Lifecycle;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * 创 建 人: xueh
 * 创建日期: 2019/2/18 17:20
 * 备注：
 */
public class RxSchedulers {

    public static <T> ObservableTransformer<T, T> compose(final PublishSubject<Lifecycle.Event> lifecycleSubject) {

        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {

                Observable<Lifecycle.Event> compareLifecycleObservable =
                        lifecycleSubject.filter(new Predicate<Lifecycle.Event>() {
                            @Override
                            public boolean test(Lifecycle.Event activityLifeCycleEvent) {
                                return activityLifeCycleEvent.equals(Lifecycle.Event.ON_DESTROY);
                            }
                        });
                return observable
                                .takeUntil(compareLifecycleObservable)
                                .subscribeOn(Schedulers.io())
                                .unsubscribeOn(Schedulers.io())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread());

            }
        };

    }

    public static <T> ObservableTransformer<T, T> compose() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
//                                if (!Utils.isNetworkConnected()) {
//                                    Toast.makeText(context,"网络异常", Toast.LENGTH_SHORT).show();
//                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
