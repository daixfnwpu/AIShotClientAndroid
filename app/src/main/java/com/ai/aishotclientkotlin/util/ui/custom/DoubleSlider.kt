package com.ai.aishotclientkotlin.util.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RangeSlider(
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float>,
    initialValueLeft: Float = range.start,
    initialValueRight: Float = range.endInclusive,
    onValueChange: (Float, Float) -> Unit
) {
    // 定义两个滑块的状态
    var sliderPositions by remember { mutableStateOf(initialValueLeft..initialValueRight) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Range: ${sliderPositions.start} - ${sliderPositions.endInclusive}")

        // 使用 RangeSlider
        androidx.compose.material3.RangeSlider(
            value = sliderPositions,
            onValueChange = { newPositions ->
                sliderPositions = newPositions
                onValueChange(newPositions.start, newPositions.endInclusive)
            },
            valueRange = range,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}



@Composable
fun RangeSliderExample() {
    var leftValue by remember { mutableStateOf(0f) }
    var rightValue by remember { mutableStateOf(100f) }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text(text = "Left: $leftValue, Right: $rightValue", modifier = Modifier.padding(16.dp))

        RangeSlider(
            range = 0f..100f,
            initialValueLeft = leftValue,
            initialValueRight = rightValue
        ) { left, right ->
            leftValue   = left
            rightValue  = right
        }
    }
}
