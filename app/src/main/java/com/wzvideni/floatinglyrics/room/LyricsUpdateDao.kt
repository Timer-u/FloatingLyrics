package com.wzvideni.floatinglyrics.room

import androidx.room.Dao
import androidx.room.Query

// 歌词更新表访问对象
@Dao
interface LyricsUpdateDao {

    @Query("SELECT updateDate FROM LyricsUpdate WHERE title = :title AND album = :album")
    fun queryUpdateDate(title: String, album: String): Long

    @Query("INSERT INTO LyricsUpdate (title, album, updateDate) VALUES (:title, :album, :date)")
    fun insertUpdateDate(title: String, album: String, date: Long)

    @Query("UPDATE LyricsUpdate SET updateDate = :date WHERE title = :title AND album = :album")
    fun updateUpdateDate(title: String, album: String, date: Long)
}