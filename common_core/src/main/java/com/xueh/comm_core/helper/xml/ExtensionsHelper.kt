package com.xueh.comm_core.helper.xml

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.blankj.utilcode.util.*
import com.xueh.comm_core.helper.coroutine.GlobalCoroutineExceptionHandler
import com.xueh.comm_core.helper.xml.GlideUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.jessyan.progressmanager.ProgressListener
import me.jessyan.progressmanager.ProgressManager
import me.jessyan.progressmanager.body.ProgressInfo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.properties.Delegates


/**
 * 创 建 人: xueh
 * 创建日期: 2019/8/16 17:41
 * 备注：
 */

//*****************************************常用方法***********************************************************
fun getDrawable(@DrawableRes id: Int) = ResourceUtils.getDrawable(id)

fun Float.px() = ConvertUtils.dp2px(this)

fun Float.dp() = ConvertUtils.px2dp(this)

fun View.click(time: Long = 800L, onclick: View.OnClickListener) {
    var clickTime by Delegates.observable(0L) { pre, old, new ->
        if (new - old >= time) {
            onclick.onClick(this)
        }
    }
    setOnClickListener {
        clickTime = System.currentTimeMillis()
    }
}

//*********************************************************************************************************



//*********************************************常用View扩展*****************************************************

fun View.setClick(block: (View) -> Unit) {
    ClickUtils.applyGlobalDebouncing(this, 1000) {
        block.invoke(it)
    }
}

fun ImageView.loadCircleImg(url: String, circular: Int = 0) {
    if (circular == 0) GlideUtils.loadCircleImg(this, url) else GlideUtils.loadCircleImg(
        this,
        url,
        circular
    )
}

fun ImageView.loadImg(url: String) {
    GlideUtils.loadImg(this, url)
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

/**
/**
 * 设置圆角 View
 */
fun View.setRoundBg(
    CornersRadius: Int, @ColorRes bgColor: Int, @ColorRes tvColor: Int = 0,
) {
    val drawable = DrawableCreator.Builder()
        .setCornersRadius(ConvertUtils.dp2px(CornersRadius.toFloat()).toFloat())
        .setSolidColor(CommonUtils.getColor(bgColor))
        .setStrokeColor(CommonUtils.getColor(bgColor))
        .build()
    this.background = drawable
    if (this is TextView && tvColor != 0) {
        this.setTextColor(CommonUtils.getColor(tvColor))
    }
}


/**
 * 设置圆线 View
 */
fun View.setRoundLineBg(
    radius: Int, @ColorRes SolidColor: Int, @ColorRes lineColor: Int,
    width: Int = 1, @ColorRes tvColor: Int = 0,
) {
    val drawable = DrawableCreator.Builder()
        .setCornersRadius(ConvertUtils.dp2px(radius.toFloat()).toFloat())
        .setStrokeColor(CommonUtils.getColor(lineColor))
        .setStrokeWidth(ConvertUtils.dp2px(width.toFloat()).toFloat())
        .setSolidColor(CommonUtils.getColor(SolidColor))
        .build()
    this.background = drawable
    if (this is TextView && tvColor != 0) {
        this.setTextColor(CommonUtils.getColor(tvColor))
    }
}

/**
 * 裁剪成圆角 View
 * radius 圆角
 */
fun View.clipRoundBg(radius: Float) {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            //圆角
            outline.setRoundRect(0, 0, view.width, view.height, radius.px().toFloat())
            //矩形
            //outline.setRect(Rect(0,0,view.width,view.width))
            //圆形
            //outline.setOval(0, 0, view.width, view.width)
        }
    }
}

**/
//*********************************************************************************************************



///////////////////////////////////////////////////////////////////////////////////////////////////////////////
class lifecycleEventDsl {
    var onResume: (() -> Unit)? = null

    var onDestroy: (() -> Unit)? = null

    infix fun onDestroy(onDestroy: (() -> Unit)) {
        this.onDestroy = onDestroy
    }

    infix fun onResume(onResume: (() -> Unit)) {
        this.onResume = onResume
    }
}

inline fun FragmentActivity.lifecycleEvent(crossinline lifecycleDsl: lifecycleEventDsl.() -> Unit) {
    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    lifecycleEventDsl().apply(lifecycleDsl)?.onResume?.invoke()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    lifecycle.removeObserver(this)
                    lifecycleEventDsl().apply(lifecycleDsl)?.onDestroy?.invoke()
                }
                else -> {

                }
            }
        }
    })
}

