package com.xueh.comm_core.helper.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * intent 构建工具类
 */
class IntentBuilder private constructor(private val context: Context) {
    private val intent: Intent
    /**
     * 添加启动标记
     */
    fun flag(flags: Int): IntentBuilder {
        intent.flags = flags
        return this
    }

    /**
     * 动作
     */
    fun action(action: String?): IntentBuilder {
        intent.action = action
        return this
    }

    fun params(bundle: Bundle): IntentBuilder {
        intent.putExtras(bundle)
        return this
    }

    /**
     * 参数添加
     */
    fun params(
        key: String?,
        value: String?
    ): IntentBuilder {
        intent.putExtra(key, value)
        return this
    }

    /**
     * 参数添加
     */
    fun params(
        key: String?,
        value: Int
    ): IntentBuilder {
        intent.putExtra(key, value)
        return this
    }

    /**
     * 参数添加
     */
    fun params(
        key: String?,
        value: Boolean
    ): IntentBuilder {
        intent.putExtra(key, value)
        return this
    }

    /**
     * 参数添加
     */
    fun params(
        key: String?,
        value: Serializable?
    ): IntentBuilder {
        intent.putExtra(key, value)
        return this
    }

    /**
     * 参数添加
     */
    fun params(
        key: String?,
        value: Parcelable?
    ): IntentBuilder {
        intent.putExtra(key, value)
        return this
    }

    /**
     * 设置类名
     */
    fun className(clazz: Class<*>): IntentBuilder {
        intent.setClass(context, clazz)
        return this
    }

    /**
     * 设置类名
     */
    fun className(className: String): IntentBuilder {
        try {
            className(Class.forName(className))
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return this
    }

    /**
     * 构建Intent
     */
    fun build(): Intent {
        return intent
    }

    /**
     * 启动Activity
     */
    fun start() {
        context.startActivity(build())
    }

    /**
     * 启动Activity带结果返回的
     */
    fun start(requestCode: Int) {
        (context as Activity).startActivityForResult(build(), requestCode)
    }

    companion object {
        @JvmStatic
        fun builder(context: Context): IntentBuilder {
            return IntentBuilder(context)
        }
    }

    init {
        intent = Intent()
    }
}