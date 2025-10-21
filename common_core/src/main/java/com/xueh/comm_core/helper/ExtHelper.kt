package com.xueh.comm_core.helper

import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.Gravity
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jessyan.progressmanager.ProgressListener
import me.jessyan.progressmanager.ProgressManager
import me.jessyan.progressmanager.body.ProgressInfo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat

//*************************************************** Null 判断 ***************************************************

inline fun <A, B> notNull(a: A?, b: B?, block: (A, B) -> Unit) {
    if (a != null && b != null) block(a, b)
}

inline fun <A, B> notNullOrNull(
    a: A?,
    b: B?,
    block: (a: A, b: B) -> Unit,
    onNull: () -> Unit
) {
    if (a != null && b != null) block(a, b) else onNull()
}

inline fun notNull(vararg args: Any?, block: () -> Unit) {
    if (args.all { it != null }) block()
}

fun Any?.isEmpty() = ObjectUtils.isEmpty(this)
fun Any?.isNotEmpty() = ObjectUtils.isNotEmpty(this)


//*************************************************** 文件操作 ***************************************************

fun File.toMultipartPart(name: String = "file"): MultipartBody.Part =
    MultipartBody.Part.createFormData(
        name,
        this.name,
        this.asRequestBody("image/jpeg".toMediaTypeOrNull())
    )

fun File.getUri(): Uri =
    FileProvider.getUriForFile(
        Utils.getApp(),
        "${Utils.getApp().packageName}.utilcode.provider",
        this
    )

//*************************************************** 下载进度 ***************************************************

fun String.listenDownloadProgress(onProgress: (ProgressInfo) -> Unit) {
    ProgressManager.getInstance().addResponseListener(this, object : ProgressListener {
        override fun onProgress(progressInfo: ProgressInfo) = onProgress(progressInfo)
        override fun onError(id: Long, e: Exception?) {
            Log.e("ProgressManager", "Error: $e")
        }
    })
}

//*************************************************** Boolean 扩展 ***************************************************

sealed class BooleanExt<out T>
object Otherwise : BooleanExt<Nothing>()
class WithData<T>(val data: T) : BooleanExt<T>()

inline fun <T> Boolean.yes(block: () -> T): BooleanExt<T> = if (this) WithData(block()) else Otherwise
inline fun <T> Boolean.no(block: () -> T): BooleanExt<T> = if (!this) WithData(block()) else Otherwise

inline fun <T> BooleanExt<T>.otherwise(block: () -> T): T = when (this) {
    is Otherwise -> block()
    is WithData -> this.data
}

//*************************************************** 协程扩展 ***************************************************

inline fun <T> Flow<T>.collectIn(scope: CoroutineScope, crossinline action: suspend (T) -> Unit) {
    scope.launch { collect { action(it) } }
}

fun <T> launchIO(
    blockIO: suspend CoroutineScope.() -> T,
    onMain: suspend CoroutineScope.(T) -> Unit,
    onError: (Exception) -> Unit = {},
    onFinally: () -> Unit = {}
) {
    val scope = CoroutineScope(Dispatchers.Main)
    scope.launch {
        try {
            val result = withContext(Dispatchers.IO) { blockIO() }
            onMain(result)
        } catch (e: Exception) {
            onError(e)
            Log.e("launchIO", "Exception", e)
        } finally {
            scope.cancel()
            onFinally()
        }
    }
}

//*************************************************** 时间格式 ***************************************************

fun Long.toYMD() = TimeUtils.millis2String(this, SimpleDateFormat("yyyy-MM-dd"))
fun Long.toHM() = TimeUtils.millis2String(this, SimpleDateFormat("HH:mm"))
fun nowYMD() = TimeUtils.getNowMills().toYMD()

//*************************************************** Toast ***************************************************

fun showToast(msg: String, longDuration: Boolean = false) {
    ThreadUtils.runOnUiThread {
        ToastUtils.make()
            .setBgColor(Color.parseColor("#CC000000"))
            .setTextColor(Color.WHITE)
            .setGravity(Gravity.CENTER, 0, 0)
            .setTextSize(16)
            .setDurationIsLong(longDuration)
            .show(msg)
    }
}
