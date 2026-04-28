---
name: compose-component-api
description: >-
  Jetpack Compose 可复用组件的 API 设计规范：调用处可读性、无状态与状态提升、Modifier 顺序、
  暴露意图而非实现、避免布尔参数泛滥、遵循 Material/Compose 命名与槽位约定、默认简单易扩展。
  触发词包括「Compose 组件 API」「封装组件」「可复用组件」「组件参数设计」「slot」等；
  设计与评审自定义 Composable 公共 API 时应用。
---

# Compose 组件 API 设计规范

本文 skill 提炼自 [API Guidelines for Jetpack Compose UI Components](https://medium.com/@prahaladsharma4u/api-guidelines-for-jetpack-compose-ui-components-821423ffd7e2)（Prahalad Sharma），并与本项目既有约定对齐。

## 为何重要

每个 `@Composable` 既是 UI，也是给别人用的 **API**：影响可读性、可测性、可演进性与误用成本。

设计组件 API 时常问：

- 调用处是否一读就懂？
- 是否难以误用？
- 后续改版能否尽量不破坏调用方？

---

## 1. 调用处可读性（Call-site readability）

代码读远比写多。**禁止**依赖位置语义的多参数调用：

```kotlin
// ❌ 调用处看不出含义
MyButton("Login", true, false) { viewModel.login() }

// ✅ 命名参数 + 语义清晰，读起来像句子
MyButton(
    text = "Login",
    enabled = true,
    showLoader = false,
    onClick = { viewModel.login() },
)
```

**规则：** 对外公共 API 优先使用命名参数；参数名表达业务含义，而非实现细节缩写。

---

## 2. 优先无状态（Stateless）

组件内部 **尽量不** `remember { mutableStateOf }` 承载业务或可共享状态；由调用方传入并在回调中上报变更。

```kotlin
// ❌ 难测、难复用
@Composable fun Counter() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) { Text("Count: $count") }
}

// ✅ 状态外置（通常来自 ViewModel）
@Composable fun Counter(count: Int, onIncrement: () -> Unit) {
    Button(onClick = onIncrement) { Text("Count: $count") }
}
```

**规则：** 与本项目「ViewModel 放业务状态、Composable 用 remember 收局部 UI 状态」一致；可复用库组件倾向完全无状态。

---

## 3. 状态提升（State hoisting）

凡组件会「改变」的数据，都应：**value 下行 + 回调上行**。

```kotlin
// ❌
@Composable fun MyTextField() {
    var text by remember { mutableStateOf("") }
    TextField(value = text, onValueChange = { text = it })
}

// ✅
@Composable fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    TextField(value = value, onValueChange = onValueChange)
}
```

**规则：** 「单向数据流」：`状态在上，事件在上报`。

---

## 4. `Modifier`：第一个可选参数

对外签名中，`modifier: Modifier = Modifier` **放在首位**（在必填参数之后若有团队约定，至少保证 Modifier 有默认值且文档一致；官方 Material 组件普遍为 Modifier 第一个可选参数）。

```kotlin
// ❌ Modifier 夹在中间且无默认值习惯时易调用混乱
@Composable fun ProfileCard(name: String, modifier: Modifier)

// ✅
@Composable fun ProfileCard(
    modifier: Modifier = Modifier,
    name: String,
)
```

**规则：** 便于调用方统一链式定制外观与布局，并与 Compose/Material 生态习惯一致。

---

## 5. 暴露「意图」，不暴露「实现细节」

避免把内部绘制细节全部打成参数（颜色、圆角、阴影粒度堆叠），否则 API 脆弱、主题难以统一。

```kotlin
// ❌ 旋钮过多，易与主题打架
@Composable fun AppButton(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    cornerRadius: Dp,
    elevation: Dp,
)

// ✅ 语义化 + Modifier；细分样式交给 Theme / 少量枚举或类型
@Composable fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
)
```

**规则：** 优先 MaterialTheme、CompositionLocal、或项目内 Design Token；必要时再增加 **少量** 语义化扩展点。

---

## 6. 避免布尔参数泛滥（Boolean explosion）

多个独立 `Boolean` 易产生非法组合（如同时 loading 与 disabled 的语义冲突）。

```kotlin
// ❌
MyButton(enabled = true, isLoading = false, isOutlined = true, isError = false)

// ✅ 合并为有意义的类型（sealed interface / enum / data class）
sealed class ButtonState {
    data object Normal : ButtonState()
    data object Loading : ButtonState()
    data object Disabled : ButtonState()
}
MyButton(state = ButtonState.Loading)
```

**规则：** 与本项目「同一 key 下多个 remember 收拢为 data class」思想一致；对外 API 层用 **状态类型** 代替布尔组合。

---

## 7. 遵循 Material / Compose 惯例

- 交互：`onClick`、`onValueChange` 等命名与框架一致，避免 `onButtonClick` 等自创同义名。
- 槽位（slot）：用 `@Composable () -> Unit` 表示区域内容，如 `topBar`、`content`。
- 默认值：行为与 Material 组件默认值协调，降低认知负担。

**规则：** 非必要不发明新约定；与 `androidx.compose.material3` 对齐。

---

## 8. 默认极简，扩展可选

最少参数即可覆盖最常见路径；进阶定制通过 `Modifier`、slot、可选参数完成。

```kotlin
AppButton(text = "Submit", onClick = { })
```

---

## 参考示例（良好聚合）

```kotlin
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier,
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp))
        } else {
            Text(text)
        }
    }
}
```

---

## 延伸阅读

- 原文：[API Guidelines for Jetpack Compose UI Components](https://medium.com/@prahaladsharma4u/api-guidelines-for-jetpack-compose-ui-components-821423ffd7e2)
- 项目中 related：`docs/CODE_STYLE_GENERIC.md`（若存在）、`.cursor/rules/android-project.mdc`（架构与状态约定）
