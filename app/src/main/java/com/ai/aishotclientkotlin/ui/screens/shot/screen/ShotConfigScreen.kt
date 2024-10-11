package com.ai.aishotclientkotlin.ui.screens.shot.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigBaseViewModel

import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.util.ShotConfigRow


@Composable
fun ShotConfigGrid(navController: NavController,viewModel: ShotConfigViewModel = hiltViewModel()) {
    val  isShowConfigDetail by remember {
        mutableStateOf(viewModel.isShowShotConfigDetail)
    }

    LaunchedEffect(true) {
        viewModel.loadShotConfigs()
    }


    Column(modifier = Modifier.fillMaxSize()) {
        // 添加和删除按钮
        Row(modifier = Modifier.fillMaxWidth()
            .padding(8.dp)  // 可选：添加外部间距
            .border(
                width = 2.dp,        // 边框宽度
                color = Color.Black, // 边框颜色
                shape = RoundedCornerShape(4.dp) // 可选：设置圆角
            ),
            verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                viewModel.selectConfigID.value=-1 // 取消原来选择的row；
              //  viewModel.showShotConfigDetailScreen(true)
                navController?.navigate(
                    ScreenList.ShotConfigDetailScreen.withArgs((-1L).toString(),false.toString())
                )

            }) {
                Text("添加")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.deleteSelectedRows() }) {
                Text("删除")
            }
        }

        // 显示配置行
        LazyColumn {
            itemsIndexed(viewModel.rows) { index, shotConfigRow ->
                if (index < viewModel.rows.size) {
                  //  val baseViewModel = viewModel.getRowViewModel(shotConfigRow.shotConfig.configUI_id!!)
                //    baseViewModel.bind(shotConfigRow.shotConfig)
                    ShotConfigRowItem(
                    //    viewModel = baseViewModel,
                        navController= navController,
                        row = viewModel.rows[index],
                        onApply = { viewModel.applyConfig(index) },
                        onSelect = { isSelected ->
                            viewModel.updateRowSelection(
                                index,
                                isSelected
                            )
                        },
                        isShowShotConfigDetail = viewModel.isShowShotConfigDetail,
                        sel_ConfigId = viewModel.selectConfigID
                    )
                }
            }
        }
    }
}
@Composable
fun ShotConfigRowItem(
   // viewModel: ShotConfigBaseViewModel,
    navController: NavController,
    row: ShotConfigRow,
    sel_ConfigId : MutableState<Long>,
    isShowShotConfigDetail: MutableState<Boolean>,
    onApply: () -> Unit,
    onSelect: (Boolean) -> Unit,
   // onSave: (ShotConfig) -> Unit // 添加保存回调
) {
    var showEditDialog by remember { mutableStateOf(false) }
    Row(modifier = Modifier.fillMaxWidth().padding(2.dp)  // 可选：添加外部间距
        .border(
            width = 2.dp,        // 边框宽度
            color = Color.Black, // 边框颜色
            shape = RoundedCornerShape(4.dp) // 可选：设置圆角
        )) {
        // 星号
       // item {
        Spacer(modifier = Modifier.weight(1f))

        Text(
                text = if (row.isDefault) "★" else "☆",
                color = if (row.isDefault) Color.Red else Color.Black
            )
       // }
        // 配置标题
       // item {
        Spacer(modifier = Modifier.weight(1f))

        Text(text = row.title)
        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { isShowShotConfigDetail.value = !isShowShotConfigDetail.value /* 修改配置逻辑 */
            sel_ConfigId.value = row.shotConfig.configUI_id!!
            navController?.navigate(
                ScreenList.ShotConfigDetailScreen.withArgs(row.shotConfig.configUI_id.toString(),true.toString())
            )

        }) {
            Text("查看")
        }
       // }
        // 修改按钮
       // item {
        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {

            isShowShotConfigDetail.value = !isShowShotConfigDetail.value /* 修改配置逻辑 */
          //  var id =
            sel_ConfigId.value = row.shotConfig.configUI_id!!

            navController?.navigate(
                ScreenList.ShotConfigDetailScreen.withArgs(sel_ConfigId.value.toString(),false.toString())
            )
        }) {
                Text("修改")
            }
      //  }
        // 下发配置按钮
      //  item {
        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onApply) {
                Text("下发")
            }
       // }
        // 复选框
      //  item {
        Spacer(modifier = Modifier.weight(1f))

        Checkbox(
                checked = row.isSelected,
                onCheckedChange = onSelect
            )
     //   }
    }

}