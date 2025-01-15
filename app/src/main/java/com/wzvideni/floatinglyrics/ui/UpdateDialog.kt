package com.wzvideni.floatinglyrics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.wzvideni.floatinglyrics.viewmodel.UpdateViewModel

// 检查更新提示框
@Composable
fun UpdateDialog(
    updateViewModel: UpdateViewModel,
) {
    val isisLatest by updateViewModel.isLatest.collectAsState()
    val lanZouFile by updateViewModel.lanZouFile.collectAsState()
    if (!isisLatest) {
        AlertDialog(
            onDismissRequest = { updateViewModel.setIsisLatest(true) },
            confirmButton = {
                TextButton(onClick = {
                    updateViewModel.getDownloadId()
                }) {
                    Text("下载")
                }
            },
            dismissButton = {
                TextButton(onClick = { updateViewModel.setIsisLatest(true) }) {
                    Text("取消")
                }
            },
            title = { Text("检查更新") },
            text = {
                Column {
                    Text("检查到新版本：${lanZouFile?.nameAll}")
                    Text("更新日期：${lanZouFile?.time}")
                }
            }
        )
    }
}