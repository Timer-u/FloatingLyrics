package com.wzvideni.floatinglyrics.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.wzvideni.floatinglyrics.network.model.MusicInfo
import com.wzvideni.floatinglyrics.utils.StringUtility.Companion.jaroWinklerSimilarity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

// QQ音乐搜索
suspend fun qqMusicSearch(
    mediaTitle: String,
    mediaArtist: String,
    mediaAlbum: String,
    numPerPage: Int,
): List<MusicInfo> = withContext(Dispatchers.IO) {
    // QQ音乐搜索请求参数
    val qqMusicSearchRequestData = """{
            "req_0": {
                "method": "DoSearchForQQMusicDesktop",
                "module": "music.search.SearchCgiService",
                "param": {
                    "search_type": 0,
                    "query": "$mediaTitle - $mediaArtist -$mediaAlbum",
                    "page_num": 1,
                    "num_per_page": $numPerPage
                }
            }
        }""".trimIndent() // 删除字符串中的共同缩进

    // 构建QQ音乐搜索请求
    val qqMusicSearchRequest = Request.Builder()
        .url("https://u.y.qq.com/cgi-bin/musicu.fcg")
        .header("Referer", OkHttpClient.QQ_REFERER)
        .header("User-Agent", OkHttpClient.USER_AGENT)
        .post(qqMusicSearchRequestData.toRequestBody()) // 将请求参数转换为请求正文并使用POST请求
        .build()

    // 执行请求并获取响应，使用use自动释放资源
    OkHttpClient.okHttpClient.newCall(qqMusicSearchRequest).execute().use { response: Response ->
        if (response.isSuccessful) {
            response.body?.string()?.let { jsonString: String ->

                // 将JSON字符串转换为JsonObject
                val jsonObject = Gson().fromJson(jsonString, JsonObject::class.java)

                // 以下代码需对应请求出来的json数据来编写
                // 获取list数组
                jsonObject.getAsJsonObject("req_0")
                    .getAsJsonObject("data")
                    .getAsJsonObject("body")
                    .getAsJsonObject("song")
                    .getAsJsonArray("list")?.let { listArray ->
                        // 搜索结果List
                        val resultList: MutableList<MusicInfo> = mutableListOf()

                        // 遍历list数组
                        for (list in listArray) {
                            // 将list对应的JSON字符串转换为JsonObject
                            val listObject = list.asJsonObject
                            // 从JsonObject获取音乐信息
                            getQQMusicInfoFromJsonObject(mediaTitle, listObject, resultList)
                            // 获取list中的grp数组
                            val grpList = listObject.getAsJsonArray("grp")
                            //  遍历grp数组
                            for (grp in grpList) {
                                // 将grp对应的JSON字符串转换为JsonObject
                                val grpListObject = grp.asJsonObject
                                // 从JsonObject获取QQ音乐的信息
                                getQQMusicInfoFromJsonObject(mediaTitle, grpListObject, resultList)
                            }
                        }
                        return@withContext resultList.toList()
                    }
            }
        }
    }
    return@withContext emptyList()
}

// 从JsonObject获取QQ音乐的信息
private fun getQQMusicInfoFromJsonObject(
    mediaTitle: String,
    listObject: JsonObject,
    resultList: MutableList<MusicInfo>,
) {
    // 标题
    val title = listObject.get("title").asString
    // 如果搜索到的歌曲标题和媒体标题对应才添加
    if (jaroWinklerSimilarity.apply(mediaTitle, title) > 0.5) {
        // 专辑的JsonObject
        val albumObject = listObject.getAsJsonObject("album")
        // 专辑
        val album = albumObject.get("title").asString
        // 专辑ID
        val albumMid = albumObject.get("mid").asString
        // 音乐ID
        val musicMid = listObject.get("mid").asString
        // 歌手数组
        val singerArray = listObject.getAsJsonArray("singer")
        // 添加QQ音乐信息到结果列表
        resultList.add(
            MusicInfo(
                albumMid,
                musicMid,
                title,
                buildSignerString(singerArray),
                album
            )
        )
    }
}