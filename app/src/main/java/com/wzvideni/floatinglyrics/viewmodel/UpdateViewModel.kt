package com.wzvideni.floatinglyrics.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wzvideni.floatinglyrics.installApkFromFile
import com.wzvideni.floatinglyrics.network.checkUpdate
import com.wzvideni.floatinglyrics.network.getLatestFile
import com.wzvideni.floatinglyrics.network.model.LanZouFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class UpdateViewModel(private val versionCode: Long, private val application: Application) :
    AndroidViewModel(application) {

    private val connectivityManager =
        application.getSystemService(Application.CONNECTIVITY_SERVICE) as ConnectivityManager

    var downloadId = 1L
    var _lanZouFile = MutableStateFlow<LanZouFile?>(null)
    val lanZouFile: StateFlow<LanZouFile?> = _lanZouFile

    private val _isLatest = MutableStateFlow(true)
    val isLatest: StateFlow<Boolean> = _isLatest
    fun setIsisLatest(isisLatest: Boolean) {
        _isLatest.value = isisLatest
    }


    init {
        // 判断网络是否连接
        if (connectivityManager.activeNetwork != null) {
            viewModelScope.launch(Dispatchers.IO) {
                // 网络不好的情况下检查更新很容易出现异常，捕捉一下
                try {
                    // 检查lanZouFile是否为空
                    checkUpdate()?.let {
                        val latestCode = Regex("\\d+").find(it.nameAll)?.value?.toLong() ?: 0
                        if (latestCode > versionCode) {
                            _isLatest.value = false
                            _lanZouFile.value = it
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getDownloadId() {
        lanZouFile.value?.let { lanZouFile: LanZouFile ->
            _isLatest.value = true
            viewModelScope.launch {
                getLatestFile(lanZouFile.id)?.let { fileUrl: String ->
                    // 获取APK文件
                    val apkFile = File(
                        application.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                        lanZouFile.nameAll
                    )
                    // 检查文件是否存在
                    if (apkFile.exists()) {
                        Toast.makeText(
                            application,
                            "已下载！请点击安装",
                            Toast.LENGTH_SHORT
                        ).show()
                        installApkFromFile(application, apkFile)
                    } else {
                        Toast.makeText(
                            application,
                            "正在下载中……",
                            Toast.LENGTH_SHORT
                        ).show()
                        downloadId = downloadApkFile(application, fileUrl, lanZouFile)
                    }
                }
            }
        }
    }

    private fun downloadApkFile(
        context: Context,
        fileUrl: String,
        lanZouFile: LanZouFile,
    ): Long {
        // 创建下载请求
        val request = DownloadManager.Request(Uri.parse(fileUrl)).apply {
            // 设置下载网络类型，可以是 WIFI 或者 移动网络
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

            // 设置通知栏可见性
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            // 设置文件保存路径和文件名（私有下载目录）
            // 公有下载目录
            // request.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName)
            setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                lanZouFile.nameAll
            )
        }

        // 获取系统的下载管理器
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        // 将下载请求加入到下载队列，并返回下载任务的唯一标识 ID
        return downloadManager.enqueue(request)
    }

    override fun onCleared() {
        super.onCleared()
        // 如果当前ViewModel中的任务仍处于活动状态，取消协程作用域中的任务
        if (viewModelScope.isActive) {
            viewModelScope.cancel()
        }
    }


}