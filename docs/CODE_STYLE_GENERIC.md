# 通用代码风格（Kotlin / Jetpack Compose）

本文档与具体仓库解耦，可**原样复制**到任意项目（例如项目 B）的 `docs/` 或 wiki，仅按需改「参考模块名」与资源路径说明。适用于 **Android + Kotlin + Jetpack Compose + ViewModel** 的 UI 层约定。

在 Cursor 等工具中可将本文件 `@` 引用，作为 AI 与协作者的统一约束。

---

## 总原则

- **以本仓库的「参考模块」为准**：在仓库内指定一个**结构最清晰、已落地**的功能模块（如 `feature/chat`），其它模块对齐其分层、状态方式与注释习惯，而不是每人一套风格。
- **少文件、少抽象**：不新增**仅装常量**的独立文件；常量写在**当前功能所在的同一源文件**（顶部 `private const val` / `internal val` 或紧邻使用处）。
- **少常量、少重复**：只保留对行为/产品一致有意义的数字；只出现一次且含义明显的不必强行命名。
- **ViewModel 与局部状态**：`ViewModel` 只放**业务层共用**状态（多子组件共享、需 survive 配置变更、与网络/持久化/会话相关）。**仅某一组件自己用的**展开/折叠、动画相位、拖拽中间量、焦点、列表滚动状态、弹层是否打开等，用组件内的 **`remember` / `mutableStateOf` / `Animatable`** 处理，**不要**往 `ViewModel` 里堆。减少无意义状态字段。
- **少回调**：能在**组件内部**完成的（读 `ViewModel`、`derivedStateOf`、局部 `remember`）就在内部完成；**不要**把同一类逻辑拆成多个 `onXxx` 从父组件传入。父级只保留真正**跨页面/跨模块**的入口（导航、路由、必须由宿主决定的策略）；**禁止**在单个 Composable 上堆一长串 lambda 当「伪 ViewModel」。
- **跨端资源一致**：若设计或实现以**另一平台工程**（如 iOS）或**独立设计交付物**中的本地资源为准，将对应资源**按本工程规范复制**入资源目录（Android：`res/drawable*`、`res/raw` 等），并在 KDoc 中注明来源或资产名。
- **注释与业务流程**：见下文专节——**要写清代码在业务里干什么、主路径怎么走**，避免只写代码不交代上下文。
- **Compose 性能**：见「Compose 性能与重组」——**控制重组范围与列表行为**，避免组合阶段重活；文末 **「易忽略：remember、可变列表与依赖链」**。
- **工程与质量（协程、无障碍、安全等）**：见文末 **「附录：工程与质量」**。
- **主题、Insets、Preview、R8 等**：见 **「附录：其它细节」**。
- **公用组件与工具类**：见「目录与分层」——**跨模块复用的 UI** 与**无业务耦合的工具**要抽干净，放在约定目录（如 `helper/`、`utils/`、`ui/components/`）。
- **Navigation 3（Nav3）**：见 **「附录：其它细节」** 中 **Navigation 3** 小节——**少把业务导航回调堆到首页**，跳转处写**可检索注释**。

---

## 目录与分层（建议）

按实际项目调整命名，常见形态：

- **页面 / 协调层**：`XxxPage`、`XxxViewModel`、聚合状态类型（如 `XxxUiState`）。
- **功能内 `ui/`**：当前 feature 专用子界面；**跨 feature 复用**的 Compose 组件放在 **`ui/components/`**（或共享模块 `common_ui` / `common_core`），**不要**在各业务目录复制粘贴多份。
- **`manager/`、`repository/`、`data/`**：长生命周期、IO、数据源；**与 UI 状态边界**见下节。
- **`helper/` 与 `utils/`（或项目统一其一）**：**与具体页面无关**的纯工具——时间/格式化、通用扩展、节流、轻量封装（如震动、剪贴板封装）等；**单一职责、无业务状态**，与某业务强绑定的逻辑**不要**塞进「大杂烩」工具类，应留在对应 feature 或 `Manager`。若 `helper` 随项目变大，**按领域分子包**（例如 `helper/time/`、`helper/analytics/`），**不要**在单文件里无限堆 `fun`，避免 **God Helper**。

  **已有 `helper/` 拆分参考**（`common_core`）：

  | 子包 / 文件 | 职责 | 说明 |
  |-------------|------|------|
  | `ExtHelper.kt` | Null 校验、文件操作、Boolean DSL、Toast、时间格式化 | 历史大杂烩，新增工具**不要**往里堆，按领域拆到子包 |
  | `helper/compose/` | Compose 专用：权限、截图、Paging、滚动监听、`OrderedStateMap` | 与 Compose Runtime 耦合的工具 |
  | `helper/coroutine/` | `launchSafety`、`GlobalCoroutineExceptionHandler` | 协程基础设施 |
  | （已删）`helper/activityresult/` | — | 曾含 TakeCameraUri / CROP 封装；已废弃，请直接使用 `ActivityResultContracts`，演示见 app `PhotoFilePickerDemoScreen` |

  **新增工具函数时**：先判断属于哪个领域子包，放到对应位置；不属于已有子包时新建子包，而非继续往 `ExtHelper.kt` 里追加。

**公用封装原则**：抽离后**依赖方向清晰**（工具不反向依赖具体 `ViewModel` / 页面）；命名能一眼看出用途；需要被多模块引用时考虑 `internal` / `api` 模块边界。

**跨模块 UI 组件（`ui/components/` 等）的 API 演进**：参数或行为发生**破坏性变更**时，须在 KDoc 写清**迁移方式**，或先 **`@Deprecated`** 保留旧入口、给调用方缓冲期；避免多 feature 同时编译失败或线上行为静默变化。

**ViewModel 负责协调；Manager/Repository 负责通道与副作用。** 与**业务/会话**相关的**多字段组合**快照可用 **`data class` / `UiState`**；**单一局部状态**留在 Composable 内（见下节「Compose 与状态」）。

---

## ViewModel 里放什么、组件里放什么

| 放在 **ViewModel**（业务共用） | 放在 **组件内部**（`remember` 等） |
|-------------------------------|-----------------------------------|
| 列表数据、连接状态、当前会话、需上报的分析字段 | 单个控件内的测量、某张卡是否展开 |
| 与 Repository / 网络 / Manager 同步的数据 | 拖拽过程中的临时 `offset`、长按菜单锚点 |
| 多个兄弟组件都要读的同一份展示数据 | 仅本 Composable 子树使用的动画、开关 |

原则：**能下放则下放**（离 UI 越近越好）；只有多子组件需一致或业务需持久化时，再进 `ViewModel`。避免「什么都在 VM 里」导致文件臃肿、重组范围变大。

---

## Compose 与状态

- **`data class`（组合状态）**：仅在**业务上需要多个状态字段一起表示、一起传递**时使用（例如输入栏同时有文案、选图列表、模式切换等），用 **`data class` + 默认参数** 做聚合快照。**若只有单一状态**（一个布尔、一个字符串、一个枚举），用组件内的 **`remember { mutableStateOf(...) }`** 即可，**不要**为单字段再套一层 data class。
- **禁止同一 key 下散落多个 `remember { mutableStateOf }`**：当同一个 Composable 内**3 个及以上** `remember(sameKey)` 共享相同 key 时，**必须**收拢为一个 **`data class` + 单个 `remember`**，用 `copy()` 更新。散落的多个 `var xxx by remember` 难以维护、容易遗漏 key、且无法整体传递。

  ```kotlin
  // ❌ BAD — 散落多个 remember，难维护
  var showPopupA by remember(id) { mutableStateOf(false) }
  var showPopupB by remember(id) { mutableStateOf(false) }
  var selectedIndex by remember(id) { mutableStateOf<Int?>(null) }
  var currentKind by remember(id) { mutableStateOf<Kind?>(null) }

  // ✅ GOOD — 收拢为 data class
  data class PanelUiState(
      val showPopupA: Boolean = false,
      val showPopupB: Boolean = false,
      val selectedIndex: Int? = null,
      val currentKind: Kind? = null,
  )
  var panelState by remember(id) { mutableStateOf(PanelUiState()) }
  // 更新：panelState = panelState.copy(showPopupA = true)
  ```
- **组件能消化的业务**：若逻辑只影响当前 subtree、且已有 `ViewModel` 入口，在**组件内**直接调用 `viewModel.xxx()`、在内部 `derivedStateOf` 派生即可，**不要**再拆成父组件状态 + 回调。
- 派生展示用 **`remember { derivedStateOf { ... } }`**，只订阅与当前 Composable 相关的字段，避免列表项因无关状态变化大面积重组。
- **组件内部优先**：子 Composable 若已持有 `ViewModel`，则点击、手势、子状态更新在**本组件内**完成，**不要**再向父组件暴露大量 `onClickFoo`、`onDismissBar` 等让父级转发。
- 仅当行为必须由**上层**决定时（例如打开系统设置、导航到其它目的地）才用少量回调；其余留在组件内。

---

## Compose 性能与重组

Compose 按**状态快照**调度重组：**谁读了 `State` / `Snapshot`，谁在依赖变化时可能重跑**。优化目标是**缩小重组范围、降低重组频率、避免组合阶段做重活**。

### 缩小重组范围

- **`remember { derivedStateOf { ... } }`**：从同一组上游状态派生展示用字段时，只在**真正用到的子字段**变化时让依赖它的 Composable 失效；列表项内尤其要避免「父级任意状态一变，所有 item 全重组」。
- **状态读取下沉**：能在**子组件内** `collectAsState()` / 读 `State` 的，不要先在**父组件**读再整包 `param` 往下传（除非子树确实需要整包）。读取得越靠近叶子，重组范围越小。
- **`remember { ... }`**：缓存**计算结果**或**稳定引用**（如 `LazyListState`、解析后的列表），避免每次重组重复创建对象或子 lambda 导致子项误以为入参变了。

### 列表（`LazyColumn` / `LazyRow`）

- **`items` / `itemsIndexed` 提供稳定 `key`**（业务 id），避免插入/删除时整表错位或多余重组。
- item 内容 Composable **保持参数稳定**：对会频繁变的回调，必要时在 item 内 `rememberUpdatedState` 或把逻辑收进子组件，避免父级每帧传新 lambda。

### 类型与参数稳定性（概念）

- 传入 Composable 的**列表、数据类**：尽量用**不可变快照**；若某类型导致**频繁整树重组**，再考虑 `@Immutable` / `@Stable`（以官方说明与基准为准，不滥用）。
- **避免**在 `@Composable` 里无 `remember` 地 `list.filter { }` 创建新 `List` 又作为参数传给子组件——除非子组件能稳定接受；优先 **`remember(keys) { ... }`** 或在上层 `ViewModel` 里算好再下发。

### 副作用与重计算

- **`LaunchedEffect(key)` / `DisposableEffect`**：`key` 写全依赖，避免漏重启或多余执行；副作用（网络、订阅、动画启动）不要堆在「纯组合」路径里随手 `launch`。
- **重 CPU / IO**：不进 `Composable` 主路径阻塞组合；用 **`LaunchedEffect` + `withContext`**、或在 **`ViewModel`** 里算完再以状态形式下来。

### 排查

- 开发阶段可用 **Layout Inspector**、系统开发者选项中的 **显示重组计数** 等工具观察热点；列表滚动卡顿优先查 **item 重组次数** 与 **是否在读过大粒度状态**。

### 易忽略：`remember`、可变列表与依赖链

- **`remember(key1, key2) { ... }`**：当闭包依赖的**外部值**会变化时，必须把参与计算的量放进 **`remember` 的参数列表**（作为 key），否则可能**长期持有过期闭包**、读到旧状态。与 **`derivedStateOf`** 搭配时，要想清楚**依赖链**：谁在订阅谁、重组时读到的快照是否仍是预期。
- **`SnapshotStateList` / ViewModel 内可变列表**：列表项内的 **`derivedStateOf`** 应只读与**当前 item** 相关的字段（如 `messageId`、本条状态）；避免在 item 里无必要地依赖**整个列表引用**——若父级频繁替换列表实例或大范围 `notify`，会导致**过多 item 失效**。能immutable 快照下发的优先用不可变快照 + 稳定 `key`。

---

## 常量与视觉（不写独立常量文件）

- **禁止**为常量单独新增文件（例如 `FooConstants.kt`）；常量写在**使用该功能的 `.kt` 文件内**（`private const val` 或同一文件内、与多段代码强相关的 `object`）。
- 色值、圆角、dp：优先 **同一 Composable 文件顶部** 用 `internal val` / 局部 `val`；多文件复用时再集中到**已有**状态/主题文件，而非新建「仅常量」文件。

---

## 注释与业务流程

### 文件头（KDoc）：职责 + 大致流程

- **本文件负责什么**、与哪些类协作（上一跳 / 下一跳），读代码的人应能在**不逐行跟读**的情况下建立心智模型。
- **大致业务流程**（至少覆盖主路径）：**入口**（用户操作、推送、生命周期）→ **关键步骤**（校验、请求、写库、刷新 UI）→ **结果或分支**。复杂时用 **有序列表** 或 **ASCII 图**，不必画得很细，但要让人知道「先干什么、再干什么」。
- **数据流**：状态从 `ViewModel` / `Repository` / `Manager` 哪来、到哪去；若与**其它端**（如 iOS）或**产品文档**有对应关系，在 KDoc 里**点名**（类名、模块名、文档链接均可）。

### 类与长函数

- **ViewModel / Manager / Repository**：在类 KDoc 或 `init` / 主入口旁，用短列表写清 **init 顺序**、**发送链路**、**接收链路**（谁回调谁），避免后来者只能靠搜调用栈猜。
- **复杂分支、与后端约定、历史坑**：用 **`//` 行注释** 写**原因**或**不变量**，不写「这里在做 if 判断」这类重复代码的废话。

### Composable

- **大组件 / 页面**：文件头 KDoc 写 **UI 层级**（从上到下、从外到内）或 **与业务模块的对应**；与路由、权限、埋点相关的**业务含义**可写一句。
- **长文件**：用 **`// ──`** 或 **`// ======`** 分段，段名对应一块业务或一块 UI。

### 不写什么

- 不为了「有注释」而注释；**显而易见**的变量名、单行逻辑优先靠命名表达。
- 注释语言与团队约定一致（见下节「书写语言」）。

---

## Manager 与 UI 层回调

- **UI 组件**：避免「组件内部一堆 `onXxx`」；与 **数据层 ↔ ViewModel** 的边界不同。
- Manager / Repository 与 ViewModel 之间用 **接口/回调** 收拢事件；协议与重连等留在数据层，不在 Page 里堆实现细节。

---

## 书写语言（团队可改）

- **标识符**：英文。
- **注释与 KDoc**：团队统一语言（例如中文）。

---

## 落地到新项目（B）时

1. 将本文件复制到项目 B 的 `docs/CODE_STYLE.md`（或任意名称）。
2. 在 README 或贡献指南中加一行链接，并指定**本仓库的参考模块路径**（例如 `app/.../feature/xxx/`）。
3. 在 Cursor / Copilot 规则中 `@` 该文件，或写入 `.cursor/rules`，便于 AI 自动遵守。

---

## 附录：工程与质量（可选）

以下为与**界面代码风格**并列、建议在项目内统一的工程约定；细节过长时可单独拆成 `CONTRIBUTING.md` / `SECURITY.md`，此处只列**可执行摘要**。

### 协程与作用域

- 网络、持久化、与页面生命周期绑定的任务：用 **`viewModelScope`**（或注入的 **`applicationScope`**）；Composable 内一次性副作用用 **`rememberCoroutineScope`**。
- **禁止**在业务代码中使用 **`GlobalScope`**（无界协程，难取消、易泄漏）。

### 可测试性

- `ViewModel` 内优先 **可注入依赖**（Repository / 接口），核心分支逻辑可单测；Compose 中复杂条件可抽 **无 `@Composable` 的纯函数** 再测。

### 无障碍与触控

- 图标/纯图片按钮设置合理 **`contentDescription`**（ decorative 图标可按团队约定为 `null` 并集中说明）。
- 关键可点区域尽量满足 **最小触控目标**（常见约 **48dp**），避免难以点击。

### 文案与多语言

- 用户可见字符串放入 **`strings.xml`**（及多 `values-*`），**不要**在 Kotlin 里硬编码中英文；与姊妹端对齐时注明**文案来源与同步方式**。

### 错误与空态

- 网络失败、权限拒绝等须有 **用户可读提示**；列表区分 **加载中 / 空 / 失败**，避免只实现成功路径。

### 媒体与内存

- 大图使用 **Coil / Glide** 等库的 **尺寸约束**，避免整图解码进内存；`Bitmap` 生命周期交给库或明确回收策略。
- Lottie、流式订阅、动画：在 **页面退出或 `DisposableEffect`** 中取消，避免泄漏与后台耗电。

### 导航与安全（详见仓库级文档）

- **导航**：路由参数尽量 **类型安全**（类型化 Navigation 或封装）；深链/通知冷启进入某页时，在 KDoc 注明 **参数与 ViewModel 初始化顺序**，避免偶现空白页。使用 **Navigation 3（Nav3）** 时另见下文 **「附录：其它细节」** 中专节。
- **安全**：密钥与 Token **不入仓库**；日志**不输出隐私与完整 Token**；传输层策略（HTTPS / WSS 等）与后端一致。具体以项目 **README**、**`SECURITY.md`** 或内部规范为准。

---

## 附录：其它细节（可选）

以下为前文未展开、但在 Compose / 上架场景中**常被问到**的条目；各条一句可执行结论，深入实现以官方文档为准。

### 主题与颜色

- 业务 UI 优先使用 **`MaterialTheme`** / 项目 **`theme`** 中的色板与字重，避免在业务 Composable 里散落硬编码 `Color(0xFF……)`；若必须与姊妹端**像素级对齐**且已审阅，可在局部保留并在 KDoc 说明对照关系。

### 配置变更与状态保留

- **`remember`** 在**旋转、语言切换**等配置变更后会丢失；需跨配置保留的轻量 UI 状态用 **`rememberSaveable`**，其余仍按「业务进 `ViewModel`、纯 UI 进 `remember`」分界。

### 窗口、边衬与键盘

- 全屏 / **edge-to-edge** 时处理 **`WindowInsets`**（状态栏、导航栏、刘海）；含输入框的页面为 **`ime`（键盘）** 预留空间（如 **`imePadding()`**），避免按钮与内容被遮挡。

### `@Preview`

- 为复杂 Composable 提供 **`@Preview`** 时，使用 **假数据 / 静态模型**，避免 Preview 依赖真实网络、单例或 `ViewModel`；必要时拆 **仅 UI** 的无业务 Preview 函数。

### Release 与混淆（R8）

- Release 构建启用 **R8**；涉及 **反射、序列化、Compose 导航** 等时，按依赖库文档维护 **keep / consumer rules**，发版前在 **minify** 包上做一次**冒烟**。

### 埋点与隐私

- 分析事件与 **隐私政策 / 用户同意** 范围一致；未获同意前不采集**可识别个人**的敏感行为；关键转化点与产品对齐**事件名与参数**（可在 KDoc 或单独埋点表维护）。

### 高频交互

- 搜索、自动保存、发送等易**抖动**的输入：在 `ViewModel` 或数据层对 Flow **`debounce`**；按钮可对关键操作做**短时间防重复点击**（避免双请求）。

### Navigation 3（Nav3）

与全文的 **「少回调」** 一致：导航逻辑**不要**默认全部「抛回首页 / 根容器」再由首页转发到目标页，否则根 Composable 会堆满 **`onNavigateToXxx`**，与业务页脱节、难维护。

- **业务在发生处处理**：能在**当前页或其 `ViewModel`** 内根据状态直接 `navigate`（或通过注入的 **`Navigation` / 封装好的 `Navigator` 接口**）的，就在**该 feature** 完成；首页 / Shell 只保留 **真正的全局入口**（如登出后回登录、deeplink 统一入口、未实现路由的兜底）。
- **减少经首页中转**：若团队当前习惯「一律回调到首页再跳转」，可逐步改为：**类型化路由** + **各模块持有导航能力**（与 DI 一致），避免一层层把 lambda 传到根节点。
- **跳转处写清注释，便于全局搜索**：
  - 在 **`navigate(...)` 调用旁**或 **路由注册处**用 **`// NAV:`** 或 **`// Route:`** 前缀 + **源 → 目标** + 必要参数说明（示例：`// NAV: Relationship -> EditRelation (relationId)`）。
  - **目标 Composable / 对应 `route` 字符串**在文件头 **KDoc** 里写一句「谁可以 navigate 进来、必传参数」，与 **Nav3 的 destination 定义**可交叉检索。
  - 路由常量 / sealed 类集中定义时，在该文件 **KDoc** 说明 **与产品流程或 PRD 章节** 的对应（可选），方便后人 **`grep "NAV:"` / `Route:`** 定位。

### 其它（按需查阅）

- **返回与手势**：系统返回、`BackHandler`、预测性返回与页面栈一致。
- **进程被杀与恢复**：极重要 UI 状态除 VM 外可配合 **`SavedStateHandle`** / `rememberSaveable`。
- **调试**：Debug 包可开 **StrictMode**、**LeakCanary**（按团队接受度），辅助发现主线程 IO 与泄漏。

---

## 与本仓库的关系

- **BestieAI-Android** 在 [`CODE_STYLE_CONVERSATION.md`](CODE_STYLE_CONVERSATION.md) 中保留了**本仓库**具体路径与 `conversation` 模块示例；**通用条目以本文件为准**，仓库专用文档可视为其特化版。
- 工程与质量附录适用于本仓库；若仓库另有 **AGENTS.md / 安全清单**，与之**同时遵守**，冲突时以仓库专用文档为准。
