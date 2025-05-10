package com.wzvideni.floatinglyrics

import android.app.Application
import com.wzvideni.floatinglyrics.viewmodel.PlayingStateViewModel
import com.wzvideni.floatinglyrics.viewmodel.SharedPreferencesViewModel
import com.wzvideni.floatinglyrics.viewmodel.UpdateViewModel

class MainApplication : Application() {

    companion object {
        lateinit var instance: MainApplication

    }

    val playingStateViewModel: PlayingStateViewModel by lazy { PlayingStateViewModel() }
    val sharedPreferencesViewModel: SharedPreferencesViewModel by lazy {
        SharedPreferencesViewModel(
            this
        )
    }
    val updateViewModel: UpdateViewModel by lazy { UpdateViewModel(this) }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}