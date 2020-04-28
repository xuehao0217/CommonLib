package com.xueh.commonlib.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.utils.TakePictureUtils
import com.xueh.commonlib.R
import kotlinx.android.synthetic.main.fragment_my.*
import java.io.File


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class MyFragment : DFragment() {
    lateinit var takePictureUtils: TakePictureUtils

    override fun getLayoutId() = R.layout.fragment_my

    override fun initListener() {
        tv_album.setOnClickListener {
            takePictureUtils.startTakeWayByAlbum()
        }
        tv_carema.setOnClickListener {
            takePictureUtils.startTakeWayByCarema()
        }
        tv_loading.setOnClickListener {
            showState(UiStatus.LOADING)
        }
        tv_content.setOnClickListener {
            showState(UiStatus.CONTENT)
        }
    }

    override fun initDataAfterView() {
    }

    override fun initView(savedInstanceState: Bundle?) {
        bindStateView(iv_my)
        takePictureUtils =
            TakePictureUtils(this, object : TakePictureUtils.takePictureCallBackListener {
                override fun failed(errorCode: Int, deniedPermissions: MutableList<String>?) {
                }

                override fun successful(isTailor: Boolean, outFile: File?, filePath: Uri?) {
                    iv_my.setImageURI(Uri.fromFile(outFile?.absoluteFile))
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        takePictureUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takePictureUtils.attachToActivityForResult(requestCode, resultCode, data)
    }

}