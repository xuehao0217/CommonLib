# 公用库适配Androidx
MVVM  Kotlin Coroutine 封装

#### BaseViewModel使用
```java
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

```java

     override fun initLivedata(viewModel: HomeViewModel) {
         viewModel.banner.observe(this, Observer {
             tv_home.text=it.toString()
         })
     }

```
