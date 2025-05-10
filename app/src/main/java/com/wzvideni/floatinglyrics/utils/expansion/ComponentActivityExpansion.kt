package com.wzvideni.floatinglyrics.utils.expansion

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import com.wzvideni.floatinglyrics.MediaListenerService


// 请求自定义权限
fun ComponentActivity.requestCustomPermissions(onSuccess: () -> Unit) {
    // 获取系统设置中已启用的通知监听服务列表
    val enabledListenerPackages =
        Settings.Secure.getString(contentResolver, "enabled_notification_listeners")

    // 授予通知监听权限
    if (enabledListenerPackages?.contains(
            ComponentName(
                this,
                MediaListenerService::class.java
            ).flattenToString()
        ) != true
    ) {
        Toast.makeText(this, "请授予通知监听权限", Toast.LENGTH_SHORT).show()
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    } else {
        // 检查是否可以在其他应用之上绘制，请求悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(
                this,
                "请授予悬浮窗权限",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(this, "请授予管理所有文件权限", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = "package:${packageName}".toUri()
                })
            } else {
                onSuccess()
            }
        } else {
            onSuccess()
        }
    }
}

fun ComponentActivity.gotoPermissionSetting() {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = "package:$packageName".toUri()
    }
    startActivity(intent)
}
