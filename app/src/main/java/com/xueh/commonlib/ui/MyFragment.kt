package com.xueh.commonlib.ui

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.xueh.comm_core.base.xml.DFragment
import com.xueh.comm_core.helper.activityresult.CropImageActivityResul
import com.xueh.comm_core.helper.activityresult.CropImageResult
import com.xueh.comm_core.helper.activityresult.TakeCameraUri
import com.xueh.comm_core.utils.time.Interval
import com.xueh.comm_core.web.WebViewComposeActivity
import com.xueh.commonlib.databinding.FragmentMyBinding
import java.util.concurrent.TimeUnit


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class MyFragment : DFragment<FragmentMyBinding>() {

    private val mLauncherCameraBitmap = registerForActivityResult<Void, Bitmap>(
        ActivityResultContracts.TakePicturePreview()
    ) {
        it?.let {
            binding.ivMy.setImageBitmap(it)
        }

    }

    private val takeCameraUriLauncherUri = registerForActivityResult(TakeCameraUri()) {
        it?.let {
            mActLauncherCrop.launch(CropImageResult(it, 1, 1))
        }
    }

    //选取图片
    private val mLauncherAlbum = registerForActivityResult<String, Uri>(
        ActivityResultContracts.GetContent()
    ) {
        it?.let {
            mActLauncherCrop.launch(CropImageResult(it, 1, 1))
        }
    }

    //裁剪图片
    private var mActLauncherCrop =registerForActivityResult(CropImageActivityResul()){
        it?.let {
            binding.ivMy.setImageURI(it)
        }
    }


    val mResultLauncherPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result: Map<String, Boolean> ->
//        mLauncherCameraBitmap.launch(null)
        takeCameraUriLauncherUri.launch(null)
    }

    override fun initListener() {
        with(binding) {
            tvAlbum.setOnClickListener {
                mLauncherAlbum.launch("image/*")
            }
            tvCarema.setOnClickListener {
                mResultLauncherPermission.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE))
            }
            btStartTime.setOnClickListener {
                interval.start()
            }
            btStartPause.setOnClickListener {
                interval.pause()
            }
            btStartResume.setOnClickListener {
                interval.resume()
            }
            tvWeb.setOnClickListener {
                WebViewComposeActivity.start("https://www.baidu.com/")
//                WebViewActivity.start("https://www.baidu.com/")
            }
        }

    }

    override fun initData() {

    }

    private lateinit var interval: Interval
    override fun initView(savedInstanceState: Bundle?) {
        interval = Interval(
            0,
            1,
            TimeUnit.SECONDS,
            20
        ).life(this) // 自定义计数器个数的轮循器, 当[start]]比[end]值大, 且end不等于-1时, 即为倒计时
        // interval = Interval(1, TimeUnit.SECONDS) // 每秒回调一次, 不会自动结束
        interval.subscribe {
            binding.tvTime.text = it.toString()
        }.finish {
            binding.tvTime.text = "计时完成"
        }
    }

}