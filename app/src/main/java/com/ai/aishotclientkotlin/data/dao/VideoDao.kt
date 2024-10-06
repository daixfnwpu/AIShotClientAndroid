package com.ai.aishotclientkotlin.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ai.aishotclientkotlin.data.dao.entity.Video

@Dao
interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(shotConfig: List<Video>)

    @Update
    suspend fun updateVideo(shotConfig: Video)

    @Query("SELECT * FROM video WHERE id = :id_")
    suspend fun getVideo(id_: Long): Video

    @Query("SELECT * FROM video ")
    suspend fun getShotAllConfig(): List<Video>

    @Query("SELECT * FROM video WHERE id = :id")
    suspend fun getConfigById(id: Int): Video?

    @Query("DELETE FROM video WHERE id = :id ")
    suspend fun deleteConfig(id: Int):Int
}
