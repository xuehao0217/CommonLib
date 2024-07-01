package com.xueh.commonlib.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.*
import com.xueh.comm_core.base.mvvm.BaseRequstViewModel
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.helper.getDownloadProgress
import com.xueh.comm_core.helper.getPart
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
            onRequestParseData {
                api.bannerList3()
            }
            onResponse {
                banner.postValue(it)
            }
        }

//        apiFlowDSL<List<BannerVO>> {
//            onRequest {
//                api.bannerList3().data
//            }
//            onResponse {
//                banner.postValue(it)
//            }
//        }

//
//        apiDSL<List<BannerVO>> {
//            onRequest {
//                api.bannerList3().data
//            }
//            onResponse {
//                banner.postValue(it)
//            }
//        }
    }


    fun loadFlow() {
        apiFlow(request = {
            api.bannerList3()
        }, start = {
            apiLoading.value = true
        }, finally = {
            apiLoading.value = false
        }) {
            stateFlowDada.emit(it)
        }
    }

    //上传头像
    fun uploadFiles(file: File) {
        apiDSL<BaseResult<String>> {
            onRequest {
                api.uploadFiles(file.getPart())
            }
            onResponse {

            }
        }
    }

    companion object {
        const val Url = "http://3g.163.com/links/4636"
    }

    fun downloadFile() {
        apiDSL<Response<ResponseBody>> {
            onRequest {
                api.downloadFile(Url)
            }
            onResponse {
                var file = File(PathUtils.getExternalAppCachePath(), "news.apk")
                var isSuccess = FileIOUtils.writeFileFromIS(
                    file,
                    it?.body()?.byteStream(),
                )
                if (isSuccess) {
                    ToastUtils.showShort("下载完成：${file.absoluteFile}")
                }
            }
            onStart {
                Url.getDownloadProgress {
                    progressLiveData.postValue(it)
                }
                //true 表示拦截 可以在这里进行自定义 不展示loading
                true
            }

        }
    }

}
