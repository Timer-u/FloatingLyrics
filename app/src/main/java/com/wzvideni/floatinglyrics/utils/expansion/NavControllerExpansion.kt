package com.wzvideni.floatinglyrics.utils.expansion

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.wzvideni.floatinglyrics.ui.moedl.Pages

fun NavController.navigateTo(page: Pages) {
    navigate(page.route) {
        // 在导航之前从后退堆栈中弹出所有不匹配的目标，直到找到与NavController关联的最顶层导航图
        // 用于防止多次点击多个子项导致的后退栈堆积
        popUpTo(graph.findStartDestination().id) {
            // saveState用于最顶层导航图页面状态的恢复
            saveState = true
        }
        // 避免多次点击同一个子项时产生多个实例
        launchSingleTop = true
        // 再次点击之前的子项时，恢复状态
        restoreState = true
    }
}

