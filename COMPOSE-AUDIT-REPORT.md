# Jetpack Compose Audit Report

Target: `/Users/macbookpro/Documents/MY/CommonLib`（`:app` + `:common_core`）  
Date: 2026-04-07  
Scope: `app/src/main/java/**`、`common_core/src/main/java/**` 中的 Compose 与共享 UI  
Excluded from scoring: 仪器测试 / 仅 JVM 测试（未纳入）  
Confidence: High  
Overall Score: **78/100**

## Scorecard

| Category | Score | Weight | Status | Notes |
|----------|-------|--------|--------|-------|
| Performance | 8/10 | 35% | solid | 懒加载列表普遍带 `key`；已用编译器报告核对 skippability；本次已用 `mutableIntStateOf` 等减少装箱 |
| State management | 8/10 | 25% | solid | ViewModel + `mutableStateOf` 一致；已移除实体上的 Compose `MutableState` |
| Side effects | 8/10 | 20% | solid | `snapshotFlow` + `rememberUpdatedState` 等用法合理 |
| Composable API quality | 7/10 | 20% | solid | 共享组件多带 `Modifier`；Demo 页偏多、部分列表 key 可加强 |

## Critical Findings

1. **（低）Lazy 列表 `key` 来源不够稳定（演示页）**  
   - Why：`key` 若不能随身份稳定变化，易导致错误复用或重复 key 风险。  
   - Evidence: `app/src/main/java/com/xueh/commonlib/ui/compose/PhotoFilePickerDemoScreen.kt`（`key = { it.toString() }` 依赖 `Uri`/`File` 的 `toString()` 语义）  
   - Fix direction: 使用稳定 id（如 `uri.toString()` 已常够用，但跨场景更稳妥的是业务主键或 `rememberSaveable` 与索引组合策略）。  
   - References: <https://developer.android.com/develop/ui/compose/lists>

2. **（低）模块级 `skippable%` 被 lambda 形 composable 拉低（度量噪声）**  
   - Why：`app` 模块 `*-module.json` 中 `skippableComposables/restartableComposables` 比例偏低，但 `*-composables.csv` 在过滤 `isLambda==0` 后，**具名 composable 的 skippable 占比为 100%**（与 Strong Skipping 一致）。  
   - Evidence: `app/build/compose_audit/debug/app-module.json`、`common_core/build/compose_audit/debug/common_core-module.json`；合并 `app-composables.csv` + `common_core-composables.csv` 的 named-only 统计。  
   - Fix direction: 评估热点路径上的非 skippable 具名函数（若有），而非仅看模块整体比例。  
   - References: <https://developer.android.com/develop/ui/compose/performance/stability/strongskipping>

## Category Details

### Performance — 8/10

**What is working**

- `Material3PlaygroundDemoScreen` 等使用 `item(key = "...")` 分段；`PagingWidget` / `ComposeListWidget` 等对列表项提供 `key`。  
- 本次将多处 `mutableStateOf` 整型/浮点/长整型改为 `mutableIntStateOf` / `mutableFloatStateOf` / `mutableLongStateOf`，减少装箱。  
- References: <https://developer.android.com/develop/ui/compose/state>、<https://developer.android.com/develop/ui/compose/lists>

**What is hurting the score**

- Demo 中仍存在依赖 `toString()` 的列表 key 等可改进点（见 Critical Findings）。  
- heterogeneous 列表未系统使用 `contentType`（若未来混合多类型 item，建议补上）。  
- References: <https://developer.android.com/develop/ui/compose/lists>

**Evidence**

- `app/src/main/java/com/xueh/commonlib/ui/compose/ConstraintSet.kt` — `mutableIntStateOf` 替代 `mutableStateOf<Int>` · References: <https://developer.android.com/develop/ui/compose/state>  
- `common_core/.../ComposeHelper.kt` — `onScrollDirectionChanged` / `LogCompositions` 使用 `mutableIntStateOf` · References: <https://developer.android.com/develop/ui/compose/state>  
- `common_core/.../ComposeExt.kt`、`ComposeDialog.kt` — 防抖与拖拽位移使用 `mutableLongStateOf` / `mutableFloatStateOf` · References: <https://developer.android.com/develop/ui/compose/state>

**Performance ceiling check:**

- `app/build/compose_audit/debug/app-module.json`：`skippableComposables=3`，`restartableComposables=6` → 粗算 50%（含 lambda 锚定）。  
- `common_core/build/compose_audit/debug/common_core-module.json`：`17/24` ≈ 70.8%。  
- **Named-only `skippable%`（`isLambda==0`）**：合并 `app` + `common_core` 的 `*-composables.csv` 得 `sum(skippable)/sum(restartable)=16/16=100%`。  
- 按 `references/scoring.md`：named-only ≥95% 且无不稳定类作为广泛复用组件参数 → **不设 Performance 上限**。  
- `app/build/compose_audit/app-classes.txt`：`BannerVO` 为 **stable**（已移除实体内 `MutableState`）。  
- References: <https://developer.android.com/develop/ui/compose/performance/stability>

### State management — 8/10

**What is working**

- 网络与页面状态集中在 `RequestViewModel` / `BaseRequstViewModel` 等层；未发现 `collectAsState()` 无生命周期版本在 UI 中的使用。  
- `BannerVO` 已改为纯数据类，不再携带 `@Transient` + `MutableState` 字段。  
- References: <https://developer.android.com/develop/ui/compose/architecture>、<https://developer.android.com/develop/ui/compose/state>

**What is hurting the score**

- 若后续为轮播项增加「选中」态，应在 Composable 或 ViewModel 中维护，而非写回 `@Serializable` 实体。  
- References: <https://developer.android.com/develop/ui/compose/state-hoisting>

**Evidence**

- `app/src/main/java/com/xueh/commonlib/entity/BannerVO.kt` — 纯 `val` 字段 · References: <https://developer.android.com/develop/ui/compose/performance/stability>

### Side effects — 8/10

**What is working**

- `onScrollDirectionChanged` 使用 `LaunchedEffect` + `snapshotFlow` + `rememberUpdatedState` 组合，符合侧效应与回调刷新指引。  
- References: <https://developer.android.com/develop/ui/compose/side-effects>

**What is hurting the score**

- 无明显系统性问题；个别页面若后续增加订阅/监听，需保持 `DisposableEffect` 清理对称。  
- References: <https://developer.android.com/develop/ui/compose/side-effects>

**Evidence**

- `common_core/.../ComposeHelper.kt` — `onScrollDirectionChanged` · References: <https://developer.android.com/develop/ui/compose/side-effects>

### Composable API quality — 7/10

**What is working**

- 共享弹窗、列表封装等多暴露 `Modifier` 与显式参数；`common_core` 内可复用组件结构清晰。  
- References: <https://developer.android.com/develop/ui/compose/performance/stability>

**What is hurting the score**

- Demo/Playground 体量大、单文件职责偏重，复用 API 的「边界」不如独立 widget 模块干净（可接受为示例应用）。  
- References: <https://developer.android.com/develop/ui/compose/architecture>

**Evidence**

- `common_core/.../ComposeDialog.kt`、`PagingWidget.kt` 等 — `Modifier` 与参数顺序合理 · References: <https://developer.android.com/develop/ui/compose/performance/stability>

## Prioritized Fixes

1. **（已完成）** 将纯数字状态改为 `mutableIntStateOf` / `mutableLongStateOf` / `mutableFloatStateOf`：`ConstraintSet.kt`、`ComposeHelper.kt`、`ComposeExt.kt`、`ComposeDialog.kt`。  
2. **（已完成）** 从 `BannerVO` 移除未使用且反模式的 Compose `MutableState` 字段。  
3. **（后续）** 审核 `PhotoFilePickerDemoScreen` 等处的 `key` 策略，确保与列表身份一致。  
4. **（后续）** 若混合类型 `LazyColumn` 增多，为不同 item 设置 `contentType`。  
5. **（可选）** 对 `ScreenHeightInDp` 等非 skippable 热点再评估（见 `common_core-composables.txt`）。

## Notes And Limits

- 审计以库 + 示例应用为主，生产业务屏占比较小；若仅统计 `common_core`，结论更偏「组件库质量」。  
- Weight choice: 默认 **35% / 25% / 20% / 20%**（与技能默认一致）。  
- Renormalization: 无。  
- **Compiler diagnostics used: yes**  
  - `app/build/compose_audit/`：`app-classes.txt`、`app-composables.csv`、`debug/app-module.json`  
  - `common_core/build/compose_audit/`：`common_core-classes.txt`、`common_core-composables.csv`、`debug/common_core-module.json`

## Suggested Follow-Up

- 若需对照设计系统，可再跑 Material 3 专项审计（技能说明中的 `material-3`）。  
- 持续在 CI 或本地用同一 `--init-script` 生成报告，跟踪 `skippable%` 回归。
