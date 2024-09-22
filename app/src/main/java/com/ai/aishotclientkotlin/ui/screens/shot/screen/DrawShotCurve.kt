package com.ai.aishotclientkotlin.ui.screens.shot.screen
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.ai.aishotclientkotlin.engine.ShotCauseState
import com.ai.aishotclientkotlin.engine.calculateTrajectory
import com.ai.aishotclientkotlin.engine.findPosByShotDistance
import kotlin.math.sin


///TODO : 刻度显示有问题；
/// TODO: drawpath造成了重新计算一样？
@Composable
fun PlotTrajectory( radius: Float, velocity: Float, angle: Float, destiny: Float,shotDistance: Float   ) {
    var scale by remember { mutableStateOf(50f) } // 初始缩放因子
    var curveOffsetX by remember { mutableStateOf(0f) } // 曲线的 X 轴偏移
    var curveOffsetY by remember { mutableStateOf(0f) } // 曲线的 Y 轴偏移

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                // 仅在缩放手势时修改缩放因子，不平移
                if (zoom != 1f) {
                    scale *= zoom
                }
                // 仅在没有缩放时（zoom == 1f）处理平移手势
                else {
                    curveOffsetX += pan.x
                    curveOffsetY += pan.y
                }
            }
        }
    ) {
        val width = size.width
        val height = size.height

        // 坐标系的原点固定在左下角 (屏幕左下角)
        val originX = 3f // X 轴向右偏移 3 像素
        val originY = height - 3f // Y 轴向上偏移 3 像素

        // 绘制坐标系（X 和 Y 轴）和动态刻度
        drawCoordinateSystem(originX, originY, scale, curveOffsetX, curveOffsetY)

        // 绘制曲线 (相对坐标系的曲线缩放和移动)
        drawCurve(scale, radius, velocity, angle, destiny,shotDistance, curveOffsetX, curveOffsetY)
    }
}

fun DrawScope.drawCoordinateSystem(
    originX: Float,
    originY: Float,
    scale: Float,
    curveOffsetX: Float,
    curveOffsetY: Float
) {
    val width = size.width
    val height = size.height

    // 根据缩放比例动态调整刻度显示间隔
    val step = (50 * scale).coerceAtLeast(50f) // 最小间隔50，缩放时保持间隔动态变化
    val labelThreshold = 30f // 仅当间隔大于某个值时显示标签

    // 绘制 X 轴：原点在屏幕的左下角（偏移3像素）
    drawLine(
        color = Color.Black,
        start = androidx.compose.ui.geometry.Offset(3f, originY), // 原点从 (3, height - 3)
        end = androidx.compose.ui.geometry.Offset(width, originY), // 终点为 (width, height - 3)
        strokeWidth = 2f
    )

    // 绘制 Y 轴：原点在屏幕的左下角
    drawLine(
        color = Color.Black,
        start = androidx.compose.ui.geometry.Offset(originX, 0f),
        end = androidx.compose.ui.geometry.Offset(originX, height),
        strokeWidth = 2f
    )

    // 绘制 X 轴刻度和标定
    for (i in (-width.toInt()..width.toInt() step step.toInt())) {
        val adjustedX = i + curveOffsetX // 基于曲线的偏移量调整刻度
        drawLine(
            color = Color.Black,
            start = androidx.compose.ui.geometry.Offset(adjustedX.toFloat() + originX, originY - 5),
            end = androidx.compose.ui.geometry.Offset(adjustedX.toFloat() + originX, originY + 5),
            strokeWidth = 2f
        )
        // 当间隔足够大时显示刻度数字
        if (step > labelThreshold) {
            drawText(
                String.format("%.1f", i / scale), // 格式化为一位小数
                adjustedX.toFloat() + originX, // X 轴位置不变
                originY + 20 - 4 // 向上偏移 4 个像素
            )
        }
    }

    // 绘制 Y 轴刻度和标定
    for (i in (-height.toInt()..height.toInt() step step.toInt())) {
        val adjustedY = i - curveOffsetY // 基于曲线的偏移量调整刻度
        drawLine(
            color = Color.Black,
            start = androidx.compose.ui.geometry.Offset(originX - 5, originY - adjustedY.toFloat()),
            end = androidx.compose.ui.geometry.Offset(originX + 5, originY - adjustedY.toFloat()),
            strokeWidth = 2f
        )
        // 当间隔足够大时显示刻度数字
        if (step > labelThreshold) {
            drawText(
                String.format("%.1f", i / scale), // 格式化为一位小数
                originX + 20 + 4, // 向右偏移 4 个像素
                originY - adjustedY.toFloat() // Y 轴位置不变
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


fun DrawScope.drawCurve(
    scale: Float,
    radius: Float,
    velocity: Float,
    angle: Float,
    destiny: Float,
    shotDistance: Float,
    curveOffsetX: Float,
    curveOffsetY: Float
) {
    val width = size.width
    val height = size.height

    val shotCauseState: ShotCauseState = ShotCauseState()
    val positions = calculateTrajectory(radius, velocity, angle, destiny, shotCauseState)
    val objectPosition = findPosByShotDistance(angle, positions, shotDistance)

    // Grid drawing with consistent scaling and offset
    for (i in 0..width.toInt() step (20 * scale).toInt()) {
        drawLine(
            color = Color.LightGray,
            start = Offset(i.toFloat() + curveOffsetX, 0f),
            end = Offset(i.toFloat() + curveOffsetX, height)
        )
    }
    for (i in 0..height.toInt() step (20 * scale).toInt()) {
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, i.toFloat() + curveOffsetY),
            end = Offset(width, i.toFloat() + curveOffsetY)
        )
    }

    // Scaling and adjusting the trajectory curve
    val steps = 100
    val step = positions.size / steps
    val pixelsPerUnit: Float = (width / step) * scale

    val path = Path()
    if (positions.size >= 3) {
        // Start the path at the first point
        path.moveTo(
            positions[0].x * pixelsPerUnit + curveOffsetX,
            height - (positions[0].y * pixelsPerUnit + curveOffsetY)
        )

        // Draw the quadratic Bézier curve connecting the trajectory points
        for (i in 0 until positions.size - 2 step 2) {
            val start = positions[i]
            val control = positions[i + 1]
            val end = positions[i + 2]

            path.quadraticBezierTo(
                control.x * pixelsPerUnit + curveOffsetX,
                height - (control.y * pixelsPerUnit + curveOffsetY),
                end.x * pixelsPerUnit + curveOffsetX,
                height - (end.y * pixelsPerUnit + curveOffsetY)
            )
        }

        // Draw the curve
        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 3f)
        )
    }

    // Draw the object position (red circle)
    val objX = objectPosition.first * pixelsPerUnit + curveOffsetX
    val objY = height - (objectPosition.second * pixelsPerUnit + curveOffsetY)
    drawCircle(Color.Red, radius = 10f * scale, center = Offset(objX, objY))
}








