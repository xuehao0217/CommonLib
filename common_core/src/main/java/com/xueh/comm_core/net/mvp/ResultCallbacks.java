package com.xueh.comm_core.net.mvp;

import com.xueh.comm_core.net.BaseResult;

/************************************************************
 * 创建日: 2017/7/18
 * 备  注： Model获取数据后的回调
 ************************************************************/
public interface ResultCallbacks<T>{
    void onSuccess(T data);
    void onException(BaseResult model);
    void onError(Throwable e);
}
