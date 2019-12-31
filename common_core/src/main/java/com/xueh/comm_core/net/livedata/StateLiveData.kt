package com.xueh.comm_core.net.livedata

import androidx.lifecycle.MutableLiveData

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 11:42
 * 备注：
 */
class StateLiveData<T> : MutableLiveData<T>() {

    enum class State {
        Idle, Loading, Success,Error
    }
    val state = MutableLiveData<State>()

    init {
        clearState()
    }

    fun postValueAndSuccess(value: T) {
        super.postValue(value)
        postSuccess()
    }

    fun clearState() {
        state.postValue(State.Idle)
    }

    fun postLoading() {
        state.postValue(State.Loading)
    }

    fun postSuccess() {
        state.postValue(State.Success)
    }

    fun postError() {
        state.postValue(State.Error)
    }

    fun changeState(s: State) {
        state.postValue(s)
    }
}
