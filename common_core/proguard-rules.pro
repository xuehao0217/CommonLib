# =============================================================================
# common_core 库 — consumer ProGuard / R8 规则
# 通过 build.gradle.kts 的 consumerProguardFiles 打入 AAR，app 开启 minify 时会自动合并。
# 业务包名：com.xueh.comm_core（库） / com.xueh.commonlib（app 模块实体与 Api 建议写在 app/proguard-rules.pro）
# =============================================================================

# -----------------------------------------------------------------------------
# Kotlin 元数据与反射（序列化、泛型签名）
# -----------------------------------------------------------------------------
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleTypeAnnotations,RuntimeInvisibleTypeAnnotations,Signature,InnerClasses,EnclosingMethod,*Annotation*

# -----------------------------------------------------------------------------
# kotlinx.coroutines
# -----------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# -----------------------------------------------------------------------------
# kotlinx.serialization（配合 Retrofit converter-kotlinx-serialization）
# -----------------------------------------------------------------------------
-dontnote kotlinx.serialization.AnnotationsKt

# 生成器与 Companion（供运行时查找 KSerializer）
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
}

# 编译器生成的 $serializer / $$serializer（包名随模块而定，按 com.xueh 前缀保留）
-keepnames class com.xueh.**$$serializer { *; }
-keepnames class com.xueh.**$serializer { *; }
-keepnames class com.xueh.**$*Serializer { *; }

# 对外 API / 网络模型：允许压缩与混淆成员时仍保留序列化描述（可按需收紧到 entity / net 子包）
-keep @kotlinx.serialization.Serializable class com.xueh.comm_core.** { *; }

# -----------------------------------------------------------------------------
# Retrofit 3 + OkHttp 5
# -----------------------------------------------------------------------------
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.** { *; }
-dontwarn okio.**

-dontwarn retrofit2.**
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# -----------------------------------------------------------------------------
# Coil 3（io.coil-kt.coil3）
# -----------------------------------------------------------------------------
-dontwarn coil3.**
-keep class coil3.decode.** { *; }

# -----------------------------------------------------------------------------
# MMKV
# -----------------------------------------------------------------------------
-keep class com.tencent.mmkv.** { *; }
-dontwarn com.tencent.mmkv.**

# -----------------------------------------------------------------------------
# AgentWeb
# -----------------------------------------------------------------------------
-keep class com.just.agentweb.** { *; }
-dontwarn com.just.agentweb.**

# -----------------------------------------------------------------------------
# Logger（com.orhanobut:logger）
# -----------------------------------------------------------------------------
-keep class com.orhanobut.logger.** { *; }

# -----------------------------------------------------------------------------
# Blankj Utilcodex
# -----------------------------------------------------------------------------
-keep class com.blankj.utilcode.** { *; }
-dontwarn com.blankj.utilcode.**

# -----------------------------------------------------------------------------
# ProgressManager
# -----------------------------------------------------------------------------
-keep class me.jessyan.progressmanager.** { *; }
-keep interface me.jessyan.progressmanager.** { *; }

# -----------------------------------------------------------------------------
# XXPermissions（com.github.getActivity:XXPermissions，包名多为 com.hjq.permissions）
# -----------------------------------------------------------------------------
-keep class com.hjq.permissions.** { *; }

# -----------------------------------------------------------------------------
# Chucker（debug 全量 / release no-op，避免 R8 告警）
# -----------------------------------------------------------------------------
-dontwarn com.chuckerteam.chucker.**

# -----------------------------------------------------------------------------
# OkHttp Profiler
# -----------------------------------------------------------------------------
-keep class io.nerdythings.okhttp.profiler.** { *; }
-dontwarn io.nerdythings.okhttp.profiler.**

# -----------------------------------------------------------------------------
# SAF LoggingInterceptor（com.github.fengzhizi715:saf-logginginterceptor）
# -----------------------------------------------------------------------------
-keep class com.safframework.http.** { *; }
-dontwarn com.safframework.http.**

# -----------------------------------------------------------------------------
# LeakCanary（debug 依赖，release 可不打进包；防误合并时告警）
# -----------------------------------------------------------------------------
-dontwarn leakcanary.**
-dontwarn com.squareup.leakcanary.**
