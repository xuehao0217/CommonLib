# =============================================================================
# common_core 库 — consumer ProGuard / R8 规则（随 AAR 合并到 app）
# =============================================================================

# Kotlin 元数据（序列化、泛型签名）
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleTypeAnnotations,RuntimeInvisibleTypeAnnotations,Signature,InnerClasses,EnclosingMethod,*Annotation*

# kotlinx.coroutines：1.7+ 自带 META-INF/proguard，不再手写 broad keep。

# -----------------------------------------------------------------------------
# kotlinx.serialization（converter-kotlinx-serialization）
# -----------------------------------------------------------------------------
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
}

-keepnames class com.xueh.**$$serializer { *; }
-keepnames class com.xueh.**$serializer { *; }
-keepnames class com.xueh.**$*Serializer { *; }

# 若 release 出现序列化异常，再为 com.xueh.comm_core 下具体 entity 包补 -keep @kotlinx.serialization.Serializable。

# -----------------------------------------------------------------------------
# OkHttp 5 / Retrofit 3：库自带 consumer 规则；仅保留平台/可选依赖的 dontwarn
# -----------------------------------------------------------------------------
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

-dontwarn okhttp3.logging.**
-dontwarn okio.**

-dontwarn retrofit2.**

# 不再 -keep okhttp3.internal.** / retrofit2.Call / Continuation（由依赖规则处理）

# -----------------------------------------------------------------------------
# Coil 3
# -----------------------------------------------------------------------------
-dontwarn coil3.**

# -----------------------------------------------------------------------------
# MMKV / AgentWeb / 工具库（JNI 或反射较多，保留类级 keep）
# -----------------------------------------------------------------------------
-keep class com.tencent.mmkv.** { *; }
-dontwarn com.tencent.mmkv.**

-keep class com.just.agentweb.** { *; }
-dontwarn com.just.agentweb.**

-keep class com.orhanobut.logger.** { *; }

-keep class com.blankj.utilcode.** { *; }
-dontwarn com.blankj.utilcode.**

-keep class me.jessyan.progressmanager.** { *; }
-keep interface me.jessyan.progressmanager.** { *; }

-keep class com.hjq.permissions.** { *; }

-dontwarn com.chuckerteam.chucker.**

-keep class io.nerdythings.okhttp.profiler.** { *; }
-dontwarn io.nerdythings.okhttp.profiler.**

-keep class com.safframework.http.** { *; }
-dontwarn com.safframework.http.**

-dontwarn leakcanary.**
-dontwarn com.squareup.leakcanary.**
