package com.xueh.commonlib.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation 3 演示目的地：均为可序列化的 [NavKey]。 */
@Serializable
data object DemoActionList : NavKey

@Serializable
data class DemoProfileRoute(val name: String, val age: Int = 18) : NavKey

@Serializable
data object DemoRefreshLoad : NavKey

@Serializable
data object DemoLoginCompose : NavKey

@Serializable
data object DemoConstraintSet : NavKey

@Serializable
data object DemoNavigateParam1 : NavKey

@Serializable
data object DemoNavigateParam2 : NavKey

@Serializable
data object DemoDialog : NavKey

@Serializable
data object DemoCommonTabPager : NavKey

@Serializable
data object DemoCarousel : NavKey

@Serializable
data object DemoComposePermission : NavKey

@Serializable
data object DemoAgentWeb : NavKey

@Serializable
data object DemoParkComposeWeb : NavKey

@Serializable
data object DemoComposeTab : NavKey

@Serializable
data object DemoComposePaging : NavKey

@Serializable
data object DemoVisibilityChanged : NavKey

@Serializable
data object DemoOrderedTabs : NavKey

@Serializable
data object DemoBaseComposeActivityApi : NavKey
