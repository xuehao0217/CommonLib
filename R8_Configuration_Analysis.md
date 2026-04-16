# R8 配置分析

## 构建与 R8 开关（`app` 模块）

- **已采用**默认优化规则：`getDefaultProguardFile("proguard-android-optimize.txt")`，与官方建议一致。
- **已调整**：`release` 构建类型启用 `isMinifyEnabled = true` 与 `isShrinkResources = true`，以便发布包实际走 R8 压缩与资源收缩；此前 `minify` 关闭时，下列 `proguard-rules.pro` 与依赖自带的 consumer 规则不会在常用发布流程中生效。
- **`common_core` 库模块**：`consumerProguardFiles("proguard-rules.pro")` 已配置；库自身 `release` 仍关闭 minify（AAR 常见做法），由 **app** 在开启 minify 时合并规则。

## 工程级属性（`gradle.properties`）

- 未设置 `android.enableR8.fullMode=false`，与 AGP 8+ 默认全量 R8 行为一致。
- 存在 `android.r8.strictFullModeForKeepRules=false`，用于放宽对部分 keep 语法的严格校验；若后续收紧规则、清理宽泛 keep，可再评估是否恢复严格模式以便尽早暴露问题。

## AGP 版本

- 当前 AGP 为 9.x 系列，无需因「低于 9.0」而单独做迁移说明。

---

## `app/proguard-rules.pro`：规则审阅与建议动作

下列按「影响面由大到小」排列（先包级/全类保留，再成员级与条件规则）。**未直接改动的文件**；请在确认反射与线上行为后自行删减或收窄。

### 1. 包级 `-keep … { *; }`（影响面大）

| 规则 | 建议动作 | 理由 |
|------|----------|------|
| `-keep @kotlinx.serialization.Serializable class com.xueh.commonlib.** { *; }` | **收窄** | 保留整个 `com.xueh.commonlib` 下所有 `@Serializable` 的类及成员；若仅部分子包为网络/DTO 模型，可改为只保留 `entity`、`api` 等实际包路径，或依赖 `kotlinx-serialization` 的编译器生成规则与 `consumer` 规则，仅对仍被反射/多模块使用的类型补最小 `-keepnames`。 |
| `-keep class com.xueh.commonlib.MyApplication { *; }` | **保留或收窄** | 若 Application 仅被 `AndroidManifest` 引用，通常由清单与默认规则覆盖；若仍有 JNI/反射入口，可改为只保留类名或 `onCreate` 等。 |

### 2. 与「官方库自带 consumer 规则」重叠（常见可删）

| 规则 | 建议动作 | 理由 |
|------|----------|------|
| `-keep interface com.xueh.commonlib.api.** { *; }` | **评估删除** | 若 Retrofit 版本已带官方 `retrofit2` 的 consumer 规则，接口方法签名通常已被保留；删除后需跑 release 包与接口调用回归。 |
| `-keep class * extends androidx.lifecycle.ViewModel` / `AndroidViewModel` 构造函数 | **评估删除** | 若仅使用 `ViewModel` 的常规用法，依赖与默认规则往往已足够；若使用自定义 `Factory`/`SavedStateHandle` 等反射路径，再按需加**单类**规则。 |
| `-keep public class * extends android.app.Activity` / `Service` / `BroadcastReceiver` / `ContentProvider` | **删除** | 清单声明的组件由构建与 R8 的默认处理链保留；此类宽泛规则会扩大保留范围。 |
| `-keep class * implements android.os.Parcelable { … Creator }` | **评估删除** | 若模型已使用 `@Parcelize` 与 `kotlin-parcelize`，默认优化文件与插件会处理；若仍有手写 `Parcelable`，可只对具体类保留。 |
| `-keepclassmembers class * implements java.io.Serializable`（长模板） | **评估删除** | 仅对确实参与 Java 序列化且仍崩溃的类再收窄保留。 |

### 3. 与 WebView / JS 相关（中等）

| 规则 | 建议动作 | 理由 |
|------|----------|------|
| `@JavascriptInterface` 方法、`WebViewClient` 若干 `public void *` | **保留或收窄** | 若业务里确有 JS 桥与 WebView，需保留被调用方法；若可列出具体类名，应用类级规则替代通配 `class *`。 |

### 4. 调试与映射（低业务风险）

| 规则 | 建议动作 | 理由 |
|------|----------|------|
| `-keepattributes SourceFile,LineNumberTable`、`-renamesourcefileattribute`、`-printmapping` | **保留** | 有利于崩溃堆栈还原；`printmapping` 与 CI 归档策略一致即可。 |

### 5. 其它常见模板

| 规则 | 建议动作 | 理由 |
|------|----------|------|
| `native <methods>`、`enum` 的 `values`/`valueOf`、`R$*` 字段 | **按需保留** | 有 JNI 或反射读枚举时保留；若仅标准用法，可尝试删除后验证。 |

---

## `common_core/proguard-rules.pro`：规则审阅与建议动作

### 1. 与 Kotlin / AndroidX / Retrofit / Coroutines 官方 consumer 重叠

| 规则 | 建议动作 | 理由 |
|------|----------|------|
| `kotlinx.coroutines` 下 `MainDispatcherFactory`、`CoroutineExceptionHandler` 等 `-keepnames` | **删除** | 新版协程库已自带 consumer 规则，手写 broad keep 会削弱优化空间。 |
| `-keep class okhttp3.internal.** { *; }` | **收窄或删除** | 包级保留 `okhttp3.internal` 影响面大；优先依赖 OkHttp 自带规则，仅对仍报错的内部类做 `dontwarn` 或单类。 |
| `-keep,allowobfuscation,allowshrinking interface retrofit2.Call` / `class retrofit2.Response` / `Continuation` | **评估删除** | Retrofit 自带规则通常已覆盖；删除后需网络层回归。 |
| `-dontwarn retrofit2.**` | **保留** | 常与版本组合有关；若删除后无告警可再精简。 |

### 2. 第三方 SDK（多为 JNI / 反射，需单独验证）

| 规则 | 建议动作 | 理由 |
|------|----------|------|
| `-keep class com.tencent.mmkv.**`、AgentWeb、Coil decode、Utilcodex、Logger、ProgressManager、XXPermissions、Chucker、OkHttp Profiler、SAF 等 | **暂保留，逐个验证** | 若官方文档或 release 说明已包含 consumer 规则，可删除重复项；否则保留至确认无 `ClassNotFoundException`。 |

### 3. kotlinx.serialization（与 `com.xueh` 前缀）

| 规则 | 建议动作 | 理由 |
|------|----------|------|
| `-keep @kotlinx.serialization.Serializable class com.xueh.comm_core.** { *; }` | **与 app 侧一致** | 若可收紧到 `entity`/`dto` 等子包，可减少保留范围。 |
| `$serializer` / `$$serializer` 的 `keepnames` | **保留** | 与运行时查找 `KSerializer` 相关，删除前需充分测试序列化路径。 |

---

## 子规则包含关系（简要）

- `app` 中对 `Activity`/`Service`/`Receiver`/`Provider` 的**四类** `-keep` 与 `androidx.lifecycle` 的 `ViewModel` **若同时存在**，前者在「清单组件」上与默认工具链重叠，**优先删除组件类模板**再观察；`ViewModel` 规则单独验证。
- `common_core` 中同时存在 `retrofit2` 的 `dontwarn` 与对 `Call`/`Response` 的显式 `keep` 时，**若移除显式 keep** 后构建与运行正常，应**移除显式 keep**，保留 `dontwarn` 直至依赖升级。

---

## 回归建议

- 在 **release** 包（已开启 minify）上跑：冷启动、网络请求、序列化、含 WebView/JS 的页面、图片加载、权限与存储。
- 若团队有自动化，可对 **修改过 keep 规则** 所涉包做 UI 回归（技能文档建议关注 Instrumentation 等）。
