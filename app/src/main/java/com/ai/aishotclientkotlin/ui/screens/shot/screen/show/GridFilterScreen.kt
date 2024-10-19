package com.ai.aishotclientkotlin.ui.screens.shot.screen.show

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.model.show.GridFilterViewModel
import androidx.compose.material3.Text
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FiberSmartRecord
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.util.ui.custom.AppBarWithArrow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterableExcelWithAdvancedFilters(navController :NavController,gridFilterViewModel: GridFilterViewModel = hiltViewModel()
,pressOnBack: ()  -> Unit ,modifier: Modifier= Modifier) {


    val columnNames by gridFilterViewModel.columns
    val columns = columnNames.size
    val originalData by gridFilterViewModel.results
    val isLoading by gridFilterViewModel.isLoading
    val distance_index = 6;
    // 创建一个空的 selectedColumns，稍后根据 columnNames 初始化
    val selectedColumns = remember { mutableStateListOf<Boolean>() }
    var rightValue by remember { mutableStateOf(25f) }
    var columnCount by remember {
        mutableStateOf(columns)
    }
    // 使用 LaunchedEffect 监听 columnNames 的变化
    LaunchedEffect(columnNames) {
        if (columnNames.isNotEmpty() && selectedColumns.isEmpty()) {
            // 当 columnNames 加载完成且 selectedColumns 还没有初始化时，进行初始化
            rightValue = gridFilterViewModel.distance.value
            selectedColumns.addAll(List(columnNames.size) { true })

        }
    }
    var rangeStart  = 0f;
    var leftValue by remember { mutableStateOf(rangeStart) }


    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded, // 修改初始值为 Collapsed
            skipHiddenState = false // 跳过 Hidden 状态
        )
    )

    val scope = rememberCoroutineScope()

    if (isLoading  ) {
        CircularProgressIndicator()
    } else {
        Log.e("originalData","originalData 's length is ${originalData.size}")

        val filteredData = originalData.filter { row ->
            row[distance_index].toFloatOrNull()?.let {
                it in leftValue..rightValue
            }?:true
        }
        val visibleColumns  = columnNames.filterIndexed { index, _ -> selectedColumns.getOrElse(index) { false } }
        columnCount = visibleColumns.size // 选择的列数
        Log.e("filteredData","filteredData 's length is ${filteredData.size},cloumnCount is ${columnCount}")

        // Main content layout
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetContent = {
                Column(modifier = Modifier.fillMaxWidth()) {

                    com.ai.aishotclientkotlin.util.ui.custom.RangeSlider(
                        range = 0f..100f,
                        initialValueLeft = leftValue,
                        initialValueRight = rightValue
                    ) { left, right ->
                        leftValue = left
                        rightValue = right
                    }
                    if (columnNames.isNotEmpty() && selectedColumns.isNotEmpty()) {
                        Log.e("UI","columnNames is : ${columnNames},columns size  is ${columns}")
                        ColumnSelection(
                            columnNames = columnNames,
                            selectedColumns = selectedColumns,
                        ) { index, isChecked ->
                            // 更新选中状态
                            selectedColumns[index] = isChecked
                        }
                    }
                    IconButton(onClick = {

                        navController?.navigate(
                            ScreenList.AiProtileAnimateScreen.withArgs()
                        )

                    }) {
                        Icon(
                            imageVector = Icons.Filled.FiberSmartRecord,
                            contentDescription = "Localized description"
                        )
                    }


                }

            },
            sheetPeekHeight = 0.dp // 初始 peek 高度
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                AppBarWithArrow("", showMenu = true, pressOnBack, menuClick = {
                    Log.e("EVENT","menuClick is clicked!!!")
                    scope.launch {
                        if (sheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                            sheetState.bottomSheetState.expand()
                        } else {
                            sheetState.bottomSheetState.partialExpand()
                        }
                    }
                })


                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnCount), // 根据列数固定
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(visibleColumns) { header ->
                        HeaderItem(text = header)
                    }
                }
                // Show data table
//                LazyVerticalGrid(
//                    columns = GridCells.Adaptive(minSize = 100.dp),
//                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(8.dp)
//                ) {

                    val selectedData = filteredData.map { row ->
                        row.filterIndexed { index, _ -> selectedColumns[index] }
                    }
                   /* items(selectedData.flatten()) { cell ->
                        CellItem(text = cell)
                    }*/

                    // 显示数据
                    LazyColumn {
                        items(selectedData) { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ){
                                row.forEach { cell ->
                                    CellItem(text = cell, modifier = Modifier.weight(1.0f))
                                }
                            }
                        }
                    }
//}
            }
        }
    }
}



@Composable
fun HeaderItem(text: String) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Gray), // 背景颜色可以区分表头
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun CellItem(text: String,modifier: Modifier) {
    Box(
        modifier = modifier
            .padding(4.dp)
            //    .aspectRatio(1f)
            .background(Color.LightGray)// 数据单元格的背景
            .border(1.dp, Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text,textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnSelection(
    columnNames: List<String>,                 // 列的名字
    selectedColumns: MutableList<Boolean>,     // 用来保存每个列是否被选中的状态
    onColumnSelectionChanged: (Int, Boolean) -> Unit // 回调函数，处理列选择的变化
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 标题
        Text(text = "选择需要显示的列", fontWeight = FontWeight.Bold)
        // 循环显示复选框
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            columnNames.forEachIndexed { index, columnName ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Checkbox(
                        checked = selectedColumns[index], // 复选框的状态
                        onCheckedChange = { isChecked ->
                            // 当复选框被点击时，更新列的选择状态
                            onColumnSelectionChanged(index, isChecked)
                        }
                    )
                    Text(text = columnName) // 显示列名称
                }
            }
        }
    }
}
