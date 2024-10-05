package com.ai.aishotclientkotlin.ui.screens.shot.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.ai.aishotclientkotlin.domain.model.bi.entity.ShotConfig
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigBaseViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigRow
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigViewModel

@Composable
fun ShotConfigGrid(viewModel: ShotConfigViewModel = viewModel()) {


    Column {
        // 添加和删除按钮
        Row {
            Button(onClick = { viewModel.isShowShotConfigDetail.value = true }) {
                Text("添加")
            }
            Button(onClick = { viewModel.deleteSelectedRows() }) {
                Text("删除")
            }
        }

        // 显示配置行
        LazyColumn {
            itemsIndexed(viewModel.configList) { index,shotConfig  ->
                val baseviewModel: ShotConfigBaseViewModel = hiltViewModel()
                baseviewModel.bind(shotConfig)
                ShotConfigRowItem(
                    viewModel =baseviewModel ,
                    row = viewModel.rows[index],
                    onApply = { viewModel.applyConfig(index) },
                    onSelect = { isSelected -> viewModel.updateRowSelection(index, isSelected) },
                    isShowShotConfigDetail = viewModel.isShowShotConfigDetail,
//                    onSave = { newConfig ->
//                        // 当保存时，更新 ConfigViewModel 中的对应配置
//                        baseviewModel.updateConfig(newConfig)
//                    }

                )
            }
        }
    }
}
@Composable
fun ShotConfigRowItem(
    viewModel: ShotConfigBaseViewModel,
    row: ShotConfigRow,
    isShowShotConfigDetail: MutableState<Boolean>,
    onApply: () -> Unit,
    onSelect: (Boolean) -> Unit,
   // onSave: (ShotConfig) -> Unit // 添加保存回调
) {
    var showEditDialog by remember { mutableStateOf(false) }
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        // 星号
        item {
            Text(
                text = if (row.isDefault) "★" else "☆",
                color = if (row.isDefault) Color.Red else Color.Black
            )
        }
        // 配置标题
        item {
            Text(text = row.title)
        }
        // 修改按钮
        item {
            Button(onClick = { isShowShotConfigDetail.value = !isShowShotConfigDetail.value /* 修改配置逻辑 */ }) {
                Text("修改")
            }
        }
        // 下发配置按钮
        item {
            Button(onClick = onApply) {
                Text("下发配置")
            }
        }
        // 复选框
        item {
            Checkbox(
                checked = row.isSelected,
                onCheckedChange = onSelect
            )
        }
    }
    if (isShowShotConfigDetail.value) {
        ShotConfigDetailScreen(
            id = viewModel.configUI_id.toString(),
            viewModel = viewModel,
            onDismiss = { isShowShotConfigDetail.value = false },
        )
    }
}