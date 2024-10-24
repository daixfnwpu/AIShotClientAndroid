package com.ai.aishotclientkotlin.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ai.aishotclientkotlin.data.dao.entity.DeviceProfile

/**
 * 默认情况下： 数据库中只有一条记录；
 */

@Dao
interface DeviceProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDeviceProfiles(reviews: List<DeviceProfile>)

    @Update
    suspend fun updateDeviceProfile(shotConfig: DeviceProfile)

    @Query("SELECT * FROM DeviceProfile WHERE id = :id_")
    suspend fun getDeviceProfile(id_: Long): DeviceProfile

    @Query("SELECT * FROM DeviceProfile ")
    suspend fun getAllDeviceProfile(): List<DeviceProfile>

    @Query("SELECT * FROM DeviceProfile WHERE id = :id")
    suspend fun getDeviceProfileById(id: Long): DeviceProfile?

    @Query("DELETE FROM DeviceProfile WHERE id = :id ")
    suspend fun deleteDeviceProfile(id: Long):Int
}