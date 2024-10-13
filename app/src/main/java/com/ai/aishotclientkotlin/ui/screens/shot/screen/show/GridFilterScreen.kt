package com.ai.aishotclientkotlin.ui.screens.shot.screen.show

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterableExcelWithAdvancedFilters(gridFilterViewModel: GridFilterViewModel = hiltViewModel()) {
    val columnNames by gridFilterViewModel.columns
    val columns = columnNames.size
    val originalData by gridFilterViewModel.results
    val isLoading by gridFilterViewModel.isLoading

    val filterStates = remember {
        mutableStateListOf(*Array(columns) { FilterState() })
    }

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

        // Filtered data based on filter states
        val filteredData = originalData.filter { row ->
            row.indices.all { index ->

                val filter = filterStates.getOrNull(index) // 防止越界
                filter?.let {
                    when (filter.filterType) {
                        FilterType.Equals -> row[index] == filter.value.text
                        FilterType.NotEquals -> row[index] != filter.value.text
                        FilterType.Range -> {
                            val rangeValues = filter.rangeValues
                            if (rangeValues != null) {
                                row[index].toIntOrNull()?.let {
                                    it >= rangeValues.first && it <= rangeValues.second
                                } ?: true
                            } else true
                        }

                        else -> true
                    }
                }?: true
            }
        }

        // Main content layout
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetContent = {
                FilterSheetContent(
                    columnNames = columnNames,
                    selectedColumnIndex = selectedColumnIndex,
                    filterState = filterStates.getOrNull(selectedColumnIndex) ?: FilterState(),
                    onConfirm = {
                        scope.launch {
                            if (sheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                                sheetState.bottomSheetState.expand()
                            } else {
                                sheetState.bottomSheetState.partialExpand()
                            }
                        }
                    },
                    onReset = {
                        filterStates.forEach { it.reset() }
                        scope.launch { sheetState.bottomSheetState.hide() }
                    },
                    onColumnSelected = ::onColumnSelected
                )
            },
            sheetPeekHeight = 0.dp // 初始 peek 高度
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Button to open the filter sheet
                Button(
                    onClick = {
                        scope.launch {

                            if (sheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                                sheetState.bottomSheetState.expand()
                            } else {
                                sheetState.bottomSheetState.partialExpand()
                            }


                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    Text("Filter Options")
                }
                // Show data table
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(filteredData.flatten()) { cell ->
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

@Composable
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
}
