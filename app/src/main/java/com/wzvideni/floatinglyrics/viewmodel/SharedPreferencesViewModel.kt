package com.wzvideni.floatinglyrics.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wzvideni.floatinglyrics.ui.moedl.HorizontalGravity
import com.wzvideni.floatinglyrics.ui.moedl.Located
import com.wzvideni.floatinglyrics.ui.moedl.Themes
import com.wzvideni.floatinglyrics.ui.moedl.VerticalGravity
import com.wzvideni.floatinglyrics.ui.moedl.setHorizontalGravity
import com.wzvideni.floatinglyrics.ui.moedl.setLocated
import com.wzvideni.floatinglyrics.ui.moedl.setVerticalGravity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SharedPreferencesViewModel(application: Application) : AndroidViewModel(application) {
    // SharedPreferences
    private var sharedPreferences =
        application.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    // 当前主题
    private val _currentTheme = MutableStateFlow<Themes>(Themes.System)
    val currentTheme: StateFlow<Themes> = _currentTheme

    private val _isEnableAutoSearch = MutableStateFlow(true)
    val isEnableAutoSearch: StateFlow<Boolean> = _isEnableAutoSearch
    fun setIsEnable(isEnable: Boolean) {
        _isEnableAutoSearch.value = isEnable
    }

    private val _intervalDays = MutableStateFlow(7)
    val intervalDays: StateFlow<Int> = _intervalDays
    fun intervalDaysPlusOne() {
        _intervalDays.value++
    }

    fun intervalDaysMinusOne() {
        _intervalDays.value--
    }

    fun intervalDaysReset() {
        _intervalDays.value = 7
    }

    private val _isQQMusicPriority = MutableStateFlow(true)
    val isQQMusicIsPriority: StateFlow<Boolean> = _isQQMusicPriority
    fun isQQMusicPriorityNot() {
        _isQQMusicPriority.value = !_isQQMusicPriority.value
    }

    private val _lyricsTextSize = MutableStateFlow(15.454546f)
    val lyricsTextSize: StateFlow<Float> = _lyricsTextSize

    fun setLyricsTextSize(lyricsTextSize: Float) {
        _lyricsTextSize.value = lyricsTextSize
    }

    private val _translationTextSize = MutableStateFlow(14.545454f)
    val translationTextSize: StateFlow<Float> = _translationTextSize
    fun setTranslationTextSize(translationTextSize: Float) {
        _translationTextSize.value = translationTextSize
    }

    private val _lyricsTextColor = MutableStateFlow(Color.Red.toArgb())
    val lyricsTextColor: StateFlow<Int> = _lyricsTextColor
    fun setLyricsTextColor(lyricsTextColor: Int) {
        _lyricsTextColor.value = lyricsTextColor
    }

    private val _translationTextColor = MutableStateFlow(Color.Cyan.toArgb())
    val translationTextColor: StateFlow<Int> = _translationTextColor
    fun setTranslationTextColor(translationTextColor: Int) {
        _translationTextColor.value = translationTextColor
    }

    private val _lyricsTypeface = MutableStateFlow(Typeface.NORMAL)
    val lyricsTypeface: StateFlow<Int> = _lyricsTypeface
    fun setLyricsTypeface(lyricsTypeface: Int) {
        _lyricsTypeface.value = lyricsTypeface
    }

    private val _translationTypeface = MutableStateFlow(Typeface.NORMAL)
    val translationTypeface: StateFlow<Int> = _translationTypeface
    fun setTranslationTypeface(translationTypeface: Int) {
        _translationTypeface.value = translationTypeface
    }

    private val _timelineDifference = MutableStateFlow(0)
    val timelineDifference: StateFlow<Int> = _timelineDifference
    fun timelineDifferencePlusOne() {
        _timelineDifference.value++
    }

    fun timelineDifferenceMinusOne() {
        _timelineDifference.value--
    }

    fun timelineDifferenceReset() {
        _timelineDifference.value = 0
    }


    private val _lyricsDelay = MutableStateFlow(0L)
    val lyricsDelay: StateFlow<Long> = _lyricsDelay
    fun lyricsDelayPlusOne() {
        _lyricsDelay.value++
    }

    fun lyricsDelayMinusOne() {
        _lyricsDelay.value--
    }

    fun lyricsDelayReset() {
        _lyricsDelay.value = 0
    }

    private val _findCurrentLyricsDelay = MutableStateFlow(200L)
    val findCurrentLyricsDelay: StateFlow<Long> = _findCurrentLyricsDelay
    fun findCurrentLyricsDelayPlusOne() {
        _findCurrentLyricsDelay.value++
    }

    fun findCurrentLyricsDelayMinusOne() {
        _findCurrentLyricsDelay.value--
    }

    fun findCurrentLyricsDelayReset() {
        _findCurrentLyricsDelay.value = 200
    }

    private val _located = MutableStateFlow(Located.Horizontal)
    val located: StateFlow<Located> = _located
    fun setLocated(located: Located) {
        _located.value = located
    }

    private val _verticalGravity = MutableStateFlow(VerticalGravity.Center)
    val verticalGravity: StateFlow<VerticalGravity> = _verticalGravity
    fun setVerticalGravity(verticalGravity: VerticalGravity) {
        _verticalGravity.value = verticalGravity
    }

    private val _horizontalGravity = MutableStateFlow(HorizontalGravity.Center)
    val horizontalGravity: StateFlow<HorizontalGravity> = _horizontalGravity
    fun setHorizontalGravity(horizontalGravity: HorizontalGravity) {
        _horizontalGravity.value = horizontalGravity
    }


    // 初始化加载SharedPreferences配置的设置
    init {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.apply {
                // 当前主题
                _currentTheme.value = stringToThemes(getString("CurrentTheme", Themes.System.theme))
                // 字体样式设置
                _lyricsTextSize.value = getFloat("LyricsTextSize", _lyricsTextSize.value)
                _translationTextSize.value =
                    getFloat("TranslationTextSize", _translationTextSize.value)
                _lyricsTextColor.value = getInt("LyricsTextColor", _lyricsTextColor.value)
                _translationTextColor.value =
                    getInt("TranslationTextColor", _translationTextColor.value)
                _lyricsTypeface.value = getInt("lyricsTypeface", _lyricsTypeface.value)
                _translationTypeface.value =
                    getInt("TranslationTypeface", _translationTypeface.value)
                // 自动搜索设置
                _isEnableAutoSearch.value =
                    getBoolean("IsEnableAutoSearch", _isEnableAutoSearch.value)
                _intervalDays.value =
                    getInt("IntervalDays", _intervalDays.value)
                _isQQMusicPriority.value =
                    getBoolean("IsQQMusicPriority", _isQQMusicPriority.value)
                // 歌词显示设置
                _findCurrentLyricsDelay.value =
                    getLong("FindCurrentLyricsDelay", _findCurrentLyricsDelay.value)
                _lyricsDelay.value = getLong("LyricsDelay", _lyricsDelay.value)
                _timelineDifference.value = getInt("TimelineDifference", _timelineDifference.value)

                // 歌词位置设置
                _located.value = Located.valueOf(getString("Located", _located.value.name)!!)
                _verticalGravity.value = VerticalGravity.valueOf(
                    getString(
                        "VerticalGravity",
                        _verticalGravity.value.name
                    )!!
                )
                _horizontalGravity.value = HorizontalGravity.valueOf(
                    getString(
                        "HorizontalGravity",
                        _horizontalGravity.value.name
                    )!!
                )
            }
        }
    }


    fun switchTheme() {
        when (_currentTheme.value) {
            Themes.System -> _currentTheme.value = Themes.Light
            Themes.Light -> _currentTheme.value = Themes.Dark
            Themes.Dark -> _currentTheme.value = Themes.System
        }
    }

    private fun stringToThemes(theme: String?): Themes {
        return when (theme) {
            Themes.Light.theme -> Themes.Light
            Themes.Dark.theme -> Themes.Dark
            else -> Themes.System
        }
    }

    // 保存SharedPreferences配置的设置
    fun saveSharedPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            // 要是需要在onDestroy()里面强制停止应用程序的话，需要把配置保存操作提前到onStop()方法，不然配置无法被保存
            sharedPreferences.edit {

                // 当前主题
                putString("CurrentTheme", _currentTheme.value.theme)

                // 字体样式设置
                putFloat("LyricsTextSize", _lyricsTextSize.value)
                putFloat("TranslationTextSize", _translationTextSize.value)
                putInt("LyricsTextColor", _lyricsTextColor.value)
                putInt("TranslationTextColor", _translationTextColor.value)
                putInt("lyricsTypeface", _lyricsTypeface.value)
                putInt("TranslationTypeface", _translationTypeface.value)
                // 自动搜索设置
                putBoolean("IsEnableAutoSearch", _isEnableAutoSearch.value)
                putInt("IntervalDays", _intervalDays.value)
                putBoolean("IsQQMusicPriority", _isQQMusicPriority.value)
                // 歌词显示设置
                putLong("FindCurrentLyricsDelay", _findCurrentLyricsDelay.value)
                putLong("LyricsDelay", _lyricsDelay.value)
                putInt("TimelineDifference", _timelineDifference.value)
                // 歌词位置设置
                putString("Located", _located.value.name)
                putString("VerticalGravity", _verticalGravity.value.name)
                putString("HorizontalGravity", _horizontalGravity.value.name)
            }
        }
    }

    // 设置悬浮歌词字体样式
    fun setFloatingLyricsTypeStyle(
        horizontalLyricsTextView: TextView,
        horizontalTranslationTextView: TextView,
        verticalLyricsTextView: TextView,
        verticalTranslationTextView: TextView,
    ) {
        // 歌词TextView字体大小
        horizontalLyricsTextView.textSize = _lyricsTextSize.value
        verticalLyricsTextView.textSize = _lyricsTextSize.value
        // 翻译TextView字体大小
        horizontalTranslationTextView.textSize = _translationTextSize.value
        verticalTranslationTextView.textSize = _translationTextSize.value
        // 歌词TextView字体颜色
        horizontalLyricsTextView.setTextColor(_lyricsTextColor.value)
        verticalLyricsTextView.setTextColor(_lyricsTextColor.value)
        // 翻译TextView字体颜色
        horizontalTranslationTextView.setTextColor(_translationTextColor.value)
        verticalTranslationTextView.setTextColor(_translationTextColor.value)
        // 歌词TextView字体粗细
        horizontalLyricsTextView.setTypeface(Typeface.SANS_SERIF, _lyricsTypeface.value)
        verticalLyricsTextView.setTypeface(Typeface.SANS_SERIF, _lyricsTypeface.value)
        // 翻译TextView字体粗细
        horizontalTranslationTextView.setTypeface(Typeface.SANS_SERIF, _translationTypeface.value)
        verticalTranslationTextView.setTypeface(Typeface.SANS_SERIF, _translationTypeface.value)

        // 水平和垂直歌词显示
        _located.value.setLocated(
            horizontalLyricsTextView = horizontalLyricsTextView,
            horizontalTranslationTextView = horizontalTranslationTextView,
            verticalLyricsTextView = verticalLyricsTextView,
            verticalTranslationTextView = verticalTranslationTextView
        )

        // 垂直歌词显示
        _verticalGravity.value.setVerticalGravity(
            verticalLyricsTextView = verticalLyricsTextView,
            verticalTranslationTextView = verticalTranslationTextView
        )

        // 水平歌词显示
        _horizontalGravity.value.setHorizontalGravity(
            horizontalLyricsTextView = horizontalLyricsTextView,
            horizontalTranslationTextView = horizontalTranslationTextView
        )
    }

    override fun onCleared() {
        super.onCleared()
        // 如果当前ViewModel中的任务仍处于活动状态，取消协程作用域中的任务
        if (viewModelScope.isActive) {
            viewModelScope.cancel()
        }
    }


}