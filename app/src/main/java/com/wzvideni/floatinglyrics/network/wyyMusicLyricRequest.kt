package com.wzvideni.floatinglyrics.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.wzvideni.floatinglyrics.network.model.Lyric
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.Response

// 根据网易云音乐ID请求歌词
suspend fun wyyMusicLyricRequest(songId: String): List<Lyric> =
    withContext(Dispatchers.IO) {
        // 根据音乐ID构建请求链接
        val url = "https://music.163.com/api/song/lyric?id=$songId&lv=-1&tv=-1"

        // 构建网易云音乐歌词请求
        val wyyMusicLyricsRequest = Request.Builder()
            .url(url)
            .header("Referer", OkHttpClient.WYY_REFERER)
            .header("User-Agent", OkHttpClient.USER_AGENT)
            .build()

        // 执行请求并获取响应，使用use自动释放资源
        OkHttpClient.okHttpClient.newCall(wyyMusicLyricsRequest).execute()
            .use { response: Response ->
                if (response.isSuccessful) {
                    response.body?.string()?.let {
                        // 用正则表达式提取json字符串
                        val jsonString = Regex("\\{.+\\}").find(it)?.value
                        // 使用jsonString构建jsonObject
                        val jsonObject = Gson().fromJson(jsonString, JsonObject::class.java)
                        // 从jsonObject获取歌词和翻译分别按换行符分割后的列表
                        val lyricList =
                            jsonObject.getAsJsonObject("lrc")
                                ?.get("lyric")?.asString?.split("\n")
                        val transList = jsonObject.getAsJsonObject("tlyric")
                            ?.get("lyric")?.asString?.split("\n")

                        // 把歌词列表和翻译列表相加组成行列表，如何其中有一个为空则不相加，返回不为空的那个列表
                        // 都为空就返回空列表
                        val linesList = if (lyricList != null && transList != null) {
                            lyricList + transList
                        } else lyricList ?: (transList ?: emptyList())
                        // 行列表不为空才构建歌词列表
                        if (linesList.isNotEmpty()) {
                            // 音乐歌词列表，由于LazyColumn中的itemsIndexed不支持直接处理Map，只好将其转换为List
                            return@withContext buildLyricsList(linesList)
                        }
                    }
                }
            }
        return@withContext emptyList()
    }