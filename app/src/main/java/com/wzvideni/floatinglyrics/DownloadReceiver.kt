package com.wzvideni.floatinglyrics

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class DownloadReceiver : BroadcastReceiver() {

    val updateViewModel by lazy { MainApplication.instance.updateViewModel }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadId == updateViewModel.downloadId) {
            val status = checkDownloadStatus(context, downloadId)
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                Toast.makeText(context, "下载完成！请点击安装", Toast.LENGTH_SHORT).show()
                installApk(context, downloadId)
            }
        }
    }
}

// 动态注册Receiver
fun registerDownloadReceiver(context: Context, downloadReceiver: DownloadReceiver) {
    val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.registerReceiver(
            downloadReceiver,
            intentFilter,
            Activity.RECEIVER_NOT_EXPORTED
        )
    } else {
        // API 33以下使用反射调用无标志的registerReceiver
        try {
            // 这行代码使用反射API获取Context类中的registerReceiver方法引用。
            // getMethod 方法需要两个参数：方法名和方法参数类型。这里，registerReceiver方法的参数类型是BroadcastReceiver和IntentFilter。
            // invoke用于调用上述获取的方法。invoke方法的第一个参数是调用该方法的对象实例，这里是this，表示当前Context，后面的参数是传递给这个方法的实际参数。
            Context::class.java.getMethod(
                "registerReceiver",
                BroadcastReceiver::class.java,
                IntentFilter::class.java
            ).invoke(context, DownloadReceiver(), intentFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun installApk(context: Context, downloadId: Long) {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val query = DownloadManager.Query().setFilterById(downloadId)
    val cursor = downloadManager.query(query)

    if (cursor.moveToFirst()) {
        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
        val fileUri = cursor.getString(columnIndex)
        val apkFile = File(Uri.parse(fileUri).path!!)
        installApkFromFile(context, apkFile)
    }
    cursor.close()
}

fun installApkFromFile(context: Context, apkFile: File) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        val apkUri: Uri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apkFile)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        setDataAndType(apkUri, "application/vnd.android.package-archive")
    }
    context.startActivity(intent)
}

fun installApkFromUri(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(intent)
}

fun checkDownloadStatus(context: Context, downloadId: Long): Int {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val query = DownloadManager.Query().setFilterById(downloadId)
    val cursor = downloadManager.query(query)
    if (cursor.moveToFirst()) {

        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        if (columnIndex >= 0) {
            return cursor.getInt(columnIndex)
        }
    }
    return DownloadManager.STATUS_FAILED
}