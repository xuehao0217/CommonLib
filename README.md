# 公用库
Kotlin 协程 DSL网络请求封装
Compose 使用

#### Compose
<img width="720" height="1600" src="https://github.com/xuehao0217/CommonLib/blob/master/screenshot/Screenrecorder-2022-10-19-14-42-07-579.gif"/>


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
#### RecyclerView 精简写法
```kotlin
    val onBindAdapter = binding.rv
        .linear().addLinearItemDecoration(R.color.transparent,15)
    //            .grid(4).addGridItemDecoration(15f, 10f)
        .onBindAdapter<ItemLayoutBinding, String> { item ->
            tvItem.text = item
        }.apply {
            setNewInstance(mutableListOf("Compose"))
            setOnItemClickListener { adapter, view, position ->
                if (position==0){
                    startActivity(ComposeActivity::class.java)
                }
            }
        }
```
