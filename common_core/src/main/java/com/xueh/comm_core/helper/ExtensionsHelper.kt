package com.xueh.comm_core.helper

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.*
import com.noober.background.drawable.DrawableCreator
import com.xueh.comm_core.helper.activityresult.*
import com.xueh.comm_core.utils.CommonUtils
import com.xueh.comm_core.utils.GlideUtils
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

//*****************************************常用方法***********************************************************
fun getColor(colorRes: Int) = CommonUtils.getColor(colorRes)

fun getDrawable(@DrawableRes id: Int) = ResourceUtils.getDrawable(id)

fun hasNetWorkConection() = NetworkUtils.isConnected()


fun loge(tag: String, content: String) {
    Log.e(tag, content)
}

fun Float.dp2px() = ConvertUtils.dp2px(this)

fun Float.px2dp() = ConvertUtils.px2dp(this)

//*********************************************************************************************************


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


fun Any.isNotEmpty() = ObjectUtils.isNotEmpty(this)

//**********************************************************************************************************


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
 * 设置圆角 View
 */
fun View.setRoundBg(
    CornersRadius: Int, @ColorRes bgColor: Int, @ColorRes tvColor: Int = 0
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
    width: Int = 1, @ColorRes tvColor: Int = 0
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

//*********************************************************************************************************


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
//*********************************************************************************************************


//****************************************************ActivityResultAPI相关**************************************************

// setResult(IntentBuilder.builder().params("key1","key1").params("key2","key3"))
fun ComponentActivity.setResult(intent: IntentBuilder, isFinish: Boolean = true) {
    setResult(Activity.RESULT_OK, intent.build())
    if (isFinish) {
        this.finish()
    }
}

//StartActivityForResult  block 返回Intent
fun ComponentActivity.startActivityForResult(
    activity: Class<out Activity>,
    block: (Intent) -> Unit
) {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            Activity.RESULT_OK -> {
                it.data?.let {
                    block.invoke(it)
                }
            }
        }
    }.launch(Intent(this, activity))
}


//用于请求单个权限  Manifest.permission.CAMERA, block 返回是否授权
fun ComponentActivity.requestPermission(permission: String, block: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestPermission(), block).launch(
        permission
    )
}

// 用于请求一组权限 block 返回是否全部授权
// arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        )
fun ComponentActivity.requestMultiplePermissions(
    vararg permissions: String,
    block: (Boolean) -> Unit
) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        block.invoke(it.filter { it.value == false }.isEmpty())
    }.launch(
        permissions
    )
}


//调用拍照，返回值为Bitmap图片
fun ComponentActivity.takePicturePreview(block: (Bitmap) -> Unit) {
    requestMultiplePermissions(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) {
        it.yes {
            //权限全部通过
            registerForActivityResult(ActivityResultContracts.TakePicturePreview(), block).launch(
                null
            )
        }
    }
}


//裁剪图片 block返回图片URI
fun ComponentActivity.cropImageActivityResult(
    uri: Uri?,
    block: (Uri) -> Unit
) {
    uri?.let {
        registerForActivityResult(CropImageActivityResult()) { cropImage ->
            block.invoke(cropImage)
        }.launch(
            CropImageResult(it)
        )
    }

}

//调用拍照，并将图片保存到给定的Uri地址，block返回图片URI
fun ComponentActivity.takePicture(
    isCrop: Boolean = true,
    imgName: String = "pictureSaveImg.${Bitmap.CompressFormat.PNG}",
    block: (Uri) -> Unit
) {
    requestMultiplePermissions(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) {
        it.yes {
            var uri = File(PathUtils.getExternalAppCachePath(), imgName).getUriForFile()
            //权限全部通过
            registerForActivityResult(ActivityResultContracts.TakePicture()) {
                //返回true表示保存成功
                it.yes {
                    isCrop.yes {
                        cropImageActivityResult(uri) { cropUri ->
                            block.invoke(cropUri)
                        }
                    }
                    isCrop.no {
                        block.invoke(uri)
                    }
                }
            }.launch(
                uri
            )
        }
    }
}


//调用图库 isCrop  是否裁剪  block返回图片URI
fun ComponentActivity.startTakeWayByAlbum(
    isCrop: Boolean = true,
    block: (Uri) -> Unit
) {
    registerForActivityResult(AlbumActivityResul()) { uri ->
        uri?.isNotEmpty()?.yes {
            isCrop.yes {
                cropImageActivityResult(uri) { cropImage ->
                    block.invoke(cropImage)
                }
            }
            isCrop.no {
                block.invoke(uri)
            }
        }
    }.launch(
        null
    )
}


//*********************************************************************************************************


fun Boolean.yes(block: () -> Unit) {
    if (this) {
        block.invoke()
    }
}

fun Boolean.no(block: () -> Unit) {
    if (!this) {
        block.invoke()
    }
}

class yesOrNoDsl {
    var isTrue: (() -> Unit?)? = null

    var isFalse: (() -> Unit?)? = null

    infix fun yes(isTrue: (() -> Unit?)) {
        this.isTrue = isTrue
    }

    infix fun no(isFalse: (() -> Unit?)) {
        this.isFalse = isFalse
    }
}


inline fun yesOrNo(a: Boolean, crossinline yesOrNo: yesOrNoDsl.() -> Unit) {
    a.yes {
        yesOrNoDsl().apply(yesOrNo)?.isTrue?.invoke()
    }
    a.no {
        yesOrNoDsl().apply(yesOrNo)?.isFalse?.invoke()
    }
}
