package com.xueh.commonlib.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.*
import com.xueh.comm_core.base.mvvm.BaseRequstViewModel
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.helper.listenDownloadProgress
import com.xueh.comm_core.helper.toMultipartPart
import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.net.HttpRequest
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO
import kotlinx.coroutines.flow.MutableStateFlow
import me.jessyan.progressmanager.body.ProgressInfo
import java.io.File
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 10:51
 * 备注：
 */
class HomeViewModel : BaseRequstViewModel<RestApi>() {

    override fun initApi() = HttpRequest.getService(RestApi::class.java)

    val banner = MutableLiveData<List<BannerVO>>()
    val progressLiveData = MutableLiveData<ProgressInfo>()

    var stateFlowDada = MutableStateFlow<List<BannerVO>>(emptyList())

    fun loadDsl() {
        apiDslResult {
            onRequestBaseResult {
                api.bannerList3()
            }
            onResponse {
                banner.postValue(it)
            }
            onError {
                apiError(it,showToast = false)
            }
        }

//        apiDSL {
//            onRequest {
//                api.bannerList3().data
//            }
//            onResponse {
//                banner.postValue(it)
//            }
//        }
    }


    fun loadFlow() {
//        apiFlowDSL {
//            onRequest {
//                api.bannerList3().data
//            }
//            onResponse {
//                banner.postValue(it)
//            }
//        }


        apiFlow(request = {
            api.bannerList3()
        }, start = {
//            apiLoading.value = true
        }, finally = {
//            apiLoading.value = false
        }) {
            ToastUtils.showShort("$it")
//            stateFlowDada.emit(it)
        }
    }

    //上传头像
    fun uploadFiles(file: File) {
        apiDSL {
            onRequest {
                api.uploadFiles(file.toMultipartPart())
            }
            onResponse {

            }
        }
    }

    companion object {
        const val Url = "http://3g.163.com/links/4636"
    }

    fun downloadFile() {
        apiDSL {
            onRequest {
                api.downloadFile(Url)
            }
            onResponse {
                val file = File(PathUtils.getExternalAppCachePath(), "news.apk")
                val isSuccess = FileIOUtils.writeFileFromIS(
                    file,
                    it.body()?.byteStream(),
                )
                if (isSuccess) {
                    ToastUtils.showShort("下载完成：${file.absoluteFile}")
                }
            }
            onStart {
                Url.listenDownloadProgress {
                    progressLiveData.postValue(it)
                }
            }

        }
    }

}
