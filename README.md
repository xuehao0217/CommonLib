# 公用库适配Androidx
Kotlin 协程 DSL网络请求样式封装

#### BaseViewModel使用
```kotlin
    class HomeViewModel : BaseViewModel<RestApi>() {
        override fun initApi() = HttpRequest.getService(RestApi::class.java)

        val banner = MutableLiveData<List<BannerVO>?>()

        fun loadData() {
            apiDSL<BaseResult<List<BannerVO>>> {
                onRequest {
                    api.bannerList3()
                }
                onResponse {
                    banner.postValue(it.data)
                }
            }
        }
    }

```

```kotlin

     override fun initLivedata(viewModel: HomeViewModel) {
         viewModel.banner.observe(this, Observer {
             tv_home.text=it.toString()
         })
     }

```
