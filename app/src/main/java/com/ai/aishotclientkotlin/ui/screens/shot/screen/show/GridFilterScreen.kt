package com.ai.aishotclientkotlin.ui.screens.shot.screen.show

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.ai.aishotclientkotlin.util.ui.custom.AppBarWithArrow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterableExcelWithAdvancedFilters(gridFilterViewModel: GridFilterViewModel = hiltViewModel()
,pressOnBack: ()  -> Unit ,modifier: Modifier= Modifier) {
    val columnNames by gridFilterViewModel.columns
    val columns = columnNames.size
    val originalData by gridFilterViewModel.results
    val isLoading by gridFilterViewModel.isLoading
    val distance_index = 6;
  //  val selectedColumns = remember { mutableStateListOf(*Array(columnNames.size) { true }) }

    // 创建一个空的 selectedColumns，稍后根据 columnNames 初始化
    val selectedColumns = remember { mutableStateListOf<Boolean>() }

    // 使用 LaunchedEffect 监听 columnNames 的变化
    LaunchedEffect(columnNames) {
        if (columnNames.isNotEmpty() && selectedColumns.isEmpty()) {
            // 当 columnNames 加载完成且 selectedColumns 还没有初始化时，进行初始化
            selectedColumns.addAll(List(columnNames.size) { true })
        }
    }


    var rangeStart = 0.0f;
    var rengeEnd = 200.0f;
    var rightValue by remember { mutableStateOf(rengeEnd) }
    var leftValue by remember { mutableStateOf(rangeStart) }


    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded, // 修改初始值为 Collapsed
            skipHiddenState = false // 跳过 Hidden 状态
        )
    )


    // State for the bottom sheet
    //  val sheetState = rememberBottomSheetScaffoldState(bottomSheetState = SheetValue.Hidden)
    val scope = rememberCoroutineScope()

    // Selected column index for filtering
    var selectedColumnIndex by remember { mutableStateOf(0) }

    // 当用户选择某个列时，确保 `selectedColumnIndex` 在范围内
    val safeSelectedColumnIndex = if (columnNames.isNotEmpty()) {
        selectedColumnIndex.coerceIn(0, columnNames.size - 1)
    } else {
        0 // 如果没有列名，默认值为 0
    }

    fun onColumnSelected(index: Int) {
        if (index in 0 until columnNames.size) {
            selectedColumnIndex = index
        }
    }


    if (isLoading) {
        CircularProgressIndicator()


    } else {
        Log.e("originalData","originalData 's length is ${originalData.size}")

        val filteredData = originalData.filter { row ->
            row[distance_index].toFloatOrNull()?.let {
                it in leftValue..rightValue
            }?:true
        }
        Log.e("filteredData","filteredData 's length is ${filteredData.size}")

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
                    if (columnNames.isNotEmpty()) {
                        Log.e("UI","columnNames is : ${columnNames},columns size  is ${columns}")
                        ColumnSelection(
                            columnNames = columnNames,
                            selectedColumns = selectedColumns,
                        ) { index, isChecked ->
                            // 更新选中状态
                            selectedColumns[index] = isChecked
                        }
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
                    columns = GridCells.Fixed(columnNames.size), // 根据列数固定
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(columnNames) { header ->
                        HeaderItem(text = header)
                    }
                }
                // Show data table
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {

                    val selectedData = filteredData.map { row ->
                        row.filterIndexed { index, _ -> selectedColumns[index] }
                    }
                    items(selectedData.flatten()) { cell ->
                        CellItem(text = cell)
                    }
                }
            }
        }
    }
}





// Bottom sheet content
@Composable
fun FilterSheetContent(
    columnNames: List<String>,
    selectedColumnIndex: Int,
    onColumnSelected: (Int) -> Unit,
    filterState: FilterState,
    onConfirm: () -> Unit,
    onReset: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // ComboBox to select which column to filter
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth())
            {
                Text(text = "Select Column")
                Button(onClick = { expanded = true }) {
                    Text(text = columnNames[selectedColumnIndex])
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    columnNames.forEachIndexed { index, name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                onColumnSelected(index)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter condition based on selected column
        FilterDropdown(filterState = filterState)

        when (filterState.filterType) {
            FilterType.Equals, FilterType.NotEquals -> FilterInputField(
                value = filterState.value,
                onValueChange = { filterState.value = it }
            )

            FilterType.Range -> RangeInputFields(
                value = filterState.rangeValues,
                onValueChange = { range -> filterState.rangeValues = range }
            )

            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm and Reset buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onReset) {
                Text("Reset")
            }
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        }
    }
}

fun FilterState.reset() {
    filterType = FilterType.None
    value = TextFieldValue("")
    rangeValues = null
}


// State to hold the filter information for a column
data class FilterState(
    var filterType: FilterType = FilterType.None,
    var value: TextFieldValue = TextFieldValue(""),
    var rangeValues: Pair<Int, Int>? = null
)

// Enum to represent the type of filter
enum class FilterType {
    None, Equals, NotEquals, Range
}

@Composable
fun FilterDropdown(filterState: FilterState) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth())
        {
            Button(onClick = { expanded = true }) {
                Text(text = filterState.filterType.name)
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                FilterType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            filterState.filterType = type
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterInputField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Input") },
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    )
}

@Composable
fun RangeInputFields(value: Pair<Int, Int>?, onValueChange: (Pair<Int, Int>) -> Unit) {
    var start by remember { mutableStateOf(value?.first?.toString() ?: "") }
    var end by remember { mutableStateOf(value?.second?.toString() ?: "") }

    Row(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = start,
            onValueChange = { newValue ->
                start = newValue
                onValueChange(Pair(newValue.toIntOrNull() ?: 0, end.toIntOrNull() ?: Int.MAX_VALUE))
            },
            label = { Text("Start") },
            modifier = Modifier
                .padding(4.dp)
                .weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            value = end,
            onValueChange = { newValue ->
                end = newValue
                onValueChange(
                    Pair(
                        start.toIntOrNull() ?: 0,
                        newValue.toIntOrNull() ?: Int.MAX_VALUE
                    )
                )
            },
            label = { Text("End") },
            modifier = Modifier
                .padding(4.dp)
                .weight(1f)
        )
    }
}

/*@Composable
fun CellItem(text: String) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .height(50.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}*/
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
fun CellItem(text: String) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            //    .aspectRatio(1f)
            .background(Color.LightGray)// 数据单元格的背景
            .border(1.dp, Color.Black),
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
