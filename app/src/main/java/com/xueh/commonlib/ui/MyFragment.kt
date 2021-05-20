package com.xueh.commonlib.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.utils.TakePictureUtils
import com.xueh.comm_core.utils.time.Interval
import com.xueh.commonlib.R
import com.xueh.commonlib.databinding.FragmentMyBinding
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class MyFragment : DFragment<FragmentMyBinding>() {
    lateinit var takePictureUtils: TakePictureUtils

    override fun initListener() {
        with(binding) {
            tvAlbum.setOnClickListener {
                takePictureUtils.startTakeWayByAlbum()
            }
            tvCarema.setOnClickListener {
                takePictureUtils.startTakeWayByCarema()
            }
            tvLoading.setOnClickListener {
                showState(UiStatus.LOADING)
            }
            tvContent.setOnClickListener {
                showState(UiStatus.CONTENT)
            }
            btStartTime.setOnClickListener {
                interval.resume()
            }
            btStartPause.setOnClickListener {
                interval.pause()
            }
        }

    }

    override fun initDataAfterView() {
    }

    private lateinit var interval: Interval
    override fun initView(savedInstanceState: Bundle?) {
        bindStateView(binding.ivMy)
        takePictureUtils =
            TakePictureUtils(this, object : TakePictureUtils.takePictureCallBackListener {
                override fun failed(errorCode: Int, deniedPermissions: MutableList<String>?) {
                }

                override fun successful(isTailor: Boolean, outFile: File?, filePath: Uri?) {
                    binding.ivMy.setImageURI(Uri.fromFile(outFile?.absoluteFile))
                }
            })



        interval = Interval(
            0,
            1,
            TimeUnit.SECONDS,
            30
        ).life(this) // 自定义计数器个数的轮循器, 当[start]]比[end]值大, 且end不等于-1时, 即为倒计时
        // interval = Interval(1, TimeUnit.SECONDS) // 每秒回调一次, 不会自动结束
        interval.subscribe {
            binding.tvTime.text = it.toString()
        }.finish {
            binding.tvTime.text = "计时完成"
        }.start()
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