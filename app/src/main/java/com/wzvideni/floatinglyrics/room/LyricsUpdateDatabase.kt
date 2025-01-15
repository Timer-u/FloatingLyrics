package com.wzvideni.floatinglyrics.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// entities：实体类集合
// 歌词更新数据库
@Database(entities = [LyricsUpdate::class], version = 1)
abstract class LyricsUpdateDatabase : RoomDatabase() {

    // 关联LyricsDao和LyricsDatabase
    abstract fun lyricsUpdateDao(): LyricsUpdateDao

    companion object {
        @Volatile
        private var INSTANCE: LyricsUpdateDatabase? = null

        // 获取数据库实例
        fun getLyricsDatabase(context: Context): LyricsUpdateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, LyricsUpdateDatabase::class.java, "LyricsUpdate"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}