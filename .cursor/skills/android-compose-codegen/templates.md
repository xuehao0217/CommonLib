# 代码模板参考

## 模板 1：带网络请求的 ViewModel

```kotlin
package com.xueh.commonlib.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xueh.comm_core.base.mvvm.BaseRequstViewModel
import com.xueh.comm_core.net.HttpRequest
import com.xueh.commonlib.api.XxxApi
import com.xueh.commonlib.entity.XxxItem

/**
 * Xxx 模块 ViewModel
 *
 * 职责：管理 Xxx 列表数据的加载和状态
 * 协作：[XxxApi] 提供网络接口，[XxxPage] 消费状态
 */
class XxxViewModel : BaseRequstViewModel<XxxApi>() {
    override fun initApi() = HttpRequest.getService(XxxApi::class.java)

    var items by mutableStateOf<List<XxxItem>>(emptyList())
        private set

    var detail by mutableStateOf<XxxItem?>(null)
        private set

    fun loadItems() = apiFlow(
        request = { api.getXxxList() },
        result = { items = it },
    )

    fun loadDetail(id: Int) = apiFlow(
        request = { api.getXxxDetail(id) },
        result = { detail = it },
    )
}
```

## 模板 2：带分页的 ViewModel

```kotlin
package com.xueh.commonlib.ui.viewmodel

import com.xueh.comm_core.base.mvvm.BaseRequstViewModel
import com.xueh.comm_core.helper.compose.pager
import com.xueh.comm_core.net.HttpRequest
import com.xueh.commonlib.api.XxxApi

/**
 * Xxx 分页列表 ViewModel
 *
 * 职责：提供分页数据流
 * 协作：[XxxApi] 提供分页接口，页面用 PagingLazyColumn 消费
 */
class XxxListViewModel : BaseRequstViewModel<XxxApi>() {
    override fun initApi() = HttpRequest.getService(XxxApi::class.java)

    fun getPagedItems() = pager { page ->
        api.getXxxList(page).data.datas
    }
}
```

## 模板 3：API Service

```kotlin
package com.xueh.commonlib.api

import com.xueh.comm_core.net.bean.BaseResult
import com.xueh.commonlib.entity.XxxItem
import com.xueh.commonlib.entity.XxxListResponse
import retrofit2.http.*

interface XxxApi {
    @GET("xxx/list")
    suspend fun getXxxList(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
    ): BaseResult<XxxListResponse>

    @GET("xxx/detail/{id}")
    suspend fun getXxxDetail(@Path("id") id: Int): BaseResult<XxxItem>

    @POST("xxx/create")
    suspend fun createXxx(@Body body: CreateXxxRequest): BaseResult<XxxItem>

    @DELETE("xxx/{id}")
    suspend fun deleteXxx(@Path("id") id: Int): BaseResult<Unit>
}
```

## 模板 4：Entity 数据类

```kotlin
package com.xueh.commonlib.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XxxItem(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    @SerialName("create_time")
    val createTime: String = "",
    @SerialName("is_active")
    val isActive: Boolean = true,
)

@Serializable
data class XxxListResponse(
    val curPage: Int = 0,
    val datas: List<XxxItem> = emptyList(),
    val pageCount: Int = 0,
    val total: Int = 0,
)
```

## 模板 5：普通列表页面

```kotlin
package com.xueh.commonlib.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xueh.commonlib.entity.XxxItem
import com.xueh.commonlib.ui.viewmodel.XxxViewModel

/**
 * Xxx 列表页面
 *
 * UI 结构：顶部无 → LazyColumn 列表 → XxxItemCard 卡片
 * 数据来源：[XxxViewModel.items]
 */
@Composable
fun XxxPage(viewModel: XxxViewModel = viewModel()) {
    val items = viewModel.items

    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items, key = { it.id }) { item ->
            XxxItemCard(item)
        }
    }
}

@Composable
private fun XxxItemCard(item: XxxItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
```

## 模板 6：分页列表页面

```kotlin
package com.xueh.commonlib.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.xueh.comm_core.widget.compose.PagingLazyColumn
import com.xueh.commonlib.ui.viewmodel.XxxListViewModel

/**
 * Xxx 分页列表
 *
 * 使用 [PagingLazyColumn] 自动处理加载/空/错误状态
 */
@Composable
fun XxxListPage(viewModel: XxxListViewModel = viewModel()) {
    val pagingItems = viewModel.getPagedItems().collectAsLazyPagingItems()

    PagingLazyColumn(pagingItems) { _, item ->
        XxxItemCard(item)
    }
}
```

## 模板 7：带 Scaffold + TopAppBar 的页面

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XxxPage(
    onBack: () -> Unit,
    viewModel: XxxViewModel = viewModel(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("页面标题") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // 页面内容
        }
    }
}
```

## 模板 8：Navigation 3 路由注册

```kotlin
// ── DemoNavDestinations.kt ──
@Serializable
data object XxxRoute : NavKey

@Serializable
data class XxxDetailRoute(val id: Int) : NavKey

// ── DemoNavDisplay.kt 中注册 ──
entry<XxxRoute>(
    metadata = Nav3VerticalPushTransitionSpec,
) {
    XxxPage()
}

entry<XxxDetailRoute>(
    metadata = Nav3VerticalPushTransitionSpec,
) { route ->
    XxxDetailPage(id = route.id)
}
```

## 模板 9：状态收拢为 data class

```kotlin
// 当 3 个以上 remember 共享同一 key 时
data class XxxUiState(
    val showDialog: Boolean = false,
    val selectedIndex: Int? = null,
    val expandedItemId: Int? = null,
    val searchQuery: String = "",
)

@Composable
fun XxxPage() {
    var uiState by remember { mutableStateOf(XxxUiState()) }

    // 读取
    if (uiState.showDialog) { /* ... */ }

    // 更新
    uiState = uiState.copy(showDialog = true)
    uiState = uiState.copy(selectedIndex = 3, expandedItemId = null)
}
```

## 模板 10：不需要网络请求的 ViewModel

```kotlin
package com.xueh.commonlib.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xueh.comm_core.base.mvvm.BaseViewModel

/**
 * Xxx 纯本地逻辑 ViewModel
 *
 * 职责：管理 Xxx 的本地状态和业务逻辑
 */
class XxxViewModel : BaseViewModel() {
    var selectedTab by mutableStateOf(0)
        private set

    var isEditing by mutableStateOf(false)
        private set

    fun selectTab(index: Int) {
        selectedTab = index
    }

    fun toggleEditing() {
        isEditing = !isEditing
    }
}
```

## 导入速查

```kotlin
// mutableStateOf 委托
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Compose 基础
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

// Material 3
import androidx.compose.material3.*

// 项目基类
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.base.mvvm.BaseRequstViewModel
import com.xueh.comm_core.net.HttpRequest
import com.xueh.comm_core.net.bean.BaseResult
import com.xueh.comm_core.helper.compose.pager

// 序列化
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
```
