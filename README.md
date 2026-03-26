# CommonLib

Android 多模块工程：**`common_core`** 为可复用的业务与 UI 基础库；**`app`** 为依赖该库的演示应用（Jetpack Compose + **Navigation 3**），用于验证组件与交互。

---

## 模块说明

| 模块 | 类型 | 说明 |
|------|------|------|
| **common_core** | Android Library (`api` 暴露多数依赖) | Compose / Material3、网络（Retrofit + Kotlin Serialization）、协程 DSL 请求、`BaseComposeActivity`、主题与灰度适配、分页与 Web 等封装 |
| **app** | Application | 演示入口：`MainComposeActivity`；首页为 Navigation 3 示例列表；「我的」「拦截」Tab 为调试与外链 |

---

## 环境要求

- **JDK**：21（与工程 `compileOptions` 一致）
- **Android Studio**：建议支持 AGP 8.13+ / Kotlin 2.3+ 的版本
- **SDK**：`compileSdk` / `targetSdk` **36**，`minSdk` **23**

版本与依赖集中在 [`gradle/libs.versions.toml`](gradle/libs.versions.toml)。

---

## 技术栈摘要

- **语言**：Kotlin **2.3.x**，**Kotlin Serialization**
- **UI**：Jetpack Compose（Compose BOM）、Material 3、**Navigation 3**（`navigation3-runtime` / `navigation3-ui`，演示在 `app` 模块）
- **异步**：Kotlin Coroutines、Flow
- **网络**：OkHttp 5、Retrofit 3、`BaseResult` + DSL 封装（见 `RequestViewModel` / `ViewModelDsl`）
- **其他**：Coil 3、Paging、DataStore、MMKV、Utilcodex 等（详见 `common_core/build.gradle.kts`）

> **说明**：工程已移除 **Navigation 2**（`navigation-compose`），Compose 侧主导航以 Navigation 3 为准。

---

## 工程结构（常用入口）

```
CommonLib/
├── app/                      # 演示 App
│   └── src/main/java/com/xueh/commonlib/
│       ├── navigation/       # NavKey、DemoNavDisplay（Navigation 3）
│       └── ui/               # HomePage、MainComposeActivity、各 Compose 示例页
├── common_core/              # 基础库
│   └── src/main/java/com/xueh/comm_core/
│       ├── base/compose/     # BaseComposeActivity、ComposeMaterialTheme、主题状态
│       ├── base/mvvm/        # BaseViewModel、BaseComposeViewModel 等
│       ├── net/              # 网络与协程 DSL
│       ├── helper/compose/   # Compose 扩展与工具
│       └── weight/compose/   # 通用 Compose 组件
├── gradle/
│   └── libs.versions.toml    # 版本与依赖别名
└── settings.gradle.kts       # include(":app", ":common_core")
```

---

## 构建与运行

```bash
# 编译演示 App（Debug）
./gradlew :app:assembleDebug

# 仅编译 common_core
./gradlew :common_core:assembleDebug

# 检查依赖更新（根工程已接入 versions 插件时可使用）
./gradlew dependencyUpdates
```

安装：在 Android Studio 中选择 **`app`** 运行，启动类为带 `MAIN`/`LAUNCHER` 的 Activity（一般为闪屏或主 Compose 入口，以 `AndroidManifest` 为准）。

---

## 使用方式（接入自己的 App）

1. 在业务工程中 **`implementation(project(":common_core"))`**（或通过 Maven 发布后再依赖，视你的发布方式而定）。
2. Activity 可继承 **`BaseComposeActivity`**，实现 **`setComposeContent()`**，在 **`ComposeMaterialTheme`** 树内编写 Compose UI。
3. ViewModel 可继承 **`BaseViewModel`** / **`RequestViewModel`**，使用 **`apiDSL` / `apiFlow` / `onRequestBaseResult`** 等发起请求。
4. Compose 页面可使用 **`BaseComposeViewModel<T> { vm -> ... }`** 自动绑定 Loading 与异常提示。

### 协程 DSL 请求示例（概念）

```kotlin
class MyViewModel : BaseViewModel() {
    fun load() {
        apiDslResult<MyData> {
            onRequestBaseResult { api.getData() }
            onResponse { data -> /* 更新 UI 状态 */ }
        }
    }
}
```

具体 API 与泛型以 `ViewModelDsl`、`BaseResult` 定义为准。

---

## 主题与全局状态

- **亮暗模式**：`appThemeType`（`AppThemeType`：跟随系统 / 浅色 / 深色）
- **主题色板**：`appThemeColorType`（绿 / 紫 / 橙 / 蓝 / 壁纸动态色）
- **Material 入口**：`ComposeMaterialTheme`（在函数体内读取全局色板状态，保证调色后界面会重组）

灰度蒙层（公祭日等）由 **`GrayAppAdapter`** 控制。

---

## 仓库与许可

- 若需展示动效，可在仓库根目录增加 `screenshots/` 或 `docs/` 下的录屏 / 截图，并在本 README 中引用相对路径。
- 许可证与再分发条件以仓库内 **`LICENSE`**（若存在）为准；若无单独声明，默认遵循作者约定。

---

## 相关文档

- [Navigation 3 入门](https://developer.android.com/guide/navigation/navigation-3/get-started)
- [Navigation 3 迁移指南](https://developer.android.com/guide/navigation/navigation-3/migration-guide)
