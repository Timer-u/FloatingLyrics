package com.wzvideni.floatinglyrics.network

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.wzvideni.floatinglyrics.network.model.MusicInfo
import com.wzvideni.floatinglyrics.utils.StringUtility.Companion.jaroWinklerSimilarity
import com.wzvideni.floatinglyrics.utils.StringUtility.Companion.randomStringGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.Response
import java.math.BigInteger
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


// 网易云音乐搜索
suspend fun wyyMusicSearch(
    mediaTitle: String,
    mediaArtist: String,
    mediaAlbum: String,
    limit: Int,
): List<MusicInfo> = withContext(Dispatchers.IO) {
    // 网易云音乐搜索请求参数
    val wyyMusicData = """{
            "hlpretag":"<span class=\"s-fc7\">","hlposttag":"</span>",
            "s":"$mediaTitle - $mediaArtist - $mediaAlbum",
            "type":"1",
            "offset":"0",
            "total":"true",
            "limit":"$limit",
            "csrf_token":""
        }""".trimIndent()

    // 生成一个16位的随机字符串
    val randomString = randomStringGenerator.generate(16)
    // 第一次加密的params，可用于测试encryptRSA方法正确与否
    val encText = encryptAESCBC(wyyMusicData, "0CoJUm6Qyw8W8jud")
    // AES加密两次后获得params
    val params = encryptAESCBC(encText, randomString)

    // RSA加密后获得encSecKey
    val encSecKey = encryptRSA(randomString)

    // 构建网易云音乐搜索请求体
    val wyyMusicSearchRequestBody = FormBody.Builder()
        .add("params", params)
        .add("encSecKey", encSecKey)
        .build()

    // 构建网易云音乐搜索请求
    val wyyMusicSearchRequest = Request.Builder()
        .url("https://music.163.com/weapi/cloudsearch/pc")
        .header("Referer", OkHttpClient.WYY_REFERER)
        .header("User-Agent", OkHttpClient.USER_AGENT)
        .post(wyyMusicSearchRequestBody) // 将请求体用POST请求
        .build()

    // 执行请求并获取响应，使用use自动释放资源
    OkHttpClient.okHttpClient.newCall(wyyMusicSearchRequest).execute()
        .use { response: Response ->
            if (response.isSuccessful) {
                response.body?.string()?.let { jsonString: String ->
                    // 将JSON字符串转换为JsonObject
                    Gson().fromJson(jsonString, JsonObject::class.java)?.let { jsonObject ->
                        // 以下代码需对应请求出来的json数据来编写
                        // 获取songs数组
                        jsonObject.getAsJsonObject("result").getAsJsonArray("songs")
                            ?.let { singsArray ->
                                // 搜索结果List
                                val resultList: MutableList<MusicInfo> = mutableListOf()
                                // 遍历songs数组
                                for (list in singsArray) {
                                    // 将list对应的JSON字符串转换为JsonObject
                                    val listObject = list.asJsonObject
                                    // 标题
                                    val name = listObject.get("name").asString
                                    // 如果搜索到的歌曲标题和媒体标题不对应就不添加
                                    if (jaroWinklerSimilarity.apply(mediaTitle, name) > 0.5) {
                                        // 专辑的JsonObject
                                        val albumObject = listObject.getAsJsonObject("al")
                                        // 专辑
                                        val album = albumObject.get("name").asString
                                        // 专辑图片链接
                                        val albumPicUrl =
                                            albumObject.get("picUrl").asString.replace(
                                                "http",
                                                "https"
                                            )
                                        // 音乐ID
                                        val musicId = listObject.get("id").asString
                                        // 歌手数组
                                        val singerArray = listObject.getAsJsonArray("ar")

                                        resultList.add(
                                            MusicInfo(
                                                albumPicUrl,
                                                musicId,
                                                name,
                                                buildSignerString(singerArray),
                                                album
                                            )
                                        )
                                    }
                                }
                                return@withContext resultList.toList()
                            }
                    }
                }
            }
        }
    return@withContext emptyList()
}

// 网易云音乐搜索请求参数中的CBC模式的AES加密，接收一个待加密纯文本和密钥
private fun encryptAESCBC(plainText: String, key: String): String {
    // 将密钥字符串转换为字节数组
    val keyBytes = key.toByteArray()
    // 创建一个SecretKeySpec对象，指定使用AES算法
    val secretKey = SecretKeySpec(keyBytes, "AES")
    // 创建一个Cipher对象，指定使用AES/CBC/PKCS5Padding模式
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    //region 由于网易云音乐的初始化向量是固定的，不需要随机生成，所以以下代码注释掉
//        // 创建一个SecureRandom对象，用于生成随机的初始化向量
//        val secureRandom = SecureRandom()
//        // 创建一个长度为16的字节数组，用于存储初始化向量
//        val iv = ByteArray(16)
//        // 使用SecureRandom对象填充初始化向量
//        secureRandom.nextBytes(iv)
    //endregion

    // 创建一个长度为16的字节数组，用于存储初始化向量
    val iv = "0102030405060708".toByteArray()
    // 创建一个IvParameterSpec对象，指定使用初始化向量
    val ivParameterSpec = IvParameterSpec(iv)
    // 初始化Cipher对象，指定使用加密模式和密钥和初始化向量
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
    // 将明文字符串转换为字节数组
    val plainBytes = plainText.toByteArray()
    // 使用Cipher对象对明文字节数组进行加密，得到密文字节数组
    val cipherBytes = cipher.doFinal(plainBytes)
    return Base64.encodeToString(cipherBytes, Base64.DEFAULT)
}

// 网易云音乐搜索请求参数中的RSA加密
private fun encryptRSA(randomString: String): String {
    val e = "010001"
    val f =
        "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7"
    // 计算结果
    val result =
        BigInteger(randomString.reversed().toByteArray()).modPow(
            BigInteger(e, 16),
            BigInteger(f, 16)
        )
    // 返回十六进制字符串，长度为131，不足的默认补空格
    return result.toString(16).padStart(131, 'x')
}