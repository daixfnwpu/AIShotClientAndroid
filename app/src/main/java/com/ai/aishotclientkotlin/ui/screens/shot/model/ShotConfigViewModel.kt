package com.ai.aishotclientkotlin.ui.screens.shot.model


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ai.aishotclientkotlin.data.repository.ShotConfigRespository
import com.ai.aishotclientkotlin.domain.model.bi.entity.ShotConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


data class ShotConfigRow(
    var shotConfig: ShotConfig,
    var isDefault: Boolean,
    var title: String,
    var isSelected: Boolean,
    var isShowDetailConfigUI: Boolean
)


@HiltViewModel
class ShotConfigViewModel @Inject constructor(val shotConfigRespository : ShotConfigRespository)  : ViewModel() {

    var isShowShotConfigDetail = mutableStateOf(false)

    // 管理多个 ShotConfigBaseViewModel 实例
    private val _configList = mutableStateListOf<ShotConfig>()
    val configList: SnapshotStateList<ShotConfig> = _configList
    // 保存所有的配置行数据
    var rows = SnapshotStateList<ShotConfigRow>()
        private set

    init {
        // 初始化时，可能会加载一些默认配置
        viewModelScope.launch(Dispatchers.IO) {
            shotConfigRespository.loadShotConfigs(success = {
                // 处理成功逻辑
            }, error = {
                // 处理错误逻辑
            }).collectLatest { configs ->
                _configList.addAll(configs)

            }
        }
    }

    fun updateConfigList(index: Int, newConfig: ShotConfig) {
        _configList[index] = newConfig
    }

    // 添加新配置行
    fun addRow(config: ShotConfig) {
        rows.add(
            ShotConfigRow(
                isDefault = false,
                title = "配置标题",
                isSelected = false,
                isShowDetailConfigUI = false,
                shotConfig = config
            )
        )
    }

    // 删除选中的配置行
    fun deleteSelectedRows() {
        rows.removeAll { it.isSelected }
        for (index in rows) {
            viewModelScope.launch {
                shotConfigRespository.deleteConfig(index.shotConfig.configUI_id)
            }
        }
    }

    fun applyConfig(index: Int) {
        rows.forEachIndexed { i, configRow ->
            rows[i] = configRow.copy(isDefault = (i == index))
        }
    }


    // 更新复选框状态
    fun updateRowSelection(index: Int, isSelected: Boolean) {
        rows[index] = rows[index].copy(isSelected = isSelected)
    }
}

