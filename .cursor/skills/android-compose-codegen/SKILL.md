---
name: android-compose-codegen
description: >-
  按项目代码风格生成 Android Kotlin/Compose 代码：ViewModel、Compose 页面、API 接口、
  Entity 数据类、Widget 组件、Paging 列表。触发词包括"新增页面"、"新增接口"、
  "新增组件"、"生成 ViewModel"、"新增功能模块"等。当用户要求编写或生成 Kotlin/Compose
  代码时自动应用。
---

# Android Compose 代码生成

按本项目的代码风格、基类体系和目录约定生成代码。生成前先读 [templates.md](templates.md) 获取完整模板。

## 技术栈速查

| 层 | 技术 | 关键类 |
|----|------|--------|
| UI | Jetpack Compose + Material 3 | `BaseComposeActivity`、`Scaffold`、`MaterialTheme` |
| 导航 | Navigation 3 | `NavKey`、`NavDisplay`、`backStack` |
| ViewModel | 自定义基类链 | `BaseViewModel` / `BaseRequstViewModel<E>` |
| 网络 | Retrofit 3 + OkHttp 5 | `HttpRequest.getService()`、`BaseResult<T>` |
| 序列化 | kotlinx-serialization | `@Serializable`、`@SerialName` |
| 分页 | Paging 3 | `ViewModel.pager {}`、`PagingLazyColumn` |
| 图片 | Coil 3 | `ImageCompose`、`ImageSVGCompose` |

## 生成规则

### 1. ViewModel

```kotlin
class XxxViewModel : BaseRequstViewModel<XxxApi>() {
    override fun initApi() = HttpRequest.getService(XxxApi::class.java)

    // 状态：mutableStateOf + private set
    var items by mutableStateOf<List<XxxItem>>(emptyList())
        private set

    // 网络请求：apiFlow（首选）
    fun loadItems() = apiFlow(
        request = { api.getXxxList() },
        result = { items = it },
    )

    // 分页：pager 一行搞定
    fun getPagedItems() = pager { page -> api.getXxxList(page).data.datas }
}
```

**规则：**
- 继承 `BaseRequstViewModel<E>`（有网络请求）或 `BaseViewModel`（无网络）
- 状态一律 `var xxx by mutableStateOf(初始值)` + `private set`
- 网络请求用 `apiFlow` > `apiDslResult` > `apiDSL`，禁止手动 try-catch
- 需要 Flow 操作符时才用 `MutableStateFlow`
- 禁止 `GlobalScope`

### 2. API Service

```kotlin
interface XxxApi {
    @GET("xxx/list")
    suspend fun getXxxList(@Query("page") page: Int = 1): BaseResult<List<XxxItem>>

    @POST("xxx/create")
    suspend fun createXxx(@Body body: CreateXxxRequest): BaseResult<XxxItem>
}
```

**规则：**
- 所有方法 `suspend`
- 返回 `BaseResult<T>`
- 参数有默认值的加上

### 3. Entity 数据类

```kotlin
@Serializable
data class XxxItem(
    val id: Int = 0,
    val title: String = "",
    val imageUrl: String? = null,
    @SerialName("create_time")
    val createTime: String = "",
)
```

**规则：**
- 必加 `@Serializable`
- 字段用 `val`，禁止 `var`
- 所有字段给默认值（容错）
- 驼峰与 JSON key 不一致时用 `@SerialName`

### 4. Compose 页面

```kotlin
@Composable
fun XxxPage(viewModel: XxxViewModel = viewModel()) {
    val items = viewModel.items

    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }

    LazyColumn {
        items(items, key = { it.id }) { item ->
            XxxItemCard(item)
        }
    }
}

@Composable
private fun XxxItemCard(item: XxxItem) {
    // 卡片 UI
}
```

**规则：**
- 函数名 PascalCase
- ViewModel 状态直接读，无需 `collectAsState`
- 初始加载用 `LaunchedEffect(Unit)`
- LazyColumn/LazyRow 的 items 必须提供 `key`
- 纯 UI 状态用 `remember { mutableStateOf() }`
- 3 个以上同 key 的 `remember` 收拢为 `data class`

### 5. Widget 组件

| 类型 | 命名 | 示例 |
|------|------|------|
| 单一封装 | `Compose + 功能` | `ComposeImage.kt` |
| Modifier 扩展 | `ComposeExt.kt` | `clickNoRipple` |
| 同类集合 | `功能 + Widgets` | `SpacerWidgets.kt` |
| Paging | `Paging + 功能` | `PagingWidget.kt` |

组件放 `common_core/widget/`，跨模块复用的抽到 `common_core`。

### 6. Navigation 3 路由

```kotlin
// 简单页面
@Serializable
data object XxxRoute : NavKey

// 带参数
@Serializable
data class XxxDetailRoute(val id: Int) : NavKey
```

跳转处写注释：`// NAV: 源 -> 目标 (参数)`

### 7. 完整功能模块（一句话生成全套）

当用户说"新增 Xxx 功能"时，按以下顺序生成：

1. **Entity** — `entity/XxxItem.kt`
2. **API** — `api/XxxApi.kt`（或追加到已有 API 接口）
3. **ViewModel** — `viewmodel/XxxViewModel.kt`
4. **Page** — `ui/XxxPage.kt`
5. **Route** — 在 `DemoNavDestinations.kt` 添加路由，在 `DemoNavDisplay.kt` 注册

## 通用约定

- 注释中文，标识符英文
- 缩进 4 空格，行宽 120
- 尾随逗号
- 禁止 `import *`
- 空安全优先 `?.let` / `?:` / `orEmpty()`，避免 `!!`
- `when` 优先于 `if-else if` 链
- 集合操作用标准库 `map` / `filter`，不手写 for
- 常量 `private const val` 写在同文件顶部，禁止新建常量文件
- 文件头 KDoc 写清职责和协作类

## 参考文件

- 完整模板和示例 → [templates.md](templates.md)
- 项目代码风格 → `docs/CODE_STYLE_GENERIC.md`
- Cursor 项目规则 → `.cursor/rules/`
