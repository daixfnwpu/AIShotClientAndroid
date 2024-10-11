package com.ai.aishotclientkotlin.ui.screens.shot.screen.show

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.model.show.GridFilterViewModel
import androidx.compose.material3.Text



@Composable
fun FilterableExcelWithAdvancedFilters(gridFilterViewModel: GridFilterViewModel = hiltViewModel()
                                       ) {

    //val  columnNames =  gridFilterViewModel.columns

    val columnNames  by gridFilterViewModel.columns

    val  columns  = columnNames.size

    val  originalData by  gridFilterViewModel.results

    val isLoading by gridFilterViewModel.isLoading

    if (isLoading) {
        // 如果正在加载，显示加载动画或占位符
        CircularProgressIndicator()
    } else {


        // Remember the filter state for each column (filter type and input value)
        val filterStates = remember {
            mutableStateListOf(*Array(columns) { FilterState() })
        }

        // Apply filters to the data
        val filteredData = originalData.filter { row ->
            row.indices.all { index ->
                val filter = filterStates[index]
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
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // First row: column names as the header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (name in columnNames) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Create filter controls for each column
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0 until columns) {
                    Column(modifier = Modifier.weight(1f)) {
                        FilterDropdown(filterState = filterStates[i])
                        when (filterStates[i].filterType) {
                            FilterType.Equals, FilterType.NotEquals -> FilterInputField(
                                value = filterStates[i].value,
                                onValueChange = { filterStates[i].value = it }
                            )

                            FilterType.Range -> RangeInputFields(
                                value = filterStates[i].rangeValues,
                                onValueChange = { range -> filterStates[i].rangeValues = range }
                            )

                            else -> { /* No input */
                            }
                        }
                    }
                }
            }

            // Show the filtered table
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
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


// The rest of the code remains the same (FilterState, FilterType, FilterDropdown, etc.)


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
                onValueChange(Pair(start.toIntOrNull() ?: 0, newValue.toIntOrNull() ?: Int.MAX_VALUE))
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
