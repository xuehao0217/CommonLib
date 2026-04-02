# Cursor Rules 使用指南

本项目配置了 7 个 Cursor Rules（`.cursor/rules/`），AI 会在对应场景下**自动加载**，无需手动操作。

---

## 规则列表

| 规则文件 | 触发条件 | 用途 |
|---------|---------|------|
| `android-project.mdc` | **每次对话自动加载** | 项目概览、技术栈、架构分层 |
| `git-commit.mdc` | **每次对话自动加载** | Git 提交规范 |
| `kotlin-compose.mdc` | 编辑 `*.kt` 文件时 | Kotlin/Compose 编码规范 |
| `gradle-deps.mdc` | 编辑 `*.gradle.kts` 或 `*.toml` 时 | Gradle 依赖管理约定 |
| `network-layer.mdc` | 编辑 `net/`、`api/`、`entity/` 下的 `.kt` 文件时 | 网络层使用约定 |
| `navigation3.mdc` | 编辑 `navigation/`、`ui/` 下的 `.kt` 文件时 | Navigation 3 路由规范 |
| `code-review.mdc` | 要求 review / 检查代码时 | Code Review 检查清单 |

---

## 使用场景和示例

### 1. 日常写代码（自动生效）

编辑 `.kt` 文件时，`android-project.mdc` + `kotlin-compose.mdc` 自动加载。AI 会：

- 按项目的 ViewModel 继承链生成代码
- 状态用 `mutableStateOf` + `private set`
- 网络请求用 `apiFlow` / `apiDSL`
- 注释用中文，标识符用英文

**你只管正常写代码，AI 自动遵守规范。**

### 2. 编写网络相关代码

编辑 `api/`、`net/`、`entity/` 目录下的文件时，`network-layer.mdc` 额外加载。

| 你说 | AI 自动做到 |
|------|-----------|
| "帮我写一个获取用户列表的接口" | 生成 `suspend fun` + `BaseResult<T>` 返回值 |
| "帮我写对应的数据类" | 用 `@Serializable` + `val` 字段 + 默认值 |
| "在 ViewModel 里调这个接口" | 继承 `BaseRequstViewModel<Api>`，用 `apiFlow` |

示例对话：

```
你：帮我写一个获取用户信息的接口和数据类

AI（自动遵守 network-layer.mdc）：
→ interface UserApi { suspend fun getUser(): BaseResult<UserInfo> }
→ @Serializable data class UserInfo(val id: Int = 0, val name: String = "")
```

### 3. 修改 Gradle 构建文件

编辑 `build.gradle.kts` 或 `libs.versions.toml` 时，`gradle-deps.mdc` 自动加载。

| 你说 | AI 自动做到 |
|------|-----------|
| "帮我添加 XXX 依赖" | 先加到 `libs.versions.toml`，再用 `libs.xxx` 引用 |
| "升级 Coil 版本" | 只改 `toml` 中的版本号 |
| "这个依赖放哪个模块" | `common_core` 用 `api()`，`app` 用 `implementation()` |

### 4. Code Review

要求 AI 审查代码时，`code-review.mdc` 自动触发。

**触发方式：**

```
"review 一下这个文件"
"帮我检查这段代码"
"看看有什么问题"
"审查一下"
```

AI 会按检查清单逐项审查：

- **状态管理** — mutableStateOf 用法、remember 收拢、状态分层
- **网络请求** — 是否用 apiFlow/apiDSL、序列化是否正确
- **Compose 性能** — LazyColumn key、remember 遗漏、重组范围
- **协程安全** — GlobalScope 检查、viewModelScope 使用、泄漏风险
- **代码质量** — 死代码、命名、注释价值
- **依赖管理** — 版本是否在 toml 统一管理

### 5. Navigation 3 导航

编辑 `navigation/` 或 `ui/` 目录下的文件时，`navigation3.mdc` 自动加载。

| 你说 | AI 自动做到 |
|------|-----------|
| "帮我新增一个 XXX 页面" | 在 `DemoNavDestinations.kt` 加 NavKey，在 `DemoNavDisplay.kt` 注册 |
| "这个功能需要子导航" | 用 `sealed interface : NavKey` + 独立 `NavDisplay` |
| "加个页面跳转" | `backStack.add(XxxRoute)` + `// NAV:` 注释 |

**两种路由组织方式：**

- **全局路由**：定义在 `navigation/DemoNavDestinations.kt`，注册在 `DemoNavDisplay.kt`
- **Feature 内部路由**：用 `sealed interface : NavKey` 收拢在 feature 文件内

```kotlin
// 全局路由 — data object / data class 实现 NavKey
@Serializable
data object DemoFooRoute : NavKey

// Feature 内部路由 — sealed interface
@Serializable
private sealed interface FooKey : NavKey {
    @Serializable data object List : FooKey
    @Serializable data object Detail : FooKey
}
```

### 6. Git 提交代码

每次对话自动加载 `git-commit.mdc`，你说"提交代码"时 AI 自动：

- 分析改动内容，选择正确的 type
- 用中文写简要描述
- 多文件变更时补充列表

**Commit Message 格式：**

```
<type>: <简要描述>
```

| type | 含义 | 示例 |
|------|------|------|
| `feat` | 新功能 | feat: 接入 compose-webview 封装 |
| `fix` | 修复 bug | fix: 修复列表滚动崩溃 |
| `refactor` | 重构 | refactor: 统一使用 mutableStateOf |
| `docs` | 文档 | docs: 新增 Compose 状态收拢规范 |
| `chore` | 构建/依赖 | chore: 升级 AGP 至 9.1 |
| `perf` | 性能优化 | perf: LazyColumn 减少重组 |
| `test` | 测试 | test: 添加 ViewModel 单测 |

可选加模块前缀：`feat(web):` `fix(net):`

### 7. 搭配 @ 引用

Rules 之外，你还可以在对话中用 `@` 引用更多上下文：

```
@docs/CODE_STYLE_GENERIC.md — 完整编码规范
@common_core/src/main/java/com/xueh/comm_core/ — 让 AI 参考已有封装
@gradle/libs.versions.toml — 让 AI 知道当前依赖版本
```

---

## 如何修改规则

直接编辑 `.cursor/rules/` 下的 `.mdc` 文件即可，格式：

```markdown
---
description: 规则描述
globs: "**/*.kt"        # 文件匹配模式（可选）
alwaysApply: false       # true = 每次对话都加载
---

规则内容...
```

修改后**立即生效**，下次对话 AI 就会按新规则执行。

---

## 全局 User Rules（跨项目通用）

除了项目级 Rules（`.cursor/rules/`），Cursor 还支持**全局 User Rules**，对所有项目生效。

**设置路径：** Cursor Settings → Rules → User Rules

> ⚠️ User Rules **只存本机，不跟账号同步**。换电脑 / 重装 Cursor 需要重新配置。
> 以下内容备份在此，方便复制粘贴。

```
## 身份与语言
- 我是 Android 开发，使用 Kotlin + Jetpack Compose（纯 Compose，无 XML 布局）
- 回复使用中文
- 代码注释使用中文，标识符（变量名、函数名、类名）使用英文
- 不要过度解释简单代码，不要加多余的注释

## 技术栈约束
- 语言：Kotlin，禁止 Java
- UI：Jetpack Compose + Material 3，禁止 Material 2、禁止 XML 布局
- 导航：Navigation 3（androidx.navigation3），不是 Navigation 2
- 网络：Retrofit 3 + OkHttp 5 + kotlinx-serialization，禁止 Gson、禁止 Moshi
- 异步：Kotlin Coroutines + viewModelScope，禁止 RxJava、禁止 GlobalScope
- 状态管理：mutableStateOf（ViewModel 和 Composable 均可），禁止 LiveData
- 构建：Gradle Kotlin DSL + Version Catalog（libs.versions.toml），禁止 Groovy DSL
- 分页：Paging 3

## 代码风格
- 缩进 4 空格，最大行宽 120
- data class / 函数参数使用尾随逗号
- 禁止 import *（星号导入）
- 字符串拼接优先用模板 "$xxx" 或 "${xxx.bar}"
- when 优先于 if-else if 链
- 空安全：优先 ?.let / ?: / orEmpty()，避免 !!
- 集合操作优先用 Kotlin 标准库（map / filter / groupBy），不手写 for 循环
- 命名：类 PascalCase，函数/变量 camelCase，常量 UPPER_SNAKE_CASE
- Composable 函数名 PascalCase，非 Composable 函数名 camelCase

## ViewModel 规范
- ViewModel 状态用 var xxx by mutableStateOf() + private set
- 网络请求用 apiDSL / apiFlow / apiDslResult 封装，不要手动 try-catch + launch
- 统一用 viewModelScope，禁止 GlobalScope

## Compose 规范
- LazyColumn / LazyRow 的 items 必须提供 key
- remember 内不做耗时操作
- 同一 key 下 3 个以上 remember { mutableStateOf } 收拢为 data class
- derivedStateOf 只订阅必要字段
- Side Effect 选择：一次性 → LaunchedEffect，生命周期 → DisposableEffect

## 序列化
- 实体类加 @Serializable，字段用 val 不用 var
- JSON 字段映射用 @SerialName，不用 @JsonNames（除非需要多别名兼容）

## 格式与生成
- 生成代码时遵守 .editorconfig 格式配置
- Git commit 信息格式：<type>: <中文描述>（type = feat/fix/refactor/docs/chore/style/perf）
```

---

## 自定义 Skill：代码生成

项目配置了自定义 Skill `.cursor/skills/android-compose-codegen/`，AI 在生成代码时自动遵守项目风格。

**触发方式（自动识别）：**

```
"帮我新增一个 Xxx 功能"
"生成一个 ViewModel"
"新增一个 Compose 页面"
"写一个 API 接口"
"新增一个列表组件"
```

**生成能力：**

| 说 | AI 生成 |
|----|--------|
| "新增用户模块" | Entity + API + ViewModel + Page + Route 全套 |
| "写个分页列表" | ViewModel（`pager`）+ Page（`PagingLazyColumn`） |
| "加个详情接口" | API 方法 + Entity + ViewModel 调用 |
| "写个弹窗组件" | Widget 文件，命名遵守 `Compose + 功能` 规范 |

**代码风格保证：**
- ViewModel 自动继承 `BaseRequstViewModel<E>` / `BaseViewModel`
- 状态自动用 `mutableStateOf` + `private set`
- 网络请求自动用 `apiFlow` 封装
- Entity 自动加 `@Serializable`、`val` 字段、默认值
- 组件命名自动遵守 Widget 命名规范

**Skill 文件结构：**

```
.cursor/skills/android-compose-codegen/
├── SKILL.md       # 生成规则和约定
└── templates.md   # 10 个代码模板（ViewModel、Page、API、Entity、路由等）
```

### 示范：新增一个「文章收藏」功能

假设你对 AI 说：

```
帮我新增一个文章收藏功能，需要：
1. 收藏列表接口（分页）
2. 添加/取消收藏接口
3. ViewModel
4. 收藏列表页面
```

AI 会按 Skill 中的模板和规则，生成以下代码：

**第 1 步：Entity**

```kotlin
// entity/CollectArticle.kt
@Serializable
data class CollectArticle(
    val id: Int = 0,
    val title: String = "",
    val author: String = "",
    val link: String = "",
    @SerialName("publish_time")
    val publishTime: String = "",
)
```

**第 2 步：API**

```kotlin
// api/CollectApi.kt
interface CollectApi {
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectList(@Path("page") page: Int): BaseResult<PageData<CollectArticle>>

    @POST("lg/collect/{id}/json")
    suspend fun collect(@Path("id") id: Int): BaseResult<Unit>

    @POST("lg/uncollect/{id}/json")
    suspend fun uncollect(@Path("id") id: Int): BaseResult<Unit>
}
```

**第 3 步：ViewModel**

```kotlin
// viewmodel/CollectViewModel.kt
class CollectViewModel : BaseRequstViewModel<CollectApi>() {
    override fun initApi() = HttpRequest.getService(CollectApi::class.java)

    fun getCollectList() = pager { page ->
        api.getCollectList(page).data.datas
    }

    fun toggleCollect(id: Int, collected: Boolean) = apiFlow(
        request = {
            if (collected) api.uncollect(id) else api.collect(id)
        },
        result = { /* 刷新列表或通知 UI */ },
    )
}
```

**第 4 步：Page**

```kotlin
// ui/CollectPage.kt
@Composable
fun CollectPage(viewModel: CollectViewModel = viewModel()) {
    val pagingItems = viewModel.getCollectList().collectAsLazyPagingItems()

    PagingLazyColumn(pagingItems) { _, item ->
        CollectItemCard(
            article = item,
            onToggle = { viewModel.toggleCollect(item.id, true) },
        )
    }
}

@Composable
private fun CollectItemCard(
    article: CollectArticle,
    onToggle: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(article.title, style = MaterialTheme.typography.titleMedium)
                Text(article.author, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onToggle) {
                Icon(Icons.Default.Favorite, contentDescription = "取消收藏")
            }
        }
    }
}
```

**第 5 步：Route**

```kotlin
// DemoNavDestinations.kt 中添加
@Serializable
data object CollectRoute : NavKey

// DemoNavDisplay.kt 中注册
entry<CollectRoute>(metadata = Nav3VerticalPushTransitionSpec) {
    CollectPage()
}

// 跳转处
// NAV: MinePage -> CollectRoute
backStack.add(CollectRoute)
```

### 示范：生成单个 ViewModel

```
帮我写一个管理用户设置的 ViewModel，不需要网络请求，
需要管理：主题模式（深色/浅色）、字体大小、通知开关
```

AI 生成：

```kotlin
class SettingsViewModel : BaseViewModel() {
    var isDarkMode by mutableStateOf(false)
        private set

    var fontSize by mutableStateOf(16)
        private set

    var notificationEnabled by mutableStateOf(true)
        private set

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }

    fun updateFontSize(size: Int) {
        fontSize = size
    }

    fun toggleNotification() {
        notificationEnabled = !notificationEnabled
    }
}
```

注意 AI 自动做到了：继承 `BaseViewModel`（无网络）、`mutableStateOf` + `private set`、camelCase 命名。

### 示范：新增 Widget 组件

```
帮我封装一个通用的空状态组件，显示图片 + 文字 + 重试按钮
```

AI 生成文件 `common_core/widget/ComposeEmptyState.kt`（遵守 `Compose + 功能` 命名）：

```kotlin
/**
 * 通用空状态组件
 *
 * 用于列表为空、加载失败等场景，显示居中图片 + 提示文字 + 可选重试按钮
 */
@Composable
fun ComposeEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    @DrawableRes imageRes: Int? = null,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        imageRes?.let {
            Image(
                painter = painterResource(it),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        onRetry?.let {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = it) {
                Text("重试")
            }
        }
    }
}
```

---

## 配合 CODE_STYLE_GENERIC.md

`docs/CODE_STYLE_GENERIC.md` 是完整的编码风格文档，Cursor Rules 是其中核心规则的**精简版**。关系：

- **User Rules** — 全局生效，所有项目通用的个人偏好（仅存本机）
- **Project Rules**（`.cursor/rules/`）— 项目级，AI 按场景自动加载，精简可执行
- **Project Skills**（`.cursor/skills/`）— 复杂任务的专用工作流（如代码生成）
- **CODE_STYLE_GENERIC.md** — 完整参考文档，需要时用 `@` 手动引用

四者互补，不冲突。优先级：User Rules（最高） → Project Rules / Skills → 手动 `@` 引用。
