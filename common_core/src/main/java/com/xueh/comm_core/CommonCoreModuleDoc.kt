package com.xueh.comm_core

/**
 * # common_core 模块总览
 *
 * 本模块聚合应用级「基础设施」：进程内初始化、Compose 页面壳、主题、MVVM+网络、分页、Web、
 * 权限与 Activity Result、协程安全封装、以及可复用的 Compose 组件（[com.xueh.comm_core.widget]）。
 *
 * ## 分层与职责（按包）
 *
 * - **base**：`BaseApplication` 主进程初始化；`BaseComposeActivity` 沉浸式、设计稿宽度 Density、`Scaffold` 与标题栏。
 * - **base.compose.theme**：`AppThemeType` / `AppThemeColorType`、`ComposeMaterialTheme`、`AppThemePreferences` 与 `PersistAppThemePreferencesEffect`。
 * - **base.mvvm**：`AbsViewModel` → `RequestViewModel` → `BaseViewModel`；`BaseComposeViewModel` 绑定 Loading 与异常；`BaseRequstViewModel` 懒加载 API。
 * - **net**：`HttpRequest`（Retrofit 缓存、Header、Debug 日志/Chucker）、`BaseResult`、`JsonManager`。
 * - **net.coroutinedsl**：`ViewModelDsl` 链式请求；`RequestViewModel` 的 `apiDSL` / `apiFlow` / `apiError` 等统一状态。
 * - **helper**：`launchSafety`；`helper.compose` 下分页、权限、截图、`OrderedStateMap` 等。
 * - **web**：`ComposeWebView`（Accompanist）；`WebCompose` 内 `CustomWebView`；`AgentWebScaffold` / `AgentComposeWebActivity`。
 * - **utils**：`DataStoreUtils`、`MMKVUtil`、`Lunar` 等。
 * - **widget**：图片、弹窗、底部导航、Tab、分页占位与 `Modifier` 扩展。
 *
 * ## 推荐启动流程（主进程）
 *
 * 1. `Application.onCreate` → 子类继承 [BaseApplication]，仅在主进程执行：
 *    Logger、**[DataStoreUtils.init]**、**[com.xueh.comm_core.base.compose.theme.AppThemePreferences.restore]**、业务 [init]。
 * 2. 尽早调用 **[HttpRequest.init]**（BaseUrl + 可选 OkHttp/Retrofit DSL），再 [HttpRequest.getService] 拿接口实例。
 * 3. Compose 页面继承 **[BaseComposeActivity]**：`setContent` 内已注入设计稿 Density、[ComposeMaterialTheme]、
 *    [com.xueh.comm_core.base.compose.theme.PersistAppThemePreferencesEffect]；子类实现 [com.xueh.comm_core.base.compose.BaseComposeActivity.setComposeContent]。
 *
 * ## 网络请求流程（ViewModel）
 *
 * 1. ViewModel 继承 **[BaseViewModel]** 或带 Retrofit 的 **[BaseRequstViewModel]**。
 * 2. 调用 **[RequestViewModel.apiDSL]** / **apiFlowDSL** / **apiDslResult** 配置 [ViewModelDsl]，
 *    或 **[RequestViewModel.apiFlow]** 传入 `suspend () -> BaseResult<T>`。
 * 3. **[RequestViewModel.runIfNetworkAvailable]**（私有）先检查网络，失败走 [RequestViewModel.apiError]。
 * 4. 成功路径：`apiLoading(true)` → IO 请求 → `isSuccess` 则回调 data，否则 `apiError`；**apiFinally** 关 Loading。
 * 5. Compose 页用 **[BaseComposeViewModel]** 自动挂 [com.xueh.comm_core.widget.ComposeLoadingDialog] 与异常 Toast。
 *
 * ## 主题持久化流程
 *
 * 1. 冷启动：[AppThemePreferences.restore] 从 DataStore 恢复 [com.xueh.comm_core.base.compose.theme.appThemeType] /
 *    [com.xueh.comm_core.base.compose.theme.appThemeColorType]。
 * 2. 运行时：用户切换主题 → 状态变更 → [PersistAppThemePreferencesEffect] 中 **LaunchedEffect** 调用 [AppThemePreferences.persist]。
 *
 * ## 分页（Paging 3）流程
 *
 * 1. 实现 **[BasePagingSource]** 的 `getDataList(page)`，或 **[com.xueh.comm_core.helper.compose.pager]** 传入 lambda。
 * 2. `Pager(...).flow.cachedIn(viewModelScope)` 得到 `Flow<PagingData<T>>`。
 * 3. UI 层使用 [com.xueh.comm_core.widget] 中 `PagingState*`、`LazyPagingItems` 扩展展示刷新/加载/空态/错误。
 * 4. 需在列表外做删除/替换/头尾插入时，可用 **[PagingDataModifier]**（[com.xueh.comm_core.helper.compose.BasePagingSource] 同文件）。
 *
 * ## Web 两条线
 *
 * - **Compose + Accompanist**：**[ComposeWebView]**，`rememberWebViewState` + 进度条 + [AccompanistWebViewClient]（类型名来自三方库）。
 * - **AndroidView**：**[WebCompose.WebViewPage]** / **[CustomWebView]**；**[AgentWebScaffold]** 封装 AgentWeb 生命周期与返回栈。
 *
 * 以下占位符仅用于将本说明挂在单一符号上，避免空文件；请勿在业务中引用。
 */
@Suppress("unused")
internal object CommonCoreModuleDoc
