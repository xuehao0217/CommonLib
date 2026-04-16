# AGP 9 升级 / 迁移对齐说明

本文与 Cursor 技能 **`android-agp-9-upgrade`**（非 KMP 项目）步骤对齐，用于记录本仓库的落点与结论。

**当前版本（Version Catalog）**：`agp = 9.1.0`，Gradle Wrapper **9.3.1**，Kotlin **2.3.20**（满足 AGP 9 内置 Kotlin 对 KGP 的要求）。

---

## Step 1：依赖与工具版本

| 要求 | 本仓库 |
|------|--------|
| KSP ≥ 2.3.6（若使用 KSP） | 未使用 KSP → 不适用 |
| Hilt ≥ 2.59.2（若使用 Hilt） | 未使用 Hilt → 不适用 |

后续若引入 KSP / Hilt，请先在 `gradle/libs.versions.toml` 中满足上述下限，再接入。

---

## Step 2：迁移到内置 Kotlin（Built-in Kotlin）

| 要求 | 本仓库 |
|------|--------|
| 移除 `org.jetbrains.kotlin.android`（`kotlin-android`） | **已满足**：模块仅使用 `com.android.application` / `com.android.library` + Compose Compiler / Parcelize / Serialization 等，**未**应用 `kotlin-android` |
| `gradle.properties` 勿长期 `android.builtInKotlin=false` | **未设置**（默认内置 Kotlin） |
| `kotlin {}` / JVM | 使用 **`kotlin { jvmToolchain(21) }`**，与 `compileOptions` Java 21 一致 |

---

## Step 3：新 AGP DSL

| 要求 | 本仓库 |
|------|--------|
| 勿依赖已废弃的 `BaseExtension` / 旧 Variant API 类型 | 业务构建脚本中**无** `applicationVariants` / `libraryVariants` 等活跃代码（仅保留注释示例） |
| 勿使用 `android.newDsl=false` 长期兜底 | **未设置**（使用新 DSL；勿依赖 AGP 10 将移除的 opt-out） |
| 自定义产物（如改 APK 名） | 若需实现，应使用 **`androidComponents` / Artifacts API**（参见官方 [Gradle Recipes](https://github.com/android/gradle-recipes) `listenToArtifacts` 等），替换注释中的旧写法 |

---

## Step 4：kapt / KSP / legacy-kapt

| 要求 | 本仓库 |
|------|--------|
| 无 `org.jetbrains.kotlin.kapt` | **已满足**：未使用 kapt |
| 内置 Kotlin 与 kapt 冲突时改用 KSP 或 `com.android.legacy-kapt` | 当前无 kapt → 不适用 |

---

## Step 5：BuildConfig

| 要求 | 本仓库 |
|------|--------|
| `buildFeatures { buildConfig = true }` | **app**、**common_core** 均已开启 |
| 自定义 `buildConfigField` | **当前无**；若增加字段，须使用 AGP 9 推荐的 **`BuildConfigField` / `addCustomBuildConfigFields`** 写法（字符串值须带转义引号，见官方说明） |

---

## Step 6：`gradle.properties` 迁移期开关清理

迁移完成后应**移除**以下过渡项（若曾添加）：

| 属性 | 本仓库 |
|------|--------|
| `android.builtInKotlin` | 未使用 |
| `android.newDsl` | 未使用 |
| `android.uniquePackageNames` | **已移除** |
| `android.enableAppCompileTimeRClass` | 未使用 |

---

## 技能中的 Guidelines

| 规则 | 本仓库 |
|------|--------|
| 勿添加 `android.disallowKotlinSourceSets=false` | **未添加** |

---

## Verification（技能推荐）

在工程根目录执行：

1. `./gradlew help`
2. `./gradlew build --dry-run`

（IDE Sync 需在 Android Studio 中本地确认。）

---

## Troubleshooting

- **Paparazzi** 旧版与 AGP 9 不兼容：本仓库**未**使用 Paparazzi → 不适用。

---

## 参考

- AGP 9.0 发布说明（破坏性变更、兼容性表、内置 Kotlin）：Android Developer 文档  
- 内置 Kotlin 迁移：`migrate-to-built-in-kotlin`  
- 技能内嵌参考：本机 Cursor 技能 **`android-agp-9-upgrade`**（`SKILL.md` + `references/`）
