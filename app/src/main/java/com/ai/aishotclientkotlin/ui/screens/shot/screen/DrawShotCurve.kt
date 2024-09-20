package com.ai.aishotclientkotlin.ui.screens.shot.screen
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.sin

@Composable
fun CoordinateSystemWithZoomableCurve() {
    var scale by remember { mutableFloatStateOf(50f) } // 初始缩放因子
    var offsetX by remember { mutableFloatStateOf(3f) } // X轴偏移 (加3个像素)
    var offsetY by remember { mutableFloatStateOf(3f) } // Y轴偏移 (加3个像素)

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                // 仅在缩放手势时修改缩放因子
                if (zoom != 1f) {
                    scale *= zoom // 根据缩放手势调整缩放因子
                }
                // 仅在平移手势时修改偏移
                else {
                    offsetX += pan.x // 根据平移手势调整 X 轴偏移
                    offsetY += pan.y // 根据平移手势调整 Y 轴偏移
                }
            }
        }
    ) {
        // 计算左下角的初始Y偏移值（加上高度）
        val height = size.height
        drawCoordinateSystem(offsetX, offsetY + height, scale) // Y 轴原点从左下角开始
        drawCurve(scale, offsetX, offsetY + height)
    }
}

fun DrawScope.drawCoordinateSystem(offsetX: Float, offsetY: Float, scale: Float) {
    val width = size.width
    val height = size.height

    // 根据缩放比例动态调整刻度显示间隔
    val step = (50 * scale).coerceAtLeast(50f) // 最小间隔50，缩放时保持间隔动态变化
    val labelThreshold = 30f // 仅当间隔大于某个值时显示标签

    // X 轴 (向右偏移3像素)
    drawLine(
        color = Color.Black,
        start = androidx.compose.ui.geometry.Offset(0f + offsetX, offsetY),
        end = androidx.compose.ui.geometry.Offset(width + offsetX, offsetY),
        strokeWidth = 2f
    )

    // Y 轴 (向上偏移3像素)
    drawLine(
        color = Color.Black,
        start = androidx.compose.ui.geometry.Offset(offsetX, 0f), // Y轴从屏幕底部开始
        end = androidx.compose.ui.geometry.Offset(offsetX, offsetY),
        strokeWidth = 2f
    )

    // 绘制 X 轴刻度和标定
    for (i in (-width.toInt()..width.toInt() step step.toInt())) {
        drawLine(
            color = Color.Black,
            start = androidx.compose.ui.geometry.Offset(i.toFloat() + offsetX, offsetY - 5),
            end = androidx.compose.ui.geometry.Offset(i.toFloat() + offsetX, offsetY + 5),
            strokeWidth = 2f
        )
        // 当间隔足够大时显示刻度数字
        if (step > labelThreshold) {
            drawText(
                (i / scale).toString(),
                i.toFloat() + offsetX,
                offsetY + 20
            )
        }
    }

    // 绘制 Y 轴刻度和标定
    for (i in (0..height.toInt() step step.toInt())) {
        drawLine(
            color = Color.Black,
            start = androidx.compose.ui.geometry.Offset(offsetX - 5, offsetY - i.toFloat()),
            end = androidx.compose.ui.geometry.Offset(offsetX + 5, offsetY - i.toFloat()),
            strokeWidth = 2f
        )
        // 当间隔足够大时显示刻度数字
        if (step > labelThreshold) {
            drawText(
                (i / scale).toString(),
                offsetX + 20,
                offsetY - i.toFloat()
            )
        }
    }
}

fun DrawScope.drawText(text: String, x: Float, y: Float) {
    drawContext.canvas.nativeCanvas.apply {
        drawText(
            text,
            x,
            y,
            android.graphics.Paint().apply {
                textSize = 30f
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }
}

fun DrawScope.drawCurve(scale: Float, offsetX: Float, offsetY: Float) {
    val width = size.width

    // 绘制曲线
    val path = androidx.compose.ui.graphics.Path()
    path.moveTo(0f + offsetX, offsetY)

    for (x in 0..width.toInt()) {
        val y = (offsetY - scale * sin((x + offsetX) / scale)).toFloat() // Y 轴方向反转
        path.lineTo(x.toFloat() + offsetX, y)
    }

    drawPath(
        path = path,
        color = Color.Blue,
        style = Stroke(width = 2f)
    )
}

@Preview
@Composable
fun PreviewCoordinateSystemWithZoomableCurve() {
    CoordinateSystemWithZoomableCurve()
}

