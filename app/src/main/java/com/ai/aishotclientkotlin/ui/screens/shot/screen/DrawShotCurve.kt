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
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import kotlin.math.max
import kotlin.math.min


///TODO :1\ 刻度显示有问题；
/// TODO:
@Composable
fun getScreenWidthInPx(): Int {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = context.resources.displayMetrics.density
    return (configuration.screenWidthDp * density).toInt()
}
@Composable
fun getScreenHeightInPx(): Int {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = context.resources.displayMetrics.density
    return (configuration.screenHeightDp * density).toInt()
}


@Composable
fun PlotTrajectory(viewModel: ShotViewModel) {
    var scale by remember { mutableStateOf(1f) } // 缩放因子
    var offsetX by remember { mutableStateOf(0f) } // 平移偏移 X
    var offsetY by remember { mutableStateOf(0f) } // 平移偏移 Y
    val screenWidth = getScreenWidthInPx()
    val screenHeigth =getScreenHeightInPx()
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // 缩放操作
                    if (zoom != 1f && scale in 0.1f..100f) {
                        Log.e("Scale", "scale is : ${scale},zoom is : ${zoom}")
                        scale *= zoom
                    } else
                    // 平移操作
                    {
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
            }
    ) {
        var width = size.width
        var height = size.height
        val tagetPos = viewModel.objectPosition
        val worldWidth = tagetPos.first
        val worldHeight = tagetPos.second

        scale = if(tagetPos.first != 0f && tagetPos.second!=0f)
            min( screenWidth/worldWidth ,screenHeigth/worldHeight)
        else
            min(screenWidth/30,screenHeigth/30).toFloat()  //TODO: 默认是30米，30米的空间；

        Log.e("Shot","scale is ${scale}")
        Log.e("Shot","offsetX is ${offsetX}")
        Log.e("Shot","offsetY is ${offsetY}")
        // 世界坐标转屏幕坐标函数
        fun worldToScreen(worldX: Float, worldY: Float): Offset {
            val screenX = (worldX * scale) + offsetX //+ width / 2f
            val screenY = height - ((worldY * scale) + offsetY )  // 翻转 Y 轴
            return Offset(screenX, screenY)
        }

        // 屏幕坐标转世界坐标函数
        fun screenToWorld(screenX: Float, screenY: Float): Offset {
            val worldX = (screenX - offsetX ) / scale
            val worldY = (height - screenY- offsetY) / scale
            return Offset(worldX, worldY)
        }

        // 绘制坐标系（基于世界坐标）
        drawCoordinateSystem(
            scale = scale,
            worldWidth,
            worldHeight,
            offsetX = offsetX,
            offsetY = offsetY,
            size = size,
            worldToScreen = ::worldToScreen,
            screenToWorld = ::screenToWorld
        )

        // 绘制曲线（基于世界坐标）
        drawCurve(
            positions = viewModel.positions,
            objectPosition = viewModel.objectPosition,
            worldToScreen = ::worldToScreen,
            scale = scale,
            objectRaduis = viewModel.radius,
            offsetX = offsetX,
            offsetY = offsetY
        )
    }
}

fun DrawScope.drawCoordinateSystem(
    scale: Float,
    worldWidth : Float,
    worldHeight:Float,
    offsetX: Float,
    offsetY: Float,
    size: Size,
    worldToScreen: (Float, Float) -> Offset,
    screenToWorld: (Float, Float) -> Offset
) {
    val width = size.width
    val height = size.height

    // 动态计算刻度间隔，确保标签不会过于密集
    val baseStep = 5f // 基础步长，以5米为单位；
    val step = baseStep * scale
    val minStepPixels = 50f // 最小像素间隔
    val adjustedStep = if (step < minStepPixels) minStepPixels / scale else baseStep


    var (xendscreen,yendscreen) = worldToScreen(worldWidth,worldHeight)
    // 绘制 X 轴
    drawLine(
        color = Color.Black,
        start = Offset(0f,  yendscreen),
        end = Offset(width,  yendscreen),
        strokeWidth = 2f
    )

    // 绘制 Y 轴
    drawLine(
        color = Color.Black,
        start = Offset(xendscreen, 0f),
        end = Offset(xendscreen, height),
        strokeWidth = 2f
    )

    // 绘制 X 轴刻度和标签
    val startWorldX = screenToWorld(0f, 0f).x
    val endWorldX = screenToWorld(width, 0f).x

    val startX = (startWorldX / adjustedStep).toInt() * adjustedStep
    val endX = (endWorldX / adjustedStep).toInt() * adjustedStep
    var x = startX
    while (x <= endX) {
  //  for (x in startX..endX step adjustedStep.toInt()) {
       // val worldX = x.toFloat()
       // val (worldX, _) = screenToWorld(x, height / 2f + offsetY)
        val screenPos = worldToScreen(x, 0f)
        // 绘制刻度线
        drawLine(
            color = Color.Black,
            start = Offset(screenPos.x, yendscreen+ offsetY - 5f),
            end = Offset(screenPos.x,  yendscreen+ offsetY + 5f),
            strokeWidth = 2f
        )
        // 绘制标签
        if (adjustedStep >= 50f / scale) { // 控制标签显示密度
            drawText(
                text = String.format("%.1f", x),
                x = screenPos.x,
                y = yendscreen +  offsetY + 20f,
                paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 15f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
        x += adjustedStep

    }

    // 绘制 Y 轴刻度和标签
    val startWorldY = screenToWorld(0f, height).y
    val endWorldY = screenToWorld(0f, 0f).y

    val startY = (startWorldY / adjustedStep).toInt() * adjustedStep
    val endY = (endWorldY / adjustedStep).toInt() * adjustedStep
    var y = startY
    while (y <= endY) {
       // val worldY = y.toFloat()
     //   val (worldY, _) = screenToWorld(y, height / 2f + offsetX)
        val screenPos = worldToScreen(0f, y)
        // 绘制刻度线
        drawLine(
            color = Color.Black,
            start = Offset( offsetX - 5f, screenPos.y),
            end = Offset(offsetX + 5f, screenPos.y),
            strokeWidth = 2f
        )
        // 绘制标签
        if (adjustedStep >= 50f / scale) { // 控制标签显示密度
            drawText(
                text = String.format("%.1f", y),
                x = offsetX + 20f,
                y = screenPos.y + 10f,
                paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 15f
                    textAlign = android.graphics.Paint.Align.LEFT
                }
            )
        }

        y += adjustedStep
    }
}

fun DrawScope.drawText(
    text: String,
    x: Float,
    y: Float,
    paint: android.graphics.Paint
) {
    drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
}

fun DrawScope.drawCurve(
    positions: List<Position>,
    objectPosition: Pair<Float, Float>,
    worldToScreen: (Float, Float) -> Offset,
    scale: Float,
    objectRaduis: Float,
    offsetX: Float,
    offsetY: Float
) {
    if (positions.isEmpty()) return

    val path = Path()

    // 开始绘制路径
    val firstPoint = positions.first()
    val firstScreenPoint = worldToScreen(firstPoint.x, firstPoint.y)
    path.moveTo(firstScreenPoint.x, firstScreenPoint.y)

    // 使用二次贝塞尔曲线连接点
    for (i in 1 until positions.size - 1 step 2) {
        val controlPoint = positions[i]
        val endPoint = positions[i + 1]

        val controlScreen = worldToScreen(controlPoint.x,controlPoint.y)
        val endScreen = worldToScreen(endPoint.x,endPoint.y)

        path.quadraticBezierTo(controlScreen.x, controlScreen.y, endScreen.x, endScreen.y)
    }
    var objectR = objectRaduis * scale/(2.0f*size.minDimension)
    if(objectR < 2f)
        objectR = 2f
    // 绘制路径
    drawPath(
        path = path,
        color = Color.Blue,
        style = Stroke(width = objectR)
    )

    // 绘制对象位置（红色圆圈）
    val (objX, objY) = objectPosition

    val objectScreenPos = worldToScreen(objX, objY)

    Log.e("TAG", "drawCurve: ${objectRaduis * scale/(2.0f*size.minDimension)}")
    drawCircle(
        color = Color.Red,
        radius = objectR*3,//TODO 3 is default
        center = objectScreenPos
    )
}

@Composable
fun CurveImage(
    positions: List<Position>,
    objectPosition: Pair<Float, Float>,
    scale: Float,
    curveOffsetX: Float,
    curveOffsetY: Float
) {
    // 调用函数获取绘制曲线的 Bitmap
    val bitmap = drawCurveOnBitmap(
        width = 800,
        height = 800,
        positions = positions,
        objectPosition = objectPosition,
        scale = scale,
        curveOffsetX = curveOffsetX,
        curveOffsetY = curveOffsetY
    )

    // 将 Bitmap 转换为 ImageBitmap 并显示
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Curve Image",
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun CurveImage2(positions: List<Position>,
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




