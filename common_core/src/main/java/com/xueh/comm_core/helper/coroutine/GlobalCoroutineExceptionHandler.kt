package com.xueh.comm_core.helper.coroutine

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.coroutines.CoroutineContext

/**
 * 创 建 人: xueh
 * 创建日期: 2021/12/7 16:31
 * 备注：
 */
class GlobalCoroutineExceptionHandler : CoroutineExceptionHandler {
    override val key: CoroutineContext.Key<*> = CoroutineExceptionHandler
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        println("Coroutine exception: $exception")
        exception.printStackTrace()
        ToastUtils.showShort("$exception")
        LogUtils.eTag(
            "GlobalCoroutineExceptionHandler",
            "Coroutine exception: ${getStackTrace(exception)}"
        )
    }

    fun getStackTrace(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        return try {
            throwable.printStackTrace(pw)
            sw.toString()
        } finally {
            pw.close()
            sw.close()
        }
    }
}