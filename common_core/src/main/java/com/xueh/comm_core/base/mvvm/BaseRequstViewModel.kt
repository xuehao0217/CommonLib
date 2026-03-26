package com.xueh.comm_core.base.mvvm

/**
 * 带 **Retrofit 接口类型 [E]** 的 ViewModel：首次访问 [api] 时调用 [initApi] 懒创建，避免在 `init` 阶段依赖未就绪的 [HttpRequest]。
 */
abstract class BaseRequstViewModel<E> : BaseViewModel() {
    protected val api by lazy {
        initApi()
    }

    protected abstract fun initApi(): E
}