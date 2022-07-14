package com.xueh.commonlib.entity

data class HomeEntity(
    var curPage: Int = 0, // 2
    var datas: List<Data> = listOf(),
    var offset: Int = 0, // 20
    var over: Boolean = false, // false
    var pageCount: Int = 0, // 633
    var size: Int = 0, // 20
    var total: Int = 0 // 12641
) {
    data class Data(
        var apkLink: String = "",
        var audit: Int = 0, // 1
        var author: String = "",
        var canEdit: Boolean = false, // false
        var chapterId: Int = 0, // 502
        var chapterName: String = "", // 自助
        var collect: Boolean = false, // false
        var courseId: Int = 0, // 13
        var desc: String = "",
        var descMd: String = "",
        var envelopePic: String = "",
        var fresh: Boolean = false, // false
        var host: String = "",
        var id: Int = 0, // 23444
        var link: String = "", // https://juejin.cn/post/7117498113983512589
        var niceDate: String = "", // 2天前
        var niceShareDate: String = "", // 2天前
        var origin: String = "",
        var prefix: String = "",
        var projectLink: String = "",
        var publishTime: Long = 0, // 1657527663000
        var realSuperChapterId: Int = 0, // 493
        var selfVisible: Int = 0, // 0
        var shareDate: Long = 0, // 1657527663000
        var shareUser: String = "", // KunMinX
        var superChapterId: Int = 0, // 494
        var superChapterName: String = "", // 广场Tab
        var tags: List<Tag> = listOf(),
        var title: String = "", // Google Android 官方架构示例，我在起跑线等你
        var type: Int = 0, // 0
        var userId: Int = 0, // 12482
        var visible: Int = 0, // 1
        var zan: Int = 0 // 0
    ) {
        data class Tag(
            var name: String = "", // 项目
            var url: String = "" // /project/list/1?cid=294
        )
    }
}