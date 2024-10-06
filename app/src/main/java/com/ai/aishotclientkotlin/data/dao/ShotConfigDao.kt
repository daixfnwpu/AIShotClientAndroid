package com.ai.aishotclientkotlin.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig

@Dao
interface ShotConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShotConfig(shotConfig: ShotConfig): Long

    @Update
    suspend fun updateShotConfig(shotConfig: ShotConfig)

    @Query("SELECT * FROM ShotConfig WHERE configUI_id = :id_")
    suspend fun getShotConfig(id_: Long): ShotConfig

    @Query("SELECT * FROM ShotConfig ")
    suspend fun getShotAllConfig(): List<ShotConfig>

    @Query("SELECT * FROM ShotConfig WHERE configUI_id = :id")
    suspend fun getConfigById(id: Long): ShotConfig?

    @Query("DELETE FROM ShotConfig WHERE configUI_id = :id ")
    suspend fun deleteConfig(id: Long):Int
}
