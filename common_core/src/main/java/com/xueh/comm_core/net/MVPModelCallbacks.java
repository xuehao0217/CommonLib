package com.xueh.comm_core.net;

/************************************************************
 * 创建日: 2017/7/18
 * 备  注： Model获取数据后的回调
 ************************************************************/
public interface MVPModelCallbacks <T>{
    void onSuccess(T data);
    void onException(BaseModel model);
    void onError(Throwable e);
}
