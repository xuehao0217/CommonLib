# CommonLib 设计说明（Material 3 / Jetpack Compose）

面向 **AI 与协作开发** 的单页设计约定；实现上以 `common_core` 的 `ComposeMaterialTheme`、`Theme.kt`、`Color.kt`、`Type.kt`、`Shape.kt` 为准。格式参考 [Google Stitch DESIGN.md](https://stitch.withgoogle.com/docs/design-md/overview/) 思路。

**平台**：Android，minSdk 24；UI 仅用 **Material 3** Compose 组件，禁止 Material 2 / XML 布局（新业务）。

---

## 1. Visual Theme & Atmosphere

- **基线**：Google **Material Design 3** 组件与语义色（`ColorScheme`），默认 **中等信息密度**、清晰层级。
- **品牌可变**：用户可在 **绿 / 紫 / 蓝 / 橘** 四套固定色与 **壁纸动态色**（Android 12+，`dynamicLightColorScheme` / `dynamicDarkColorScheme`）间切换，见 `AppThemeColorType`。
- **亮暗**：跟随系统、浅色、深色三态，见 `AppThemeType`；`BaseComposeActivity` 根节点为 **边到边（edge-to-edge）**。
- **适配**：根布局按 **设计稿宽度 402 dp** 做宽度等比缩放（`designWidthDp()` 可被子类重写）；`fontScale` 在根主题固定为 **1.0**，避免系统字体缩放破坏稿面。
- **气质**：工具型示例库 + 可复用组件；强调 **可读性**、**一致圆角与间距**，避免装饰性过强的 one-off 样式。

---

## 2. Color Palette & Roles

### 2.1 原始色token（`Color.kt`，与 `Theme.kt` 引用一致）

| Token | Hex | 用途 |
|--------|-----|------|
| green200 | `#A5D6A7` | primaryContainer（绿系亮/暗） |
| green500 | `#4CAF50` | primary（绿系默认主色） |
| green700 | `#388E3C` | onPrimaryContainer / secondaryContainer 等 |
| blue200 | `#9FA8DA` | primary（暗色蓝系） |
| blue500 | `#3F51B5` | primary（亮色蓝系） |
| blue700 | `#303F9F` | primaryContainer / secondaryContainer（蓝系） |
| purple200 | `#B39DDB` | primary（暗色紫系）；`darkThemeColors.theme` |
| purple | `#833AB4` | primary（亮色紫系） |
| purple700 | `#512DA8` | primaryContainer / secondaryContainer（紫系） |
| orange200 | `#FF7961` | primary（暗色橘系） |
| orange500 | `#F44336` | primary（亮色橘系） |
| orange700 | `#BA000D` | primaryContainer / secondaryContainer（橘系） |
| teal200 | `#80DEEA` | secondary（各套 palette 共用） |
| RedBookRed | `#FF2E4D` | `lightThemeColors.theme`（`AppThemeColors` 语义） |
| WhiteBackground | `#FFFFFF` | 浅语义背景 |
| BlackBackground | `#1F1D1D` | 深语义背景 |

### 2.2 Material3 `ColorScheme` 角色（摘要）

- **primary / onPrimary**：品牌主操作、主按钮填充色与其上文字。
- **primaryContainer / onPrimaryContainer**：较低强调的盛放面、选中底等。
- **secondary / onSecondary / secondaryContainer / onSecondaryContainer**：次要强调（当前各套与 teal / 深色 container 搭配为主）。
- **background / onBackground**：传统页面级背景与主内容（部分页面仍依赖 `MaterialTheme.colorScheme.background`；新布局优先 `surface` 系列）。
- **surface / onSurface / surfaceVariant / onSurfaceVariant**：卡片、列表、次级文本层级。
- **error**：`#FF0000`（`Color.Red`），错误状态。
- **壁纸动态色**：启用 `WALLPAPER` 时由系统从壁纸推导完整 `ColorScheme`，替代上述固定主色链。

### 2.3 额外语义（`AppThemeColors`）

- **lightTheme**：`theme=RedBookRed`，`background` 白，`title` 黑，`body` `#666666`，`divider` 浅灰。
- **darkTheme**：`theme=purple200`，`background` `#1F1D1D`，`title` 白，正文与分割线同浅灰逻辑。

---

## 3. Typography Rules

**Compose**：`AppTypography` / `defaultTextStyle`（`Type.kt`），与 Material 3 `Typography` 槽位对齐；正文字体为默认 **sans（设备 Roboto 等）**。

| 角色 | Material 槽位 | 字号 | 字重 | 行高（约） |
|------|----------------|------|------|------------|
| 标题大 | titleLarge | 22 sp | Bold | 28 sp |
| 标题中 | titleMedium | 18 sp | Bold | 24 sp |
| 标题小 | titleSmall | 14 sp | Medium | 20 sp |
| 正文大 | bodyLarge | 16 sp | Normal | （默认） |
| 正文正文 | bodyMedium | 14 sp | Normal | 20 sp |
| 正文小 | bodySmall | 12 sp | Normal | 16 sp |

**原则**：标题与正文层级用 `MaterialTheme.typography.*`；需要与 `AppTheme.textStyle` 一致时用 `AppTheme` 提供的 `LocalTextStyles`。

---

## 4. Component Stylings

- **按钮 / FAB / Chip**：Material 3 对应组件；主色取自 `colorScheme.primary`，禁用态降低透明度或使用 `onSurface` + `surfaceVariant`。
- **卡片 / 表面**：优先 `Card`、`Surface` 与 `surfaceContainer*` 系列颜色（新代码与 M3 容器色一致）；示例页可能仍用纯白 `surface`。
- **输入**：`TextField` / `OutlinedTextField` M3 样式；错误用 `colorScheme.error`。
- **导航**：Navigation 3；顶栏、底栏遵循 M3 高度与内边距惯例。
- **列表**：`LazyColumn` / `LazyRow` 的 **items 必须带稳定 key**。

---

## 5. Layout Principles

- **间距**：优先 **4 dp 的倍数**（与 `Shapes` small/medium 4 dp 圆角一致）；大区块用 16 dp / 24 dp 级台阶。
- **水平安全区**：边到边页面在系统栏与刘海处用 `WindowInsets`（`navigationBarsPadding`、`systemBarsPadding`、`displayCutout` 等按页面需求）。
- **键盘**：根级 `imePadding` 由 `BaseComposeActivity.useRootImePadding` 控制；全屏或特殊页可关闭并在局部处理。

---

## 6. Depth & Elevation

- 以 **Material 3 色调层级**（surface 各层、tonal elevation）为主，避免手写多重阴影栈。
- **圆角**（`Shape.kt`）：**small / medium = 4 dp**，**large = 0 dp**（直角大表面）；新增组件应复用 `MaterialTheme.shapes.*`，不要随意引入未文档化的新圆角规。

---

## 7. Do's and Don'ts

**Do**

- 使用 `MaterialTheme.colorScheme` 与 `MaterialTheme.typography`。
- 新界面遵从本文与 `.cursor/rules/android-project.mdc`。
- 网络与数据层保持项目既有封装（Retrofit 3、kotlinx-serialization 等）。

**Don't**

- 新代码不要引入 **Material 2**（`android.compose.material`）或 **XML 布局**。
- 不要用 `GlobalScope`；ViewModel 用 `viewModelScope` + `apiDSL` / `apiFlow` 等。
- 不要随意硬编码与 `Color.kt` 冲突的品牌色；应扩展主题或 `AppThemeColors`。

---

## 8. Responsive Behavior

- **触控目标**：遵循平台建议，重要可点区域至少约 **48 dp** 触控裕度。
- **宽屏 / 折叠**：在窗口尺寸变化时避免写死像素宽度；可用 `BoxWithConstraints` 或 Navigation 提供的分栏模式。
- **设计稿**：以 **402 dp 宽** 为逻辑基准对接 UI 稿；稿中标注的 dp 在 `BaseComposeActivity` 子类树下可直接对照。

---

## 9. Agent Prompt Guide（给编码 Agent）

快速约束：

- **栈**：Kotlin、Jetpack Compose、Material 3、Navigation 3、`mutableStateOf`（不用 LiveData）。
- **主题**：颜色映射到 `ColorScheme` / `AppTheme`，字号映射到 `Typography` 已有槽位；圆角优先 `Shapes`。
- **改动形状色板时**：同步更新 **`Color.kt` / `Theme.kt`**，并回看本文件 **§2** 表格。

示例提示：

> 在 CommonLib 中新增设置页列表：遵循根目录 `DESIGN.md` 与 Material 3，使用 `MaterialTheme.colorScheme.surfaceContainerLow` 作为卡片底，`titleMedium` 作为分组标题，`bodyLarge` 作为说明正文，圆角用 `MaterialTheme.shapes.medium`。

---

## 参考代码位置

| 内容 | 路径 |
|------|------|
| 调色板与亮暗枚举 | `common_core/.../theme/AppThemeState.kt` |
| 八套 ColorScheme | `common_core/.../theme/Theme.kt` |
| 色常量 | `common_core/.../theme/Color.kt` |
| 字体 | `common_core/.../theme/Type.kt` |
| 形状 | `common_core/.../theme/Shape.kt` |
| 根 Activity 主题与适配 | `common_core/.../BaseComposeActivity.kt` |
