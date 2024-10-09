package com.ai.aishotclientkotlin.ui.screens.shot.model


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ai.aishotclientkotlin.data.repository.ShotConfigRespository
import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig
import com.ai.aishotclientkotlin.ui.screens.shot.util.ShotConfigRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject





@HiltViewModel
class ShotConfigViewModel @Inject constructor(val shotConfigRespository : ShotConfigRespository)  : ViewModel() {

    var selectConfigID: MutableState<Long>  = mutableLongStateOf(-1L)
    var isShowShotConfigDetail = mutableStateOf(false)
    private val rowViewModels = mutableStateMapOf<Long, ShotConfigBaseViewModel>()
    // 保存所有的配置行数据
    var rows = SnapshotStateList<ShotConfigRow>()
        private set

    init {
        // 初始化时，可能会加载一些默认配置

    }
    // 获取对应行的 ViewModel
    fun getRowViewModel(index: Long): ShotConfigBaseViewModel {
        return rowViewModels.getOrPut(index) {
            ShotConfigBaseViewModel(shotConfigRespository)
        }
    }

    fun showShotConfigDetailScreen(show: Boolean = true) {
        isShowShotConfigDetail.value = show
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
        viewModelScope.launch {
            shotConfigRespository.addConfig(config)
        }

    }

    // 删除选中的配置行
    fun deleteSelectedRows() {
        val selectedRows = rows.filter { it.isSelected }

        // 启动 ViewModel 作用域，批量处理删除操作
        viewModelScope.launch {
            // 遍历选中的 rows，逐个进行删除
            for (row in selectedRows) {
                launch {
                    row.shotConfig.configUI_id?.let { shotConfigRespository.deleteConfig(it) }
                }
            }
            // 在数据库删除之后，才从 rows 集合中移除已选中的行
            rows.removeAll { it.isSelected }
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

    fun loadShotConfigs() {

        viewModelScope.launch(Dispatchers.IO) {
            shotConfigRespository.loadShotConfigs(success = {
                // 处理成功逻辑
                Log.e("shotConfigRespository","shotConfigRespository loadShotConfigs succeces")
            }, error = {
                // 处理错误逻辑
                Log.e("shotConfigRespository","shotConfigRespository loadShotConfigs error")
            }).collectLatest { configs ->
                if(configs.isNotEmpty()){
                    withContext(Dispatchers.Main) {
                        rows.clear()
                        Log.e("Config"," _configList.addAll(configs),${configs.size}")
                        rows.addAll(configs.map { ShotConfigRow(shotConfig = it, isDefault = it.isalreadyDown == 1,
                            isSelected = false, isShowDetailConfigUI = false, title = it.radius_mm.toString()) })
                    }
                }

            }
        }
    }
}
