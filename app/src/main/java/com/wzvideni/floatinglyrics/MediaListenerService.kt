package com.wzvideni.floatinglyrics

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.net.ConnectivityManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.service.notification.NotificationListenerService
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.wzvideni.floatinglyrics.network.buildLyricsList
import com.wzvideni.floatinglyrics.network.model.Lyric
import com.wzvideni.floatinglyrics.room.LyricsUpdateDao
import com.wzvideni.floatinglyrics.room.LyricsUpdateDatabase
import com.wzvideni.floatinglyrics.room.LyricsUpdateDatabase.Companion.getLyricsDatabase
import com.wzvideni.floatinglyrics.utils.StringUtility.Companion.jaccardSimilarity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Date
import kotlin.math.abs

class MediaListenerService : NotificationListenerService() {

    val playingStateViewModel by lazy { MainApplication.instance.playingStateViewModel }
    val sharedPreferencesViewModel by lazy { MainApplication.instance.sharedPreferencesViewModel }

    // 媒体会话管理器
    private lateinit var mediaSessionManager: MediaSessionManager

    // 所有正在进行的会话的控制器列表
    private lateinit var activeSessions: List<MediaController>

    // 媒体控制器
    private lateinit var mediaController: MediaController

    // 网络连接管理器
    private lateinit var connectivityManager: ConnectivityManager

    // 首次启动标志
    private var isFirstStart = true

    // 媒体基本信息
    private var title = ""
    private var artist = ""
    private var album = ""

    // 播放状态信息
    private var playingState = 3
    private var position = 0
    private var trackNumber = 0

    // 歌词和翻译
    private var lyrics = ""
    private var translation = ""

    // 歌词更新数据库
    private lateinit var lyricsUpdateDatabase: LyricsUpdateDatabase

    // 歌词更新表访问对象
    private lateinit var lyricsUpdateDao: LyricsUpdateDao

    // 当前的日期（毫秒）
    private val currentTime = Date().time

    // 媒体监听绑定器
    private val mediaListenerBinder = MediaListenerBinder()

    // 媒体监听服务绑定器
    inner class MediaListenerBinder : Binder() {
        // 获取媒体监听服务
        fun getMediaListenerService() = this@MediaListenerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return mediaListenerBinder
    }

    override fun onCreate() {
        super.onCreate()
        // 获取媒体会话管理服务
        mediaSessionManager =
            applicationContext.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager

        // 获取网络连接管理器实例
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onDestroy() {
        super.onDestroy()
        // 媒体控制器已初始化则注销回调
        if (::mediaController.isInitialized) {
            mediaController.unregisterCallback(mediaControllerCallback)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // 获取活跃的媒体会话列表
        activeSessions = mediaSessionManager.getActiveSessions(ComponentName(this, this.javaClass))
        // 判断是否存在媒体会话（媒体通知）
        if (activeSessions.isNotEmpty()) {
            // 设置媒体监听状态为真
            playingStateViewModel.setMediaListenerState(true)
            if (!::mediaController.isInitialized) {
                // 从第一个会话创建媒体控制器（经测试第一个媒体会话就是正在播放的媒体会话）
                mediaController = MediaController(this, activeSessions[0].sessionToken)
                // 注册回调函数
                mediaController.registerCallback(mediaControllerCallback)
                // 获取歌词更新数据库
                lyricsUpdateDatabase = getLyricsDatabase(this)
                // 获取歌词更新访问对象
                lyricsUpdateDao = lyricsUpdateDatabase.lyricsUpdateDao()
                // 循环获取当前正在播放的音乐信息
                cycleGetPlayingMusicInfo()
            } else if (!isFirstStart && !playingStateViewModel.isNotEmptyOfSearchKeyword()) {
                Toast.makeText(this, "设备配置变更！", Toast.LENGTH_SHORT).show()
                // 设备配置变更时需要重新获取媒体元数据
                isFirstStart = true
                // 循环获取当前正在播放的音乐信息
                cycleGetPlayingMusicInfo()
            }
        } else {
            Toast.makeText(this, "未发现媒体通知！", Toast.LENGTH_SHORT).show()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // 循环获取当前正在播放的音乐信息
    private fun cycleGetPlayingMusicInfo() {
        playingStateViewModel.viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                if (playingState == 3) {
                    getPlayingMusicInfo()
                }
                // 间隔指定毫秒毫秒获取当前正在播放的音乐信息
                delay(sharedPreferencesViewModel.findCurrentLyricsDelay.value)
            }
        }
    }

    // 媒体控制回调
    private val mediaControllerCallback = object : MediaController.Callback() {

        // 播放状态改变时的回调
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)
            // 状态不为空时才获取状态、更新状态
            if (state != null) {
                // 获取相关状态信息
                playingState = state.state
                position = state.position.toInt()
                // 更新状态信息到playingViewModel
                playingStateViewModel.setState(playingState)
                playingStateViewModel.setPosition(position)
            }
        }

        // 媒体元数据改变时的回调（音乐切换时的回调）
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            // 元数据不为空时才获取媒体元数据、更新playingViewModel的值
            if (metadata != null) {
                // 获取媒体基本信息，注：METADATA_KEY_DISPLAY_TITLE无法获取到媒体标题
                title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
                artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
                album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM)
                trackNumber = metadata.getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER).toInt()
                // 更新playingViewModel的值
                playingStateViewModel.setTitle(title)
                playingStateViewModel.setArtist(artist)
                playingStateViewModel.setAlbum(album)
                playingStateViewModel.setTrackNumber(trackNumber)
                // 清空遗留的歌词信息
                playingStateViewModel.setLyricsList(emptyList())
                playingStateViewModel.viewModelScope.launch(Dispatchers.IO) {
                    queryAndSaveData()
                }
            }
            super.onMetadataChanged(metadata)
        }
    }


    // 协程作用域的获取当前正在播放的音乐信息
    private suspend fun getPlayingMusicInfo() = withContext(Dispatchers.IO) {

        // 从媒体控制器获取当前播放状态
        val currentPlaybackState = mediaController.playbackState
        // 从媒体控制器获取当前媒体元数据
        val currentMetadata = mediaController.metadata

        // 获取此会话的当前播放状态，不为空才获取状态、更新状态
        currentPlaybackState?.let { playbackState: PlaybackState ->
            position = playbackState.position.toInt()
            playingStateViewModel.setPosition(position)
            playingState = playbackState.state
            if (playingState != playingStateViewModel.state.value) {
                playingStateViewModel.setState(playingState)
            }
        }

        // 获取当前媒体元数据，只需要第一次启动媒体监听和设备配置变更的时候执行一次
        if (isFirstStart && currentMetadata != null) {
            // 获取媒体基本信息，注：METADATA_KEY_DISPLAY_TITLE无法获取到媒体标题
            title = currentMetadata.getString(MediaMetadata.METADATA_KEY_TITLE)
            artist = currentMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
            album = currentMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM)
            trackNumber = currentMetadata.getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER).toInt()

            // 更新playingViewModel的值
            playingStateViewModel.setTitle(title)
            playingStateViewModel.setArtist(artist)
            playingStateViewModel.setAlbum(album)
            playingStateViewModel.setTrackNumber(trackNumber)
            // 查询并保存数据
            queryAndSaveData()
            // 更新一次性执行标志的值
            isFirstStart = false
        }
        // 获取当前的歌词
        updateCurrentLyrics()
    }

    // 获取当前位置和标题对应的歌词
    private suspend fun updateCurrentLyrics() = withContext(Dispatchers.IO) {
        // 路径不为空才继续执行
        // 使用async函数和await()方法，等待当前歌词查询结果
        val lyricsList = async { findCurrentLyrics() }.await()
        // 判断是否存在翻译，存在则读取前两个索引元素，否则只读取第一个索引元素
        if (lyricsList.size == 2) {
            // 仅在歌词和翻译改变时才更新playingViewModel的状态
            if (lyrics != lyricsList[0] && translation != lyricsList[1]) {
                lyrics = lyricsList[0]
                translation = lyricsList[1]
                playingStateViewModel.setLyric(lyrics)
                playingStateViewModel.setTranslation(translation)
            }
        } else if (lyricsList.size == 1) {
            if (lyrics != lyricsList[0]) {
                playingStateViewModel.setLyric(lyricsList[0])
                // 翻译不为空值时才更新playingViewModel的状态
                if (translation != "") {
                    playingStateViewModel.setTranslation("")
                }
            }
        } else if (lyricsList.isEmpty()) {
            // 如果playingViewModel的歌词和翻译有一个不为空字符串则为其赋值为空字符串
            if (playingStateViewModel.lyric.value != "" || playingStateViewModel.translation.value != "") {
                playingStateViewModel.setLyric("")
                playingStateViewModel.setTranslation("")
            }
        }
    }

    // 查询并保存数据
    private suspend fun queryAndSaveData() {
        // 查询音乐和歌词文件
        queryAudioAndLyricsFiles()
        // 纯音乐不读取本地歌词文件
        if (!title.contains("Instrumental", true) && !title.contains("纯音乐")) {
            // 判断网络是否连接并且自动搜索已打开
            if (connectivityManager.activeNetwork != null && sharedPreferencesViewModel.enableAutoSearch.value) {
                // 从数据库查询到的日期
                val queryDate = lyricsUpdateDao.queryUpdateDate(title, album)
                // 查找到的日期为0则数据库中不存在该歌曲信息，需要添加歌曲信息并重新搜索
                if (queryDate == 0L) {
                    if (searchAndSaveLyricToFile()) {
                        lyricsUpdateDao.insertUpdateDate(title, album, currentTime)
                    }
                } else if (abs(currentTime - queryDate) / (1000 * 60 * 60 * 24) > sharedPreferencesViewModel.intervalDays.value) {
                    // 否则判断天数差距是否大于设置的值，是则需要更新日期并重新搜索
                    if (searchAndSaveLyricToFile()) {
                        lyricsUpdateDao.updateUpdateDate(title, album, currentTime)
                    }
                } else {
                    readLyricsFile()
                }
            } else {
                readLyricsFile()
            }
        } else {
            playingStateViewModel.setQuery("纯音乐")
            playingStateViewModel.setLyricsPath("")
            // 纯音乐则清空原来的歌词信息
            playingStateViewModel.setLyricsList(emptyList())
        }
    }

    private var musicFile: File? = null
    private var lrcFile: File? = null

    // 查询音频和歌词文件
    private suspend fun queryAudioAndLyricsFiles() = withContext(Dispatchers.IO) {
        // 随机获取把传入的字符串按文件名不允许出现的字符分割后的字符串
        val titleSplit = splitStringByIllegalCharacters(title).random()
        val albumSplit = splitStringByIllegalCharacters(album).random()

        // 查询Uri：其中external（外部存储卷）包括内部存储和外部SD卡
        val queryUri = MediaStore.Files.getContentUri(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.VOLUME_EXTERNAL
            } else {
                "external"
            }
        )

        // 指定查询的列名：DATA列为绝对路径、MIME_TYPE列为文件类型
        val projection =
            arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MIME_TYPE)

        // 指定where的约束条件：根据标题查找出所有可能的结果
        val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE ?"

        // 为where中的占位符提供具体的值：经过按文件名不允许出现的字符分割后的标题
        val selectionArgs = arrayOf("%$titleSplit%")

        // 模糊、标准、精确匹配的正则表达式对象，使用Regex.escape()把标题的正则表达式特殊字符转义
        val vagueRegex = Regex(".+${Regex.escape(titleSplit)}.+")
        val standardRegex = Regex(".+${Regex.escape(albumSplit)}.+${Regex.escape(titleSplit)}.+")
        val exactRegex =
            Regex(".+${Regex.escape(albumSplit)}.+$trackNumber.+${Regex.escape(titleSplit)}.+")

        // 使用ContentResolver查询数据库
        contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            null,
        )?.use { cursor ->
            // 获取精确匹配是否匹配成功
            var queried = queryAudioAndLyricsPaths(cursor, exactRegex, "精确匹配")

            // 如果精确匹配没有查询到
            if (!queried) {
                // 获取标准匹配是否匹配成功
                queried = queryAudioAndLyricsPaths(cursor, standardRegex, "标准匹配")
                // 如果标准匹配没有查询到
                if (!queried) {
                    // 获取模糊匹配是否匹配成功
                    queried = queryAudioAndLyricsPaths(cursor, vagueRegex, "模糊匹配")
                    // 如果模糊匹配没有查询到
                    if (!queried) {
                        // 清空相关残留变量
                        lrcFile = null
                        musicFile = null
                        playingStateViewModel.setQuery("")
                        playingStateViewModel.setMusicPath("")
                        playingStateViewModel.setLyricsPath("")
                    }
                }
            }
        }
    }

    // 查询音频和歌词路径
    private suspend fun queryAudioAndLyricsPaths(
        cursor: Cursor,
        regex: Regex,
        matchType: String,
    ): Boolean = withContext(Dispatchers.IO) {
        // DATA、MIME_TYPE列索引
        var dataColumn: Int
        var mimeTypeColumn: Int
        // DATA、MIME_TYPE列索引对应的值
        var filePath: String
        var mimetype: String
        var isMusicMatched = false
        var isLyricsMatched = false
        // 将游标移动到第一行，游标为空则为false
        if (cursor.moveToFirst()) {
            do {
                // 获取DATA、MIME_TYPE列索引
                dataColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                mimeTypeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
                // 获取DATA、MIME_TYPE列索引对应的值
                filePath = cursor.getString(dataColumn)
                mimetype = cursor.getString(mimeTypeColumn)

                // 判断正则表达式是否匹配成功
                if (regex.find(filePath) != null) {
                    // flac、wav、mp3格式的音频文件的MIME_TYPE分别为：audio/flac、audio/x-wav、audio/mpeg
                    // 判断MIME_TYPE中包含audio，是则为音频文件
                    if (mimetype.contains("audio")) {
                        // 判断音频文件标题和媒体监听获取到的标题的Jaccard相似度是否大于0.5，是则表示匹配成功
                        if (jaccardSimilarity.apply(title, getAudioFileTitle(filePath)) > 0.5) {
                            musicFile = File(filePath)
                            playingStateViewModel.setMusicPath(filePath)
                            isMusicMatched = true
                        }
                    }
                    // lrc文件的MIME_TYPE为：application/lrc
                    // 否则MIME_TYPE中包含lrc则为歌词
                    else if (mimetype.contains("lrc")) {
                        File(filePath).let { file ->
                            // 判断歌词文件是否存在并且可读
                            if (file.exists() && file.canRead()) {
                                lrcFile = file
                                playingStateViewModel.setQuery(matchType)
                                playingStateViewModel.setLyricsPath(filePath)
                                isLyricsMatched = true
                            }
                        }
                    }
                }
            } while (cursor.moveToNext())
        }
        if (!isLyricsMatched) {
            // 清空相关残留变量
            lrcFile = null
            playingStateViewModel.setLyricsPath("")
        }
        return@withContext isMusicMatched
    }

    // 实现MediaMetadataRetriever的use拓展函数
    private inline fun <T> MediaMetadataRetriever.use(block: (MediaMetadataRetriever) -> T): T {
        try {
            return block(this)
        } finally {
            this.release()
        }
    }

    // 获取音频文件标题
    private fun getAudioFileTitle(filePath: String): String? {
        return MediaMetadataRetriever().use { mediaMetadataRetriever: MediaMetadataRetriever ->
            mediaMetadataRetriever.setDataSource(filePath)
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        }
    }

    // 读取歌词文件
    private suspend fun readLyricsFile() = withContext(Dispatchers.IO) {
        // 清空遗留的歌词信息
        playingStateViewModel.setLyricsList(emptyList())
        lrcFile?.let { lrcFile ->
            val linesList: List<String> = lrcFile.bufferedReader().readLines()
            playingStateViewModel.setLyricsList(buildLyricsList(linesList))
        }
    }

    // 查找当前位置的歌词（二分查找）
    private suspend fun findCurrentLyrics(): List<String> = withContext(Dispatchers.IO) {
        // 加上歌词延迟
        delay(sharedPreferencesViewModel.lyricsDelay.value)
        // 以下变量皆是需要短时间内经常改变的，定义为常量创建的开销可能过大
        // 最后索引
        val lastIndex = playingStateViewModel.lyricsList.value.lastIndex
        // 开始索引
        var beginIndex = 0
        // 结束索引
        var endIndex = lastIndex
        // 中间索引
        var midIndex: Int
        // 中间索引歌词
        var midLyric: Lyric
        // 下一个毫秒时间轴
        var nextMillisecond: Int
        // 从开始循环到结束
        while (beginIndex <= endIndex) {
            // 中间索引
            midIndex = (beginIndex + endIndex) / 2
            // 获取中间索引歌词
            midLyric = playingStateViewModel.lyricsList.value[midIndex]
            // 判断中间索引是否等于结束索引，是就表示找到了歌词
            if (midIndex == endIndex) {
                playingStateViewModel.setLyricsIndex(midIndex)
                return@withContext midLyric.lyricsList
            }
            // 获取下一个毫秒时间轴
            nextMillisecond = playingStateViewModel.lyricsList.value[midIndex + 1].millisecond
            // 判断播放位置是否在中间索引歌词的毫秒时间轴和下一个时间轴之间，是就表示找到歌词了
            if (position in midLyric.millisecond until nextMillisecond) {
                // 更新歌词索引
                playingStateViewModel.setLyricsIndex(midIndex)
                return@withContext midLyric.lyricsList
                // 否则判断播放位置是否小于中间索引歌词的毫秒时间轴，是就表示歌词位于前半部分
            } else if (position < midLyric.millisecond) {
                // 末尾索引移动到中间索引前一个
                endIndex = midIndex - 1
                // 否则判断播放位置是否大于中间索引歌词的毫秒时间轴，是就表示歌词位于后半部分
            } else if (position > midLyric.millisecond) {
                // 开始索引移动到中间索引后一个
                beginIndex = midIndex + 1
            }
        }
        // 没有找到就设置歌词索引为-1并返回单例空列表
        playingStateViewModel.setLyricsIndex(-1)
        return@withContext emptyList()
    }

    private suspend fun searchAndSaveLyricToFile(): Boolean {
        return playingStateViewModel.run {
            lyricSearch()
            saveLyricListToFile(lyricsList.value)
        }
    }

    // 搜索并保存歌词列表到文件
    suspend fun saveLyricListToFile(lyricList: List<Lyric>): Boolean =
        withContext(Dispatchers.IO) {
            musicFile?.let { musicFile ->
                try {
                    //待写入的内容
                    var stringBuffer = StringBuilder()

                    // 遍历歌词列表并写入歌词到文件
                    lyricList.forEach { lyrics: Lyric ->
                        lyrics.lyricsList.forEach { lyric: String ->
                            stringBuffer.append("${lyrics.timeline}$lyric\n")
                        }
                    }
                    val lyricFile = File(
                        musicFile.parentFile,
                        "${musicFile.nameWithoutExtension}.lrc"
                    )
                    FileOutputStream(lyricFile).use { outputStream: OutputStream ->
                        outputStream.write(
                            stringBuffer.toString().toByteArray()
                        )
                    }
                    return@withContext true
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return@withContext false
        }

    // 把字符串按文件名不允许出现的字符串分割
    private fun splitStringByIllegalCharacters(input: String): List<String> {
        val illegalCharactersRegex = Regex("[/\\\\:*?\"<>|]") // 包含文件名不允许的符号的正则表达式
        return input.split(illegalCharactersRegex).filter { it.isNotBlank() }
    }
}