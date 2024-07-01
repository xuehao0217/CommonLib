package com.xueh.comm_core.helper

import androidx.core.content.FileProvider
import com.blankj.utilcode.util.*
import com.xueh.comm_core.helper.coroutine.GlobalCoroutineExceptionHandler
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


/**
 * 创 建 人: xueh
 * 创建日期: 2019/8/16 17:41
 * 备注：
 */


//*****************************************常用判空***********************************************************

/***
 *  notNull(name,age){ name,age->
 *         doSth(name,age)
 *  }
 */

inline fun <A, B> notNull(a: A?, b: B?, block: (A, B) -> Unit) {
    if (ObjectUtils.isNotEmpty(a) && ObjectUtils.isNotEmpty(b)) {
        block(a!!, b!!)
    }
}


/**
 *  notNullOrNull(a, b) {
 *      notNull { s, s2 ->
 *          println("不为空")
 *      }
 *      isNull {
 *          println("是空")
 *      }
 *  }
 */

inline fun <A, B> notNullOrNull(a: A?, b: B?, notnull: notNullDsl<A, B>.() -> Unit) {
    if (ObjectUtils.isNotEmpty(a) && ObjectUtils.isNotEmpty(b)) {
        notNullDsl<A, B>().apply(notnull).notNull?.invoke(a!!, b!!)
    } else {
        notNullDsl<A, B>().apply(notnull).isNull?.invoke()
    }
}


class notNullDsl<A, B> {
    var notNull: ((A, B) -> Unit?)? = null

    var isNull: (() -> Unit?)? = null

    infix fun notNull(notNull: ((A, B) -> Unit?)) {
        this.notNull = notNull
    }

    infix fun isNull(onNull: (() -> Unit?)) {
        this.isNull = onNull
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



fun Any?.isEmpty()=ObjectUtils.isEmpty(this)

fun Any?.isNotEmpty()=ObjectUtils.isNotEmpty(this)


//*******************************************网络请求*********************************************************
//转换
fun File.getPart() = MultipartBody.Part.createFormData(
    "file",
    this.absolutePath,
    this.asRequestBody("image/jpeg".toMediaTypeOrNull())
)

//FileProvider 适配
fun File.getUriForFile() = FileProvider.getUriForFile(
    Utils.getApp(),
    Utils.getApp().packageName + ".utilcode.provider",
    this
)

//获取URL下载进度
fun String.getDownloadProgress(block: (ProgressInfo) -> Unit) = ProgressManager.getInstance()
    .addResponseListener(this, object :
        ProgressListener {
        override fun onProgress(progressInfo: ProgressInfo) {
            block.invoke(progressInfo)
        }

        override fun onError(id: Long, e: Exception?) {
        }

    })
//***********************************************kotlin 扩展函数之Boolean扩展**********************************************************
//https://juejin.cn/post/6952690897234591780
/**
 * 数据
 */
sealed class BooleanExt<out T>

object Otherwise : BooleanExt<Nothing>()
class WithData<T>(val data: T) : BooleanExt<T>()

/**
 * 判断条件为true 时执行block
 */
inline fun <T : Any> Boolean.yes(block: () -> T) =
    when {
        this -> {
            WithData(block())
        }
        else -> {
            Otherwise
        }
    }

/**
 * 判断条件为false 时执行block
 *
 */
inline fun <T> Boolean.no(block: () -> T) = when {
    this -> Otherwise
    else -> {
        WithData(block())
    }
}

//// 有返回值（条件为false）
//val otherwise1 = false.no {
//    2
//}.otherwise {
//    3
//}

/**
 * 与判断条件互斥时执行block
 */
inline fun <T> BooleanExt<T>.otherwise(block: () -> T): T =
    when (this) {
        is Otherwise -> block()
        is WithData -> this.data
    }
//*********************************************************************************************************

//class yesOrNoDsl {
//    var isTrue: (() -> Unit?)? = null
//
//    var isFalse: (() -> Unit?)? = null
//
//    infix fun yes(isTrue: (() -> Unit?)) {
//        this.isTrue = isTrue
//    }
//
//    infix fun no(isFalse: (() -> Unit?)) {
//        this.isFalse = isFalse
//    }
//}
//
//
//inline fun yesOrNo(a: Boolean, crossinline yesOrNo: yesOrNoDsl.() -> Unit) {
//    a.yes {
//        yesOrNoDsl().apply(yesOrNo)?.isTrue?.invoke()
//    }
//    a.no {
//        yesOrNoDsl().apply(yesOrNo)?.isFalse?.invoke()
//    }
//}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////


inline fun <T> kotlinx.coroutines.flow.Flow<T>.collect(scope: CoroutineScope, crossinline action: suspend (T) -> Unit) {
    scope.launch(GlobalCoroutineExceptionHandler()) {
        collect {
            action(it)
        }
    }
}


fun launchMainAndCancel(error: () -> Unit={},block: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.Main).launch {
    try {
        block()
    } catch (e: Exception) {
        error()
    } finally {
        cancel()
    }
}
