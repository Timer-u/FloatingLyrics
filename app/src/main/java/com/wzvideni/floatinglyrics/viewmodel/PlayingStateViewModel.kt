package com.wzvideni.floatinglyrics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wzvideni.floatinglyrics.MainApplication
import com.wzvideni.floatinglyrics.network.model.Lyric
import com.wzvideni.floatinglyrics.network.model.MusicInfo
import com.wzvideni.floatinglyrics.network.qqMusicLyricRequest
import com.wzvideni.floatinglyrics.network.qqMusicSearch
import com.wzvideni.floatinglyrics.network.wyyMusicLyricRequest
import com.wzvideni.floatinglyrics.network.wyyMusicSearch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive

class PlayingStateViewModel : ViewModel() {

    val sharedPreferencesViewModel by lazy { MainApplication.instance.sharedPreferencesViewModel }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query
    fun setQuery(query: String) {
        _query.value = query
    }

    private val _musicPath = MutableStateFlow("")
    val musicPath: StateFlow<String> = _musicPath
    fun setMusicPath(musicPath: String) {
        _musicPath.value = musicPath
    }

    private val _lyricsPath = MutableStateFlow("")
    val lyricsPath: StateFlow<String> = _lyricsPath
    fun setLyricsPath(lrcPath: String) {
        _lyricsPath.value = lrcPath
    }

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title
    fun setTitle(title: String) {
        _title.value = title
    }

    private val _artist = MutableStateFlow("")
    val artist: StateFlow<String> = _artist
    fun setArtist(artist: String) {
        _artist.value = artist
    }

    private val _album = MutableStateFlow("")
    val album: StateFlow<String> = _album
    fun setAlbum(album: String) {
        _album.value = album
    }

    private val _trackNumber = MutableStateFlow(0)
    val trackNumber: StateFlow<Int> = _trackNumber
    fun setTrackNumber(trackNumber: Int) {
        _trackNumber.value = trackNumber
    }

    private val _state = MutableStateFlow(0)
    val state: StateFlow<Int> = _state
    fun setState(state: Int) {
        _state.value = state
    }

    private val _position = MutableStateFlow(0)
    val position: StateFlow<Int> = _position
    fun setPosition(position: Int) {
        _position.value = position
    }

    private val _lyric = MutableStateFlow("")
    val lyric: StateFlow<String> = _lyric
    fun setLyric(lyric: String) {
        _lyric.value = lyric
    }

    private val _translation = MutableStateFlow("")
    val translation: StateFlow<String> = _translation
    fun setTranslation(translation: String) {
        _translation.value = translation
    }

    private val _qqMusicSearchResultList = MutableStateFlow(emptyList<MusicInfo>())
    val qqMusicSearchResultList: StateFlow<List<MusicInfo>> = _qqMusicSearchResultList

    private val _wyyMusicSearchResultList = MutableStateFlow(emptyList<MusicInfo>())
    val wyyMusicSearchResultList: StateFlow<List<MusicInfo>> = _wyyMusicSearchResultList

    // 使用Map来构建歌词理论上更快，但是滚动歌词界面的LazyColumn组件只支持List，只好改用List
    private val _lyricsList = MutableStateFlow(emptyList<Lyric>())
    val lyricsList: StateFlow<List<Lyric>> = _lyricsList
    fun setLyricsList(lyricsList: List<Lyric>) {
        _lyricsList.value = lyricsList
    }

    private val _lyricIndex = MutableStateFlow(-1)
    val lyricIndex: StateFlow<Int> = _lyricIndex
    fun setLyricsIndex(lyricIndex: Int) {
        _lyricIndex.value = lyricIndex
    }


    // 媒体监听状态，用于启动和停止媒体监听
    private val _mediaListenerState = MutableStateFlow(false)
    val mediaListenerState: StateFlow<Boolean> = _mediaListenerState
    fun setMediaListenerState(mediaListenerState: Boolean) {
        _mediaListenerState.value = mediaListenerState
    }


    override fun onCleared() {
        super.onCleared()
        // 如果当前ViewModel中的任务仍处于活动状态，取消协程作用域中的任务
        if (viewModelScope.isActive) {
            viewModelScope.cancel()
        }
    }

    // 搜索关键字是否为空字符串
    fun isNotEmptyOfSearchKeyword(): Boolean {
        return _title.value != "" && _artist.value != "" && _album.value != ""
    }

    // 音乐搜索
    suspend fun musicSearch() {
        _qqMusicSearchResultList.value =
            qqMusicSearch(_title.value, _artist.value, _album.value, 10)
        _wyyMusicSearchResultList.value =
            wyyMusicSearch(_title.value, _artist.value, _album.value, 10)
    }

    suspend fun lyricSearch() {
        // 搜索关键字不为空字符串才搜索
        if (isNotEmptyOfSearchKeyword()) {
            // 执行音乐搜索
            musicSearch()

            // QQ音乐歌词列表
            val qqMusicLyricList: List<Lyric> =
                qqMusicSearchResultList.takeIf { it.value.isNotEmpty() }?.let {
                    qqMusicLyricRequest(it.value[0].musicId)
                } ?: emptyList()


            // 网易云音乐歌词列表
            val wyyMusicLyricList: List<Lyric> =
                wyyMusicSearchResultList.takeIf { it.value.isNotEmpty() }?.let {
                    wyyMusicLyricRequest(it.value[0].musicId)
                } ?: emptyList()

            // QQ音乐和网易云音乐歌词列表都不为空时才比较其完整性
            if (qqMusicLyricList.isNotEmpty() && wyyMusicLyricList.isNotEmpty()) {
                // QQ音乐歌词完整性
                val qqMusicLyricIntegrity: Float = calculateLyricIntegrity(qqMusicLyricList)

                // 网易云音乐歌词完整性
                val wyyMusicLyricIntegrity: Float = calculateLyricIntegrity(wyyMusicLyricList)

                // 如果QQ音乐和网易云音乐的歌词完整度都差不多就按优先级来确定歌词，否则就按完整度来确定歌词
                if ((qqMusicLyricIntegrity > 0.5 && wyyMusicLyricIntegrity > 0.5) ||
                    qqMusicLyricIntegrity < 0.5 && wyyMusicLyricIntegrity < 0.5
                ) {
                    if (sharedPreferencesViewModel.isQQMusicIsPriority.value) {
                        _query.value = "QQ音乐"
                        _lyricsPath.value = ""
                        _lyricsList.value = qqMusicLyricList
                    } else {
                        _query.value = "网易云音乐"
                        _lyricsPath.value = ""
                        _lyricsList.value = wyyMusicLyricList
                    }
                } else if (qqMusicLyricIntegrity < 0.5) {
                    _query.value = "网易云音乐"
                    _lyricsPath.value = ""
                    _lyricsList.value = wyyMusicLyricList
                } else if (wyyMusicLyricIntegrity < 0.5) {
                    _query.value = "QQ音乐"
                    _lyricsPath.value = ""
                    _lyricsList.value = qqMusicLyricList
                }
            } else if (qqMusicLyricList.isNotEmpty() && sharedPreferencesViewModel.isQQMusicIsPriority.value) {
                // QQ音乐歌词列表不为空并且QQ音乐优先
                _query.value = "QQ音乐"
                _lyricsPath.value = ""
                _lyricsList.value = qqMusicLyricList
            } else if (wyyMusicLyricList.isNotEmpty()) {
                _query.value = "网易云音乐"
                _lyricsPath.value = ""
                _lyricsList.value = wyyMusicLyricList
            }
        }
    }

    // 计算歌词完整性
    private fun calculateLyricIntegrity(lyricList: List<Lyric>): Float =
        lyricList.takeIf { lyricList.isNotEmpty() }?.let {
            lyricList.count { it.lyricsList.size == 2 }.toFloat()
                .div(lyricList.size)
        } ?: 0f

}
