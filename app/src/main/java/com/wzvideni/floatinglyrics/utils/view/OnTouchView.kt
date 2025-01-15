package com.wzvideni.floatinglyrics.utils.view

import android.content.Context
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.core.content.edit


class OnTouchView {
    companion object {
        const val SharedPreferencesTAG = "FloatingLyricsViewPositions"
        fun registerDragListener(context: Context, view: View) {
            // 恢复位置
            restoreViewPosition(context, view)
            // 创建并注册拖动监听器
            val dragListener = DragListener(context)
            view.setOnTouchListener(dragListener)
            // 将DragListener实例存储到视图的tag中，方便后续调用
            view.tag = dragListener
        }

        fun registerDoubleClickListener(view: View, onDoubleClick: () -> Unit) {
            view.setOnTouchListener(DoubleClickListener(onDoubleClick))
        }

        private fun saveInitialPosition(context: Context, view: View) {
            val viewId = view.id
            if (viewId != View.NO_ID) {
                context.getSharedPreferences(SharedPreferencesTAG, Context.MODE_PRIVATE).apply {
                    val initialX = getFloat("${viewId}InitialX", -1f)
                    val initialY = getFloat("${viewId}InitialY", -1f)
                    // 如果初始位置不存在，存入初始位置
                    if (initialX == -1f && initialY == -1f) {
                        edit {
                            // 保存初始位置
                            putFloat("${viewId}InitialX", view.x)
                            putFloat("${viewId}InitialY", view.y)
                        }
                    }
                }
            }
        }

        fun restoreInitialPosition(context: Context, view: View) {
            val viewId = view.id
            if (viewId != View.NO_ID) {
                context.getSharedPreferences(SharedPreferencesTAG, Context.MODE_PRIVATE).apply {
                    val initialX = getFloat("${viewId}InitialX", -1f)
                    val initialY = getFloat("${viewId}InitialY", -1f)
                    // 如果初始位置存在，恢复位置，并重置保存坐标
                    if (initialX != -1f && initialY != -1f) {
                        view.x = initialX
                        view.y = initialY
                        edit {
                            putFloat("${viewId}X", -1f)
                            putFloat("${viewId}Y", -1f)
                        }
                    }
                }
            }
        }

        private fun restoreViewPosition(context: Context, view: View) {
            val viewId = view.id
            if (viewId != View.NO_ID) {
                context.getSharedPreferences(SharedPreferencesTAG, Context.MODE_PRIVATE).apply {
                    val savedX = getFloat("${viewId}X", -1f)
                    val savedY = getFloat("${viewId}Y", -1f)
                    // 只有在保存的坐标不是默认的(-1, -1)时，才恢复位置
                    if (savedX != -1f && savedY != -1f) {
                        view.post {
                            view.x = savedX
                            view.y = savedY
                        }
                    }
                }
            }
        }
    }

    // 拖动监听器
    @Suppress("UNCHECKED_CAST")
    private class DragListener(private val context: Context) : View.OnTouchListener {
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                // 按下时
                MotionEvent.ACTION_DOWN -> {
                    // 如果这是第一次触摸，保存视图的初始位置
                    saveInitialPosition(context, view)
                    // 当前触摸点的原始 X 和 Y 坐标存储在 view.tag
                    view.tag = event.rawX to event.rawY
                }
                // 辅助功能执行单击
                MotionEvent.ACTION_UP -> {
                    view.performClick()
                    // 获取视图唯一标识符
                    val viewId = view.id
                    if (viewId != View.NO_ID) {
                        context.getSharedPreferences(
                            SharedPreferencesTAG,
                            Context.MODE_PRIVATE
                        ).edit {
                            putFloat("${viewId}X", view.x)
                            putFloat("${viewId}Y", view.y)
                        }
                    }
                }
                // 拖动时
                MotionEvent.ACTION_MOVE -> {
                    val (lastX, lastY) = view.tag as Pair<Float, Float>
                    // 计算偏移量
                    val offsetX = event.rawX - lastX
                    val offsetY = event.rawY - lastY

                    // 计算新的位置
                    val newX = view.x + offsetX
                    val newY = view.y + offsetY

                    // 获取屏幕宽度和高度
                    val screenWidth = view.context.resources.displayMetrics.widthPixels
                    val screenHeight = view.context.resources.displayMetrics.heightPixels

                    // 边界检查
                    val maxX = screenWidth - view.width
                    val maxY = screenHeight - view.height
                    // 限制视图位置
                    view.x = newX.coerceIn(0f, maxX.toFloat())
                    view.y = newY.coerceIn(0f, maxY.toFloat())

                    // 当前触摸点的原始 X 和 Y 坐标存储在 view.tag
                    view.tag = event.rawX to event.rawY
                }
            }
            return true
        }
    }

    // 双击监听器
    private class DoubleClickListener(private val onDoubleClick: () -> Unit) :
        View.OnTouchListener {

        private var lastClickTime: Long = 0
        private var clickCount = 0

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 计算两次点击的时间差
                    val currentTime = SystemClock.uptimeMillis()
                    val elapsedTime = currentTime - lastClickTime
                    lastClickTime = currentTime

                    // 如果两次点击的时间差小于一定阈值，增加点击次数
                    if (elapsedTime <= 300) {
                        clickCount++
                    } else {
                        clickCount = 1
                    }

                    // 如果点击次数为2，触发双击事件
                    if (clickCount == 2) {
                        onDoubleClick.invoke()
                        clickCount = 0
                    }
                }
                // 辅助功能执行单击
                MotionEvent.ACTION_UP -> {
                    view.performClick()
                }
            }
            return true
        }
    }
}



