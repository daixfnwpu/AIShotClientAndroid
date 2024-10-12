package com.ai.aishotclientkotlin.ui.screens.shot.model.show


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class ShotConfigViewModel @Inject constructor(private val shotConfigRespository : ShotConfigRespository)  : ViewModel() {

    var selectConfigID: MutableState<Long>  = mutableLongStateOf(-1L)
    var isShowShotConfigDetail = mutableStateOf(false)
    // 保存所有的配置行数据
    var rows = SnapshotStateList<ShotConfigRow>()
        private set


    // 添加新配置行
    fun addRow(config: ShotConfig) {
        rows.add(
            ShotConfigRow(
                isDefault = false,
                title = "配置标题",
                isSelected = false,
                shotConfig = config
            )
        )
//        viewModelScope.launch {
//            shotConfigRespository.addConfig(config)
//        }

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
                            isSelected = false,  title = (it.radius_mm).toString()) })
                    }
                }

            }
        }
    }
}
