package com.wzvideni.floatinglyrics

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wzvideni.floatinglyrics.databinding.LayoutFloatingLyricsBinding
import com.wzvideni.floatinglyrics.network.model.Lyric
import com.wzvideni.floatinglyrics.network.qqMusicLyricRequest
import com.wzvideni.floatinglyrics.network.wyyMusicLyricRequest
import com.wzvideni.floatinglyrics.ui.basic.PrimaryIcon
import com.wzvideni.floatinglyrics.ui.basic.PrimaryText
import com.wzvideni.floatinglyrics.ui.isDarkTheme
import com.wzvideni.floatinglyrics.ui.moedl.Pages
import com.wzvideni.floatinglyrics.ui.page.LyricsPage
import com.wzvideni.floatinglyrics.ui.page.MediaListeningPage
import com.wzvideni.floatinglyrics.ui.page.SearchPage
import com.wzvideni.floatinglyrics.ui.page.SettingPage
import com.wzvideni.floatinglyrics.ui.theme.FloatingLyricsTheme
import com.wzvideni.floatinglyrics.utils.expansion.navigateTo
import com.wzvideni.floatinglyrics.utils.expansion.openDocumentTreeActivityResultLauncher
import com.wzvideni.floatinglyrics.utils.expansion.persistableUriPermissionModeFlags
import com.wzvideni.floatinglyrics.utils.expansion.requestCustomPermissions
import com.wzvideni.floatinglyrics.utils.expansion.requestReadMusicPermissionLauncher
import com.wzvideni.floatinglyrics.utils.view.OnTouchView
import com.wzvideni.floatinglyrics.utils.view.addLockedFloatingView
import com.wzvideni.floatinglyrics.utils.view.updateLockedFloatingViewLayout
import com.wzvideni.floatinglyrics.utils.view.updateUnLockedFloatingViewLayout
import com.wzvideni.floatinglyrics.viewmodel.PlayingStateViewModel
import com.wzvideni.floatinglyrics.viewmodel.SharedPreferencesViewModel
import com.wzvideni.floatinglyrics.viewmodel.SharedPreferencesViewModelFactory
import com.wzvideni.floatinglyrics.viewmodel.UpdateViewModel
import com.wzvideni.floatinglyrics.viewmodel.UpdateViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

// 播放状态的ViewModel
lateinit var playingStateViewModel: PlayingStateViewModel

// SharedPreferences配置的设置的ViewModel
lateinit var sharedPreferencesViewModel: SharedPreferencesViewModel

// 检查更新的ViewModel
lateinit var updateViewModel: UpdateViewModel

class MainActivity : ComponentActivity() {
    // 网络连接管理器
    private lateinit var connectivityManager: ConnectivityManager

    // 媒体监听服务相关
    private lateinit var mediaListenerBinder: MediaListenerService.MediaListenerBinder
    private lateinit var mediaListenerService: MediaListenerService
    private lateinit var mediaListenerIntent: Intent

    // 服务是否绑定
    private var isBound = false

    // 定义绑定服务的回调
    private val connection = object : ServiceConnection {
        // 与服务建立连接
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mediaListenerBinder = service as MediaListenerService.MediaListenerBinder
            mediaListenerService = mediaListenerBinder.getMediaListenerService()
            isBound = true
        }

        // 与服务连接丢失
        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    // 浮动歌词视图
    private lateinit var floatingLyricsView: View

    // 悬浮歌词约束布局
    private lateinit var floatingLyricsConstraintLayout: ConstraintLayout

    // 水平歌词、翻译的文本视图
    private lateinit var horizontalLyricsTextView: TextView
    private lateinit var horizontalTranslationTextView: TextView

    // 垂直歌词、翻译的文本视图
    private lateinit var verticalLyricsTextView: TextView
    private lateinit var verticalTranslationTextView: TextView

    // 打开文档树启动器回调（只能在onCreate()方法内注册）
    private lateinit var openDocumentTree: ActivityResultLauncher<Uri?>

    // 权限请求启动器回调（只能在onCreate()方法内注册）
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    // 应用版本号
    private var versionCode: Long = 0

    override fun onStop() {
        super.onStop()
        // 保存SharedPreferences配置的设置
        lifecycleScope.launch {
            sharedPreferencesViewModel.saveSharedPreferences()
        }
    }

    // 设备配置变更时会执行到此方法
    override fun onDestroy() {
        super.onDestroy()
        destroy()
    }

    // 执行销毁操作
    private fun destroy() {
        // 视图已经添加到窗口管理器中时才移除浮动窗口，防止销毁不存在的View
        if (floatingLyricsView.windowToken != null) {
            windowManager.removeView(floatingLyricsView)
        }

        // 注销旧回调
        openDocumentTree.unregister()
        requestPermissionLauncher.unregister()
        onBackPressedCallback.remove()
        // 解绑服务
        unbindService(connection)
        isBound = false
        // 停止服务，根本停不下来！！！
        stopService(mediaListenerIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 启用EdgeToEdge
        enableEdgeToEdge()
        // 初始化视图和变量
        initializeViewAndVariable()
        setContent {
            // 初始化ViewModel
            InitializeViewModel()
            val themeState by sharedPreferencesViewModel.currentTheme.collectAsState()
            // 导航控制器
            val navController = rememberNavController()
            var selectedItem by rememberSaveable { mutableStateOf(Pages.Home.route) }

            // 主导航列表，不使用局部变量，是为了防止重组时的重复创建
            @Stable
            fun createMainNavList(): List<Pages> {
                return listOf(
                    Pages.Home,
                    Pages.Lyrics,
                    Pages.Search,
                    Pages.Setting
                )
            }

            // 底部导航页面
            FloatingLyricsTheme(
                darkTheme = isDarkTheme(themeState)
            ) {
                // 设置navController的监听器，在destination发生改变时更新selectedItem的值，并在Composable进入onDispose注销监听器
                // 用于监听页面是否跳转，解决通过非点击底部导航栏的方式跳转的页面，selectedItem值不会被更新的问题
                DisposableEffect(navController) {
                    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                        selectedItem = destination.route ?: selectedItem
                    }
                    navController.addOnDestinationChangedListener(listener)

                    onDispose {
                        navController.removeOnDestinationChangedListener(listener)
                    }
                }

                Scaffold(
                    bottomBar = {
                        // 底部导航栏
                        BottomAppBar {
                            createMainNavList().forEach { page: Pages ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null, // 设置为 null，以取消涟漪效果
                                            onClick = {
                                                selectedItem = page.route
                                                navController.navigateTo(page)
                                            }
                                        ),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // 如果底部导航栏的子项被选中
                                    if (selectedItem == page.route) {
                                        PrimaryIcon(page.selectedIcon)
                                        Spacer(modifier = Modifier.height(5.dp))
                                        PrimaryText(
                                            text = page.name,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Icon(page.defaultIcon, contentDescription = null)
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Text(
                                            text = page.name,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Pages.Home.route,
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding) // 为了enableEdgeToEdge()设置
                    ) {
                        // 主页
                        composable(Pages.Home.route) {
                            MediaListeningPage(
                                playingStateViewModel = playingStateViewModel,
                                updateViewModel = updateViewModel,
                                addView = {
                                    // 悬浮歌词的父视图为空时才添加悬浮窗，防止重复添加
                                    // 如果使用视图所附加到的窗口的唯一标记：windowToken，则会出现导致因重复添加视图而崩溃的情况
                                    if (floatingLyricsView.parent == null) {
                                        windowManager.addLockedFloatingView(floatingLyricsView)
                                    }
                                    // 设置悬浮歌词字体样式
                                    sharedPreferencesViewModel.setFloatingLyricsTypeStyle(
                                        horizontalLyricsTextView,
                                        horizontalTranslationTextView,
                                        verticalLyricsTextView,
                                        verticalTranslationTextView
                                    )
                                },
                                onClickTopIcon = {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "吾爱破解论坛@wzvideni",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onClickToListen = {
                                    requestCustomPermissions(requestPermissionLauncher)
                                },
                                onClickToFinish = {
                                    // 执行销毁操作
                                    destroy()
                                    // 正常退出进程
                                    exitProcess(0)
                                    // 强行杀死应用程序 android.os.Process.killProcess(android.os.Process.myPid())
                                }
                            )
                        }
                        // 歌词页面
                        composable(Pages.Lyrics.route) {
                            LyricsPage(playingStateViewModel) { lyricList: List<Lyric> ->
                                lifecycleScope.launch {
                                    if (!mediaListenerService.saveLyricListToFile(lyricList)) {
                                        if (playingStateViewModel.musicPath.value == "") {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "保存歌词失败：系统媒体数据库未更新",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else if (playingStateViewModel.persistedUriPermissionsList.value.isEmpty()) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "保存歌词失败：请添加歌词保存目录",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "保存歌词成功",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }

                        // 歌词搜索页面
                        composable(Pages.Search.route) {
                            var isRefreshing by remember { mutableStateOf(false) }

                            SearchPage(
                                isRefreshing = isRefreshing,
                                onRefresh = {
                                    // 获取活动的网络信息并判断是否为空，为空则没有网络连接
                                    if (connectivityManager.activeNetwork != null) {
                                        lifecycleScope.launch {
                                            isRefreshing = true
                                            delay(500)
                                            // 只有playingViewModel里的标题和艺术家不为空字符串时才搜索
                                            if (playingStateViewModel.isNotEmptyOfSearchKeyword()) {
                                                playingStateViewModel.musicSearch()
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "没有监听到媒体通知",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            delay(500)
                                            isRefreshing = false
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "没有网络连接",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onClickToRequestQQMusicLyric = { musicId: String ->
                                    lifecycleScope.launch {
                                        val lyricsList =
                                            qqMusicLyricRequest(musicId)
                                        if (lyricsList.isEmpty()) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "歌词列表为空",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            navController.navigateTo(Pages.Lyrics)
                                            playingStateViewModel.setLyricsList(lyricsList)
                                            playingStateViewModel.setQuery("QQ音乐")
                                            playingStateViewModel.setLyricsPath("")
                                        }
                                    }
                                },
                                onClickToRequestWyyMusicLyric = { musicId: String ->
                                    lifecycleScope.launch {
                                        val lyricsList =
                                            wyyMusicLyricRequest(musicId)
                                        if (lyricsList.isEmpty()) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "歌词列表为空",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            navController.navigateTo(Pages.Lyrics)
                                            playingStateViewModel.setLyricsList(lyricsList)
                                            playingStateViewModel.setQuery("网易云音乐")
                                            playingStateViewModel.setLyricsPath("")
                                        }
                                    }
                                }
                            )
                        }
                        // 设置页面
                        composable(Pages.Setting.route) {
                            SettingPage(
                                playingStateViewModel = playingStateViewModel,
                                sharedPreferencesViewModel = sharedPreferencesViewModel,
                                horizontalLyricsTextView = horizontalLyricsTextView,
                                horizontalTranslationTextView = horizontalTranslationTextView,
                                verticalLyricsTextView = verticalLyricsTextView,
                                verticalTranslationTextView = verticalTranslationTextView,
                                onClickToRemovePath = { uri: Uri ->
                                    revokeUriPermission(
                                        uri,
                                        persistableUriPermissionModeFlags
                                    )
                                    playingStateViewModel.setPersistedUriPermissionsList(
                                        contentResolver.persistedUriPermissions
                                    )
                                },
                                onClickToAddPath = {
                                    openDocumentTree.launch(null)
                                },
                                onClickToUnlockFloatingView = {
                                    windowManager.updateUnLockedFloatingViewLayout(
                                        floatingLyricsView
                                    )
                                    Toast.makeText(
                                        this@MainActivity,
                                        "悬浮歌词已解锁（双击任何位置重新锁定）",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // 初始化视图和变量
    private fun initializeViewAndVariable() {
        // 使用数据绑定工具填充悬浮歌词视图并获取视图和控件
        // 填充浮动歌词视图（android.R.id.content提供了视图的根布局，是一个FrameLayout）
        DataBindingUtil.inflate<LayoutFloatingLyricsBinding>(
            getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.layout_floating_lyrics,
            findViewById(android.R.id.content),
            false
        ).apply {
            // 获取悬浮歌词视图
            floatingLyricsView = root
            // 获取悬浮歌词约束布局
            floatingLyricsConstraintLayout = FloatingLyricsConstraintLayout
            // 获取水平歌词和翻译TextView
            horizontalLyricsTextView = HorizontalLyricsTextView
            horizontalTranslationTextView = HorizontalTranslationTextView
            // 获取垂直歌词和翻译TextView
            verticalLyricsTextView = VerticalLyricsTextView
            verticalTranslationTextView = VerticalTranslationTextView
        }

        // 水平文字跑马灯设置单行
        horizontalLyricsTextView.isSingleLine = true
        horizontalTranslationTextView.isSingleLine = true
        // 水平文字跑马灯获取焦点
        horizontalLyricsTextView.isSelected = true
        horizontalTranslationTextView.isSelected = true

        // 为FloatingLyricsView设置双击事件监听器
        OnTouchView.registerDoubleClickListener(floatingLyricsView) {
            windowManager.updateLockedFloatingViewLayout(floatingLyricsView)
            Toast.makeText(this, "悬浮歌词已锁定", Toast.LENGTH_SHORT).show()
        }

        // 为TextView设置拖动事件监听器
        OnTouchView.registerDragListener(this, horizontalLyricsTextView)
        OnTouchView.registerDragListener(this, horizontalTranslationTextView)
        OnTouchView.registerDragListener(this, verticalLyricsTextView)
        OnTouchView.registerDragListener(this, verticalTranslationTextView)

        // 获取网络连接管理器实例
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        // 媒体监听服务相关
        mediaListenerIntent = Intent(this, MediaListenerService::class.java)
        // 绑定服务
        bindService(mediaListenerIntent, connection, BIND_AUTO_CREATE)

        // 注册openDocumentTree回调（只能在onCreate()方法内注册）
        openDocumentTree = openDocumentTreeActivityResultLauncher()

        // 注册请求读取音乐权限回调（只能在onCreate()方法内注册）
        requestPermissionLauncher = requestReadMusicPermissionLauncher {
            // 检索标识此视图所附加到的窗口的唯一标记为空时才添加悬浮窗，防止重复添加
            if (floatingLyricsView.windowToken == null) {
                windowManager.addLockedFloatingView(floatingLyricsView)
            }
            // 启动媒体监听服务
            startService(mediaListenerIntent)
        }

        // 获取返回手势回调
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 处理返回操作，返回桌面
                startActivity(
                    Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
        }
        // 注册返回手势回调
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        // 获取应用版本号
        versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(this.packageName, 0).longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(this.packageName, 0).versionCode.toLong()
        }
    }

    @Composable
    fun InitializeViewModel() {
        // 播放状态的ViewModel
        playingStateViewModel = viewModel()
        playingStateViewModel.setPersistedUriPermissionsList(contentResolver.persistedUriPermissions)

        // 收集流的值设置TextView的文本
        LaunchedEffect(Unit) {
            playingStateViewModel.lyric.collect { lyric ->
                horizontalLyricsTextView.text = lyric
                verticalLyricsTextView.text = lyric
            }
        }
        // 收集流的值设置TextView的文本
        LaunchedEffect(Unit) {
            playingStateViewModel.translation.collect { translation ->
                horizontalTranslationTextView.text = translation
                verticalTranslationTextView.text = translation
            }
        }

        // 检查更新的ViewModel
        updateViewModel = viewModel(factory = UpdateViewModelFactory(versionCode, application))

        // SharedPreferences配置的设置的ViewModel
        sharedPreferencesViewModel =
            viewModel(factory = SharedPreferencesViewModelFactory(application))
    }
}