package com.wzvideni.floatinglyrics.utils.view

import android.graphics.PixelFormat
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Stable

// 添加视图为锁定悬浮窗
fun WindowManager.addLockedFloatingView(view: View) {
    addView(view, lockedWindowParams)
}

// 更新视图为锁定悬浮窗
fun WindowManager.updateLockedFloatingViewLayout(view: View) {
    updateViewLayout(view, lockedWindowParams)
}

// 添加视图为未锁定悬浮窗
fun WindowManager.addUnLockedFloatingView(view: View) {
    addView(view, unlockedWindowParams)
}

// 更新视图为未锁定悬浮窗
fun WindowManager.updateUnLockedFloatingViewLayout(view: View) {
    updateViewLayout(view, unlockedWindowParams)
}

// 锁定的悬浮歌词窗口参数
@Stable
private val lockedWindowParams: WindowManager.LayoutParams =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // 安卓12不受信任的触摸事件会被屏蔽，目前采用设置透明度的方法
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.S) {
            WindowManager.LayoutParams(
                // 应用程序覆盖窗口显示在所有活动窗口上方
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                // 此窗口永远不会获得按键输入焦点，因此用户无法向其发送点击或其他按钮事件
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        // 该窗口永远不能接收触摸事件
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                // 悬浮窗口背景
                PixelFormat.TRANSPARENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSPARENT
            ).apply {
                alpha = 0.8f
            }
        }
    } else {
        @Suppress("DEPRECATION")
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSPARENT
        )
    }

// 未锁定的悬浮歌词窗口参数
@Stable
private val unlockedWindowParams: WindowManager.LayoutParams =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // 安卓12不受信任的触摸事件会被屏蔽，目前采用设置透明度的方法
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.S) {
            WindowManager.LayoutParams(
                // 应用程序覆盖窗口显示在所有活动窗口上方
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                // 此窗口永远不会获得按键输入焦点
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                // 悬浮窗口背景
                PixelFormat.TRANSPARENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT
            ).apply {
                alpha = 0.8f
            }
        }
    } else {
        @Suppress("DEPRECATION")
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        )
    }