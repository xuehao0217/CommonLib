package com.xueh.comm_core.helper.activityresult

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.blankj.utilcode.util.PathUtils
import com.xueh.comm_core.helper.getUriForFile
import com.xueh.comm_core.helper.isNotEmpty
import com.xueh.comm_core.helper.no
import com.xueh.comm_core.helper.yes
import java.io.File

/**
 * 创 建 人: xueh
 * 创建日期: 2021/7/5 12:17
 * 备注：
 */


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
fun ComponentActivity.takeAlbum(
    isCrop: Boolean = true,
    block: (Uri) -> Unit
) {
    registerForActivityResult(object : ActivityResultContract<Void, Uri>() {
        override fun createIntent(context: Context, input: Void?) =
            Intent(Intent.ACTION_PICK, null).apply {
                setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
            }

        override fun parseResult(resultCode: Int, intent: Intent?) =
            if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }) { uri ->
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
