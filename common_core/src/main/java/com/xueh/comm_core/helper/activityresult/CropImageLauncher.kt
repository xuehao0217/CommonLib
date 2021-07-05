package com.xueh.comm_core.helper.activityresult

import android.net.Uri

/**
 * 创 建 人: xueh
 * 创建日期: 2021/7/5 11:07
 * 备注：裁剪图片
 */
class CropImageLauncher :
    BaseLauncher<CropImageResult, Uri>(CropImageActivityResult()) {
    private var successBlock: (Uri) -> Unit = {}
    fun lunch(uri: Uri?, block: (Uri) -> Unit) {
        successBlock = block
        uri?.let {
            launcher.launch(
                CropImageResult(it)
            )
        }
    }

    override fun onActivityResult(result: Uri?) {
        super.onActivityResult(result)
        result?.let(successBlock)
    }
}