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

## 配合 CODE_STYLE_GENERIC.md

`docs/CODE_STYLE_GENERIC.md` 是完整的编码风格文档，Cursor Rules 是其中核心规则的**精简版**。关系：

- **Rules** — AI 自动加载，精简可执行，覆盖最高频场景
- **CODE_STYLE_GENERIC.md** — 完整参考，需要时用 `@` 手动引用

两者互补，不冲突。
