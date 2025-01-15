package com.wzvideni.floatinglyrics.ui.basic.setting

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wzvideni.floatinglyrics.ui.basic.SettingsTextWithPadding

@Composable
inline fun <T : Number> SettingWithDoubleArrow(
    startText: String,
    settingValue: T,
    crossinline leftArrowOnClick: () -> Unit,
    crossinline rightArrowOnClick: () -> Unit,
    crossinline settingValueOnLongClick: () -> Unit,
) {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = startText,
            modifier = Modifier
                .padding(start = 10.dp)
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { leftArrowOnClick() }) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = "ArrowBackIosNew"
            )
        }
        SettingsTextWithPadding(
            text = settingValue.toString(),
            onClick = {
                Toast.makeText(
                    context,
                    "长按可以重置该值",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onLongClick = {
                Toast.makeText(
                    context,
                    "已重置",
                    Toast.LENGTH_SHORT
                ).show()
                settingValueOnLongClick()
            }
        )

        IconButton(onClick = { rightArrowOnClick() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                contentDescription = "ArrowForwardIos"
            )
        }
    }
}