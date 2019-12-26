package com.xueh.comm_core.net;

/**
 * 创 建 人: xueh
 * 创建日期: 2019/2/18 17:20
 * 备注：
 */
public class BaseModel<T> {

    public static final int STATE_SUCCESS = 0;
    public static final int STATE_FAILURE = 1;
    public static final int STATE_EXCEPTION = -1;

    public int err;
    public String msg;
    public T ret;

    @Override
    public String toString() {
        return "BaseModel{" +
                "err=" + err +
                ", msg='" + msg + '\'' +
                ", ret=" + ret +
                '}';
    }

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public String getMsg() {
        return msg == null ? "" : msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getRet() {
        return ret;
    }

    public void setRet(T ret) {
        this.ret = ret;
    }

}