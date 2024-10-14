package com.ai.aishotclientkotlin.data.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "rubber_thickness")
data class RubberThickness(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceProfileId: Long,  // 关联 DeviceProfile
    @ColumnInfo(name = "thickness") val thickness: Float
)


@Entity(tableName = "initial_rubber_length")
data class InitialRubberLength(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceProfileId: Long,  // 关联 DeviceProfile
    @ColumnInfo(name = "length") val length: Float
)


@Entity(tableName = "rubber_width")
data class RubberWidth(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceProfileId: Long,  // 关联 DeviceProfile
    @ColumnInfo(name = "width") val width: Float
)


@Entity
data class DeviceProfileWithShotConfigs(
    @Embedded val deviceProfile: DeviceProfile,
    @Relation(
        parentColumn = "id",
        entityColumn = "device_profile_id"
    )
    val shotConfigs: List<ShotConfig>,
    @Relation(
        parentColumn = "rubber_thickness_id",
        entityColumn = "id"
    )
    val rubberThickness: RubberThickness?,
    @Relation(
        parentColumn = "initial_rubber_length_id",
        entityColumn = "id"
    )
    val initialRubberLength: InitialRubberLength?,
    @Relation(
        parentColumn = "rubber_width_id",
        entityColumn = "id"
    )
    val rubberWidth: RubberWidth?
)

