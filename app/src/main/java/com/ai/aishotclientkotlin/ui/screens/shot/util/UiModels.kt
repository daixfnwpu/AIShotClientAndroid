package com.ai.aishotclientkotlin.ui.screens.shot.util

import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig

data class ShotConfigRow(
    var shotConfig: ShotConfig,
    var isDefault: Boolean,
    var title: String,
    var isSelected: Boolean,
   // var isShowDetailConfigUI: Boolean
)
