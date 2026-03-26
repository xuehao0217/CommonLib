package com.xueh.comm_core.net.helper

import androidx.lifecycle.MutableLiveData

/**
 * 带 **状态机** 的 [MutableLiveData]：除数据本身外，通过 [state] 投递 [State.Idle] / [State.Loading] / [State.Success] / [State.Error]，
 * 便于 XML 或单向数据流区分首屏、加载中与错误。
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
