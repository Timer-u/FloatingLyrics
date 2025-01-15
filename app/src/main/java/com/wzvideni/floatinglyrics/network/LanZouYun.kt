package com.wzvideni.floatinglyrics.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.wzvideni.floatinglyrics.network.OkHttpClient.Companion.okHttpClient
import com.wzvideni.floatinglyrics.network.model.LanZouFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.Response

// 过程：获取输入密码时的页面参数进而进行输入密码请求
suspend fun checkUpdate(): LanZouFile? = withContext(Dispatchers.IO) {

    val lanZouYunRequest = Request.Builder()
        .url(OkHttpClient.LAN_ZOU_URL)
        .header("User-Agent", OkHttpClient.LAN_ZOU_USER_AGENT)
        .build()

    // 执行请求并获取响应，使用use自动释放资源
    okHttpClient.newCall(lanZouYunRequest).execute()
        .use { response: Response ->
            if (response.isSuccessful) {
                response.body?.string()?.let { responseString: String ->

                    // url参数
                    val url = Regex("url\\s*:\\s*'(/filemoreajax\\.php\\?file=\\d+)'")
                        .find(responseString)
                        ?.groupValues
                        ?.get(1)

                    // file参数
                    val file = url?.let { Regex("\\d+").find(it)?.value }

                    //  t参数
                    val t = Regex("var\\s+\\w+\\s*=\\s*'([^']\\d+)';")
                        .find(responseString)
                        ?.groupValues
                        ?.get(1)

                    // k参数
                    val k = Regex("var\\s+_\\w+\\s*=\\s*'([^']*)';")
                        .find(responseString)
                        ?.groupValues
                        ?.get(1)

                    // 如果各个参数都不为空执行输入密码请求
                    if (url != null && file != null && t != null && k != null) {
                        return@withContext getAfterInputPassWord(url, file, t, k)
                    }
                }
            }
        }
    return@withContext null
}

// 过程：获取输入密码后的页面返回的Json数据中的数组中的第一个元素（最新文件）后，进而请求该文件
suspend fun getAfterInputPassWord(
    url: String,
    file: String,
    t: String,
    k: String,
): LanZouFile? = withContext(Dispatchers.IO) {
    // 构建蓝奏云输入密码请求体
    val inputPassWordRequestBody = FormBody.Builder()
        .add("lx", "2")
        .add("fid", file)
        .add("uid", "1674564")
        .add("pg", "1")
        .add("rep", "0")
        .add("t", t)
        .add("k", k)
        .add("up", "1")
        .add("ls", "1")
        .add("pwd", OkHttpClient.LAN_ZOU_PWD)
        .build()

    // url形式：/filemoreajax.php?file=9323971
    // 构建蓝奏云输入密码请求
    val inputPassWordRequest = Request.Builder()
        .url(OkHttpClient.lanZouDomain + url)
        .header("User-Agent", OkHttpClient.LAN_ZOU_USER_AGENT)
        .post(inputPassWordRequestBody) // 将请求体用POST请求
        .build()

    // 执行请求并获取响应，使用use自动释放资源
    okHttpClient.newCall(inputPassWordRequest).execute()
        .use { response: Response ->
            if (response.isSuccessful) {
                response.body?.string()?.let {
                    // 构建jsonObject
                    val jsonObject = Gson().fromJson(it, JsonObject::class.java)
                    // 获取text数组
                    val textList = jsonObject.getAsJsonArray("text")
                    // 遍历text数组
                    for (text in textList) {
                        // 将list对应的JSON字符串转换为JsonObject
                        val textObject = text.asJsonObject
                        val id = textObject.get("id").asString
                        val nameAll = textObject.get("name_all").asString
                        val time = textObject.get("time").asString
                        return@withContext LanZouFile(id, nameAll, time)
                    }
                }
            }
        }
    return@withContext null
}

// 获取最新文件页面
suspend fun getLatestFile(id: String): String? = withContext(Dispatchers.IO) {
    // 构建最新文件请求
    val latestFileRequest = Request.Builder()
        .url("${OkHttpClient.lanZouDomain}/${id}")
        .header("User-Agent", OkHttpClient.LAN_ZOU_USER_AGENT)
        .build()

    // 执行请求并获取响应，使用use自动释放资源
    okHttpClient.newCall(latestFileRequest).execute()
        .use { response: Response ->
            if (response.isSuccessful) {
                response.body?.string()?.let {
                    // 获取文件src参数
                    val src =
                        Regex("(?<!<!--)<iframe\\s+class=\"ifr2\"\\s+name=\"\\d+\"\\s+src=\"([^\"]+)\"\\s+frameborder=\"0\"\\s+scrolling=\"no\"></iframe>(?!-->)")
                            .find(it)
                            ?.groupValues
                            ?.get(1)
                    if (src != null) {
                        return@withContext getFileRedirect(OkHttpClient.lanZouDomain + src)
                    }
                }
            }
        }
    return@withContext null
}

// 获取文件重定向页面
suspend fun getFileRedirect(url: String): String? = withContext(Dispatchers.IO) {
    // 构建文件重定向请求
    val fileRedirectRequest = Request.Builder()
        .url(url)
        .header("User-Agent", OkHttpClient.LAN_ZOU_USER_AGENT)
        .build()

    // 执行请求并获取响应，使用use自动释放资源
    okHttpClient.newCall(fileRedirectRequest).execute()
        .use { response: Response ->
            if (response.isSuccessful) {
                response.body?.string()?.let { responseString: String ->

                    // 获取url参数
                    val nextUrl = Regex("url\\s*:\\s*'([^']+)'")
                        .find(responseString)
                        ?.groupValues
                        ?.get(1)

                    // 获取sign参数
                    val sign = Regex("'sign'\\s*:\\s*'([^']+)'")
                        .find(responseString)
                        ?.groupValues
                        ?.get(1)

                    // 获取signs参数
                    val signs = Regex("var\\s+ajaxdata\\s*=\\s*'([^']*)';")
                        .find(responseString)
                        ?.groupValues
                        ?.get(1)

                    // 获取websign参数
                    val websign = Regex("var\\s+ciucjdsdc\\s*=\\s*'([^']*)';")
                        .find(responseString)
                        ?.groupValues
                        ?.get(1)

                    // 获取websignkey参数
                    val websignkey = Regex("var\\s+aihidcms\\s*=\\s*'([^']*)';")
                        .find(responseString)
                        ?.groupValues
                        ?.get(1)

                    if (nextUrl != null && sign != null && signs != null && websign != null && websignkey != null) {
                        return@withContext getAfterFileRedirect(
                            url,
                            OkHttpClient.lanZouDomain + nextUrl,
                            sign,
                            signs,
                            websign,
                            websignkey
                        )
                    }
                }
            }
        }
    return@withContext null
}

// 获取文件重定向之后页面
suspend fun getAfterFileRedirect(
    lastUrl: String,
    url: String,
    sign: String,
    signs: String,
    websign: String,
    websignkey: String,
): String? = withContext(Dispatchers.IO) {
    // 构建文件重定向之后请求体
    val afterFileRedirectRequestBody = FormBody.Builder()
        .add("action", "downprocess")
        .add("signs", signs)
        .add("sign", sign)
        .add("websign", websign)
        .add("websignkey", websignkey)
        .add("ves", "1")
        .build()

    // 构建文件重定向之后请求
    val afterFileRedirectRequest = Request.Builder()
        .url(url)
        .header("Referer", lastUrl)
        .header("User-Agent", OkHttpClient.LAN_ZOU_USER_AGENT)
        .post(afterFileRedirectRequestBody) // 将请求体用POST请求
        .build()

    // 执行请求并获取响应，使用use自动释放资源
    okHttpClient.newCall(afterFileRedirectRequest).execute()
        .use { response: Response ->
            if (response.isSuccessful) {
                response.body?.string()?.let {
                    // 构建jsonObject
                    val jsonObject = Gson().fromJson(it, JsonObject::class.java)
                    val dom = jsonObject.get("dom").asString
                    val nextUrl = jsonObject.get("url").asString

                    return@withContext getFileDirectLink("$dom/file/$nextUrl")
                }
            }
        }
    return@withContext null
}

// 获取文件直链
suspend fun getFileDirectLink(url: String): String? = withContext(Dispatchers.IO) {

    // 构建文件直链请求
    val fileDirectLinkRequest = Request.Builder()
        .url(url)
        .header(
            "accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
        )
        .header("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
        .header(
            "sec-ch-ua",
            "\"Chromium\";v=\"131\", \"Not(A:Brand\";v=\"24\", \"Microsoft Edge\";v=\"131\""
        )
        .header("sec-ch-ua-mobile", "?0")
        .header("sec-ch-ua-platform", "Windows")
        .header("sec-fetch-dest", "document")
        .header("sec-fetch-mode", "navigate")
        .header("sec-fetch-site", "none")
        .header("sec-fetch-user", "?1")
        .header("upgrade-insecure-requests", "1")
        .header("cookie", "down_ip=1")
        .build()

    // 执行请求并获取响应，使用use自动释放资源
    okHttpClient.newCall(fileDirectLinkRequest).execute()
        .use { response: Response ->
            if (response.isSuccessful) {
                return@withContext Regex("https://\\S+\\w").find(response.toString())?.value
            }
        }
    return@withContext null
}