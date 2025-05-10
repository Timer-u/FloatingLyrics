package com.wzvideni.floatinglyrics.ui.page

import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wzvideni.floatinglyrics.ui.basic.PrimaryIcon
import com.wzvideni.floatinglyrics.ui.basic.PrimaryText
import com.wzvideni.floatinglyrics.ui.basic.Separate
import com.wzvideni.floatinglyrics.ui.basic.TextSizeSlider
import com.wzvideni.floatinglyrics.ui.basic.TitleText
import com.wzvideni.floatinglyrics.ui.basic.setting.AutoSearchPriority
import com.wzvideni.floatinglyrics.ui.basic.setting.ColorPicker
import com.wzvideni.floatinglyrics.ui.basic.setting.HorizontalLyricsGravitySettingButton
import com.wzvideni.floatinglyrics.ui.basic.setting.LyricsLocatedSettingButton
import com.wzvideni.floatinglyrics.ui.basic.setting.RadioSwitch
import com.wzvideni.floatinglyrics.ui.basic.setting.SettingWithDoubleArrow
import com.wzvideni.floatinglyrics.ui.basic.setting.TypefaceSetting
import com.wzvideni.floatinglyrics.ui.basic.setting.VerticalLyricsGravitySettingButton
import com.wzvideni.floatinglyrics.utils.view.OnTouchView
import com.wzvideni.floatinglyrics.viewmodel.SharedPreferencesViewModel

// 设置页面
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SettingPage(
    sharedPreferencesViewModel: SharedPreferencesViewModel,
    horizontalLyricsTextView: TextView,
    horizontalTranslationTextView: TextView,
    verticalLyricsTextView: TextView,
    verticalTranslationTextView: TextView,
    onClickToUnlockFloatingView: () -> Unit,
) {
    val context = LocalContext.current
    val locatedState by sharedPreferencesViewModel.located.collectAsState()
    val verticalGravityState by sharedPreferencesViewModel.verticalGravity.collectAsState()
    val horizontalGravityState by sharedPreferencesViewModel.horizontalGravity.collectAsState()

    // 自动搜索状态
    val enableAutoSearchState by sharedPreferencesViewModel.enableAutoSearch.collectAsState()
    val intervalDaysState by sharedPreferencesViewModel.intervalDays.collectAsState()
    val isQQMusicPriorityState by sharedPreferencesViewModel.isQQMusicIsPriority.collectAsState()

    // 歌词显示设置
    val findCurrentLyricsDelayState by sharedPreferencesViewModel.findCurrentLyricsDelay.collectAsState()
    val lyricsDelayState by sharedPreferencesViewModel.lyricsDelay.collectAsState()
    val timelineDifferenceState by sharedPreferencesViewModel.timelineDifference.collectAsState()

    // 字体样式状态
    val lyricsTextSizeState by sharedPreferencesViewModel.lyricsTextSize.collectAsState()
    val translationTextSizeState by sharedPreferencesViewModel.translationTextSize.collectAsState()
    val lyricsTextColorState by sharedPreferencesViewModel.lyricsTextColor.collectAsState()
    val translationTextColorState by sharedPreferencesViewModel.translationTextColor.collectAsState()
    val lyricsTypefaceState by sharedPreferencesViewModel.lyricsTypeface.collectAsState()
    val translationTypefaceState by sharedPreferencesViewModel.translationTypeface.collectAsState()
    // 垂直滚动状态
    val verticalScrollState = rememberScrollState()
    // 持久化Uri权限列表
    // 主题状态
    val themeState by sharedPreferencesViewModel.currentTheme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { PrimaryText("歌词设置") },
                actions = {
                    IconButton(onClick = {
                        sharedPreferencesViewModel.switchTheme()
                    }) {
                        PrimaryIcon(themeState.icon)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(verticalScrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            TitleText("自动搜索设置：")
            // 自动搜索歌词开关
            RadioSwitch(title = "自动搜索歌词", enableState = enableAutoSearchState) {
                sharedPreferencesViewModel.setEnableAutoSearch(it)
            }
            // 自动搜索间隔天数
            SettingWithDoubleArrow(
                startText = "自动搜索间隔天数",
                settingValue = intervalDaysState,
                leftArrowOnClick = {
                    sharedPreferencesViewModel.intervalDaysMinusOne()
                },
                rightArrowOnClick = {
                    sharedPreferencesViewModel.intervalDaysPlusOne()
                },
                settingValueOnLongClick = {
                    sharedPreferencesViewModel.intervalDaysReset()
                }
            )
            // 自动搜索优先级
            AutoSearchPriority(isQQMusicPriorityState) {
                sharedPreferencesViewModel.isQQMusicPriorityNot()
            }
            Separate()

            TitleText("延迟设置：")
            // 查找当前歌词延迟（毫秒）
            SettingWithDoubleArrow(
                startText = "查找当前歌词延迟（毫秒）",
                settingValue = findCurrentLyricsDelayState,
                leftArrowOnClick = {
                    if (findCurrentLyricsDelayState > 0) {
                        sharedPreferencesViewModel.findCurrentLyricsDelayMinusOne()
                    }
                },
                rightArrowOnClick = {
                    sharedPreferencesViewModel.findCurrentLyricsDelayPlusOne()
                },
                settingValueOnLongClick = {
                    sharedPreferencesViewModel.findCurrentLyricsDelayReset()
                }
            )
            // 歌词延迟（毫秒）
            SettingWithDoubleArrow(
                startText = "歌词延迟（毫秒）",
                settingValue = lyricsDelayState,
                leftArrowOnClick = {
                    sharedPreferencesViewModel.lyricsDelayMinusOne()
                },
                rightArrowOnClick = {
                    sharedPreferencesViewModel.lyricsDelayPlusOne()
                },
                settingValueOnLongClick = {
                    sharedPreferencesViewModel.lyricsDelayReset()
                }
            )
            // 时间轴差（毫秒）
            SettingWithDoubleArrow(
                startText = "时间轴差（毫秒）",
                settingValue = timelineDifferenceState,
                leftArrowOnClick = {
                    if (timelineDifferenceState > 0) {
                        sharedPreferencesViewModel.timelineDifferenceMinusOne()
                    }
                },
                rightArrowOnClick = {
                    sharedPreferencesViewModel.timelineDifferencePlusOne()
                },
                settingValueOnLongClick = {
                    sharedPreferencesViewModel.timelineDifferenceReset()
                }
            )
            Separate()

            TitleText("歌词和翻译字体大小设置：")
            // 浮动歌词字体和颜色设置
            TextSizeSlider(lyricsTextSizeState) { textSize: Float ->
                sharedPreferencesViewModel.setLyricsTextSize(textSize)
                horizontalLyricsTextView.textSize = textSize
                verticalLyricsTextView.textSize = textSize
            }
            TextSizeSlider(translationTextSizeState) { textSize: Float ->
                sharedPreferencesViewModel.setTranslationTextSize(textSize)
                horizontalTranslationTextView.textSize = textSize
                verticalTranslationTextView.textSize = textSize
            }
            Separate()

            TitleText("歌词和翻译字体颜色设置：")
            ColorPicker(lyricsTextColorState, themeState) { textColor: Int ->
                sharedPreferencesViewModel.setLyricsTextColor(textColor)
                horizontalLyricsTextView.setTextColor(textColor)
                verticalLyricsTextView.setTextColor(textColor)
            }
            ColorPicker(translationTextColorState, themeState) { textColor: Int ->
                sharedPreferencesViewModel.setTranslationTextColor(textColor)
                horizontalTranslationTextView.setTextColor(textColor)
                verticalTranslationTextView.setTextColor(textColor)
            }
            Separate()

            TitleText("歌词和翻译字体样式设置：")
            TypefaceSetting(
                lyricsTypefaceState,
                translationTypefaceState,
                horizontalLyricsTextView,
                horizontalTranslationTextView,
                verticalLyricsTextView,
                verticalTranslationTextView,
                lyricsTypefaceStateChanged = { typeface: Int ->
                    sharedPreferencesViewModel.setLyricsTypeface(typeface)
                },
                translationTypefaceStateChanged = { typeface: Int ->
                    sharedPreferencesViewModel.setTranslationTypeface(typeface)
                }
            )
            Separate()



            TitleText("悬浮歌词显示设置：")
            Row {
                // 悬浮歌词位置设置按钮
                LyricsLocatedSettingButton(
                    locatedState,
                    horizontalLyricsTextView,
                    horizontalTranslationTextView,
                    verticalLyricsTextView,
                    verticalTranslationTextView
                ) {
                    sharedPreferencesViewModel.setLocated(it)
                }
                Spacer(modifier = Modifier.width(5.dp))
                // 悬浮歌词解锁设置
                Button(onClick = { onClickToUnlockFloatingView() }) {
                    Text(text = "解锁悬浮歌词")
                }
            }
            Separate()

            TitleText(
                text = "歌词和翻译对齐设置：",
                modifier = Modifier.combinedClickable(
                    onClick = {
                        Toast.makeText(
                            context,
                            "长按可以重置歌词和翻译位置",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onLongClick = {
                        Toast.makeText(context, "已重置歌词和翻译位置", Toast.LENGTH_SHORT)
                            .show()
                        OnTouchView.restoreInitialPosition(context, horizontalLyricsTextView)
                        OnTouchView.restoreInitialPosition(
                            context,
                            horizontalTranslationTextView
                        )
                        OnTouchView.restoreInitialPosition(context, verticalLyricsTextView)
                        OnTouchView.restoreInitialPosition(context, verticalTranslationTextView)
                    }
                )
            )
            Row {
                // 水平歌词布局对齐设置按钮
                HorizontalLyricsGravitySettingButton(
                    horizontalGravity = horizontalGravityState,
                    horizontalLyricsTextView = horizontalLyricsTextView,
                    horizontalTranslationTextView = horizontalTranslationTextView,
                    onHorizontalGravityChanged = {
                        sharedPreferencesViewModel.setHorizontalGravity(it)
                    }
                )
                Spacer(modifier = Modifier.width(5.dp))
                // 垂直歌词布局对齐设置按钮
                VerticalLyricsGravitySettingButton(
                    verticalGravity = verticalGravityState,
                    verticalLyricsTextView = verticalLyricsTextView,
                    verticalTranslationTextView = verticalTranslationTextView,
                    onVerticalGravityChanged = {
                        sharedPreferencesViewModel.setVerticalGravity(it)
                    }
                )
            }
        }
    }
}