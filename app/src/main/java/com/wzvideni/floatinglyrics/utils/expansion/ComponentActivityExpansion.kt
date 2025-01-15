package com.wzvideni.floatinglyrics.utils.expansion

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.wzvideni.floatinglyrics.MediaListenerService
import com.wzvideni.floatinglyrics.playingStateViewModel

// 持久化权限标志
const val persistableUriPermissionModeFlags =
    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

// 注册openDocumentTree回调（只能在onCreate()方法内注册）
fun ComponentActivity.openDocumentTreeActivityResultLauncher(): ActivityResultLauncher<Uri?> {
    // 获取打开文档树的活动结果启动器，并为其注册回调
    return registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
        it?.let { uri: Uri ->
            // 持久化权限
            contentResolver.takePersistableUriPermission(uri, persistableUriPermissionModeFlags)
            // 更新所有持久化Uri权限的列表
            playingStateViewModel.setPersistedUriPermissionsList(contentResolver.persistedUriPermissions)
        }
    }
}

// 注册请求读取音乐权限回调（只能在onCreate()方法内注册）
fun ComponentActivity.requestReadMusicPermissionLauncher(
    isGranted: () -> Unit,
): ActivityResultLauncher<String> {
    // 注册请求单个权限的回调
    return registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            isGranted()
        } else {
            gotoPermissionSetting()
            Toast.makeText(
                this,
                "请授予读取媒体音乐权限",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

// 请求自定义权限
fun ComponentActivity.requestCustomPermissions(requestReadMusicPermissionLauncher: ActivityResultLauncher<String>) {
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
        } else {
            // 请求读取音乐权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestReadMusicPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                requestReadMusicPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}

fun ComponentActivity.gotoPermissionSetting() {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.parse("package:$packageName")
    }
    startActivity(intent)
}
