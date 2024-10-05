package com.ai.aishotclientkotlin.ui.screens.shot.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.ai.aishotclientkotlin.domain.model.bi.entity.ShotConfig
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigBaseViewModel

import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.util.ShotConfigRow


@Composable
fun ShotConfigGrid(viewModel: ShotConfigViewModel = hiltViewModel()) {
    val  isShowConfigDetail by remember {
        mutableStateOf(viewModel.isShowShotConfigDetail)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // 添加和删除按钮
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { viewModel.showShotConfigDetailScreen(true) }) {
                Text("添加")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.deleteSelectedRows() }) {
                Text("删除")
            }
        }
        if (isShowConfigDetail.value) {
            ShotConfigDetailScreen(
                onDismiss = { viewModel.showShotConfigDetailScreen(false) },
                onSave ={it,config ->
                    if (it == -1) viewModel.addRow(config)
                }
            )
        }
        // 显示配置行
        LazyColumn {
            itemsIndexed(viewModel.configList) { index, shotConfig ->
                if (index < viewModel.rows.size) {
                    val baseViewModel = viewModel.getRowViewModel(index)
                    baseViewModel.bind(shotConfig)
                    ShotConfigRowItem(
                        viewModel = baseViewModel,
                        row = viewModel.rows[index],
                        onApply = { viewModel.applyConfig(index) },
                        onSelect = { isSelected ->
                            viewModel.updateRowSelection(
                                index,
                                isSelected
                            )
                        },
                        isShowShotConfigDetail = viewModel.isShowShotConfigDetail,
                    )
                }
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

}