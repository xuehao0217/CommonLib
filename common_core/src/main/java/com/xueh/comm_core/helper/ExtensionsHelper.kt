package com.xueh.comm_core.helper

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.Utils
import com.noober.background.drawable.DrawableCreator
import com.xueh.comm_core.utils.CommonUtils
import com.xueh.comm_core.utils.GlideUtils


/**
 * 创 建 人: xueh
 * 创建日期: 2019/8/16 17:41
 * 备注：
 */
fun getColor(colorRes: Int) = CommonUtils.getColor(colorRes)

fun getDrawable(@DrawableRes id: Int) = CommonUtils.getDrawable(id)

fun hasNetWorkConection() = NetworkUtils.isConnected()

fun getApp() = Utils.getApp()

fun ImageView.loadCircleImg(url: String, circular: Int = 0) {
    if (circular == 0) GlideUtils.loadCircleImg(this, url) else GlideUtils.loadCircleImg(
        this,
        url,
        circular
    )
}

fun ImageView.loadImg(url: String) {
    GlideUtils.loadImg(this, url)
    setVisible()
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun loge(tag: String, content: String) {
    Log.e(tag, content)
}


/**
 * 设置圆角 View
 */
fun View.setRoundBg(
    CornersRadius: Int, @ColorRes bgColor: Int, @ColorRes tvCollor: Int = 0
) {
    val drawable = DrawableCreator.Builder()
        .setCornersRadius(ConvertUtils.dp2px(CornersRadius.toFloat()).toFloat())
        .setSolidColor(CommonUtils.getColor(bgColor))
        .setStrokeColor(CommonUtils.getColor(bgColor))
        .build()
    this.background = drawable
    if (this is TextView) {
        this.setTextColor(CommonUtils.getColor(tvCollor))
    }
}


/**
 * 设置圆线 View
 */
fun View.setRoundLineBg(
    radius: Int, @ColorRes SolidColor: Int, @ColorRes lineColor: Int,
    width: Int = 1, @ColorRes tvColor: Int = 0
) {
    val drawable = DrawableCreator.Builder()
        .setCornersRadius(ConvertUtils.dp2px(radius.toFloat()).toFloat())
        .setStrokeColor(CommonUtils.getColor(lineColor))
        .setStrokeWidth(ConvertUtils.dp2px(width.toFloat()).toFloat())
        .setSolidColor(CommonUtils.getColor(SolidColor))
        .build()
    this.background = drawable
    if (this is TextView) {
        this.setTextColor(CommonUtils.getColor(tvColor))
    }
}


/***
 *  notNull(name,age){ name,age->
 *         doSth(name,age)
 *  }
 */
inline fun <A, B, R> notNull(a: A?, b: B?, block: (A, B) -> R) {
    if (a != null && b != null) {
        block(a, b)
    }
}

/***
 *  notNull(name,age){
 *         doSth(name,age)
 *  }
 */
inline fun <R> notNull(vararg args: Any?, block: () -> R) {
    when {
        args.filterNotNull().size == args.size -> block()
    }
}


sealed class BooleanExt<out T> constructor(val boolean: Boolean)
object Otherwise : BooleanExt<Nothing>(true)
class WithData<out T>(val data: T) : BooleanExt<T>(false)

inline fun <T> Boolean.yes(block: () -> T): BooleanExt<T> = when {
    this -> {
        WithData(block())
    }
    else -> Otherwise
}

inline fun <T> Boolean.no(block: () -> T) = when {
    this -> Otherwise
    else -> {
        WithData(block())
    }
}

inline infix fun <T> BooleanExt<T>.otherwise(block: () -> T): T {
    return when (this) {
        is Otherwise -> block()
        is WithData<T> -> this.data
        else -> {
            throw IllegalAccessException()
        }
    }
}

inline operator fun <T> Boolean.invoke(block: () -> T) = yes(block)
