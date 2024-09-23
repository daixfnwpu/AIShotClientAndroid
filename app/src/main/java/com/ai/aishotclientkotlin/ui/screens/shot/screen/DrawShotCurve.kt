package com.ai.aishotclientkotlin.ui.screens.shot.screen
import android.graphics.Bitmap
import android.graphics.Paint
import android.util.Log
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.ai.aishotclientkotlin.engine.Position
import com.ai.aishotclientkotlin.engine.ShotCauseState
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotViewModel
import android.graphics.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image



///TODO :1\ 刻度显示有问题；
/// TODO:
@Composable
fun PlotTrajectory(viewModel: ShotViewModel  ) {
    var scale by remember { mutableStateOf(1f) } // 初始缩放因子
    var curveOffsetX by remember { mutableStateOf(0f) } // 曲线的 X 轴偏移
    var curveOffsetY by remember { mutableStateOf(0f) } // 曲线的 Y 轴偏移
    Log.e("CALL","PLotTrajector is called")
  //  CurveImage(viewModel.positions,viewModel.objectPosition,scale, curveOffsetX, curveOffsetY)
    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { centroid, pan, zoom, _ ->
                // 仅在缩放手势时修改缩放因子，不平移
                if (zoom != 1f&& scale < 100 && scale > 0.1) {
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

        Log.e("Shot:"," objectPosition is : ${viewModel.objectPosition.toString()}")
        Log.e("Shot:","curveOffsetX is: ${curveOffsetX.toString()}")

        Log.e("Shot:",curveOffsetY.toString())
        ///"app\\build\\outputs\\
      //  val inputPath = "app\\build\\outputs\\input.jpg"
        val outputPath = "app\\build\\outputs\\output.jpg"
        drawCurve(viewModel.positions,viewModel.objectPosition,scale, curveOffsetX, curveOffsetY)
    //    drawCurveOnImage(outputPath,viewModel.positions,viewModel.objectPosition,scale, curveOffsetX, curveOffsetY)

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
    positions: List<Position>,
    objectPosition : Pair<Float,Float>,
    scale: Float,
    curveOffsetX: Float,
    curveOffsetY: Float
) {
    if (positions.isEmpty())
        return
    val width = size.width
    val height = size.height

    val shotCauseState: ShotCauseState = ShotCauseState()

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


    var pixelsPerUnit: Float = (width / step) * scale
    if (step == 0)
        pixelsPerUnit = 1.0f

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

    Log.e("Shot",curveOffsetX.toString())
    Log.e("Shot",pixelsPerUnit.toString())
    Log.e("Shot",objectPosition.toString())
    Log.e("Shot",objY.toString())
    if(objY != null && objX != null)
        drawCircle(Color.Red, radius = 10f * scale, center = Offset(objX, objY))
}


@Composable
fun CurveImage(positions: List<Position>,
               objectPosition : Pair<Float,Float>,
               scale: Float,
               curveOffsetX: Float,
               curveOffsetY: Float) {
    // 调用函数获取绘制曲线的 Bitmap
    val bitmap = drawCurveOnBitmap(800, 800,positions,objectPosition,scale,curveOffsetX,curveOffsetY)

    // 将 Bitmap 转换为 ImageBitmap 并显示
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Curve Image",
        modifier = Modifier.fillMaxSize()
    )
}


// 绘制曲线并返回 Bitmap
@Composable
fun drawCurveOnBitmap(width: Int, height: Int,
                      positions: List<Position>,
                      objectPosition : Pair<Float,Float>,
                      scale: Float,
                      curveOffsetX: Float,
                      curveOffsetY: Float): Bitmap {
    // 创建空白 Bitmap
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // 创建 Canvas 绑定到 Bitmap
    val canvas = Canvas(bitmap)

    // 用白色填充背景
    canvas.drawColor(android.graphics.Color.BLACK)

    val positionNum = positions.size
    val pixelsPerUnit = 20//positionNum / width
    val path = android.graphics.Path().apply {
        moveTo(100f, 100f) // 起点坐标

    }
    if (positions.size >= 3) {
        // Start the path at the first point
        Log.e("PAINT","the positions size is : ${positions.size}")
        for(i in positions.indices)
             Log.e("PAINT","the positions is : ${positions[i].toString()}")
        path.moveTo(
            positions[0].x * pixelsPerUnit + curveOffsetX,
            canvas.height - (positions[0].y * pixelsPerUnit + curveOffsetY)
        )

        // Draw the quadratic Bézier curve connecting the trajectory points
        for (i in 0 until positions.size - 2 step 2) {
            val start = positions[i]
            val control = positions[i + 1]
            val end = positions[i + 2]

            path.quadTo(
                control.x * pixelsPerUnit + curveOffsetX,
                canvas.height - (control.y * pixelsPerUnit + curveOffsetY),
                end.x * pixelsPerUnit + curveOffsetX,
                canvas.height - (end.y * pixelsPerUnit + curveOffsetY)
            )
        }
        // 创建 Paint 设置曲线的绘制属性
        val paint = Paint().apply {
            color = android.graphics.Color.RED // 曲线颜色
            strokeWidth = 5f  // 曲线宽度
            style = Paint.Style.STROKE // 只绘制线条
            isAntiAlias = true
        }
        // Draw the curve
        canvas.drawPath(path, paint)
    }

    return bitmap
}




