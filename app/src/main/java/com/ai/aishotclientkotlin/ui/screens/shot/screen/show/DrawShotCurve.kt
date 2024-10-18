package com.ai.aishotclientkotlin.ui.screens.shot.screen.show
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
import com.ai.aishotclientkotlin.engine.shot.Position
import com.ai.aishotclientkotlin.ui.screens.shot.model.show.ShotViewModel
import androidx.compose.runtime.Composable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ai.aishotclientkotlin.R
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


///TODO :1,移动，改变为只能够向右移动和上下移动，同时，向右不能够超出canvasWidth，canvasWidth需要考虑到缩放。2；移动的时候，坐标轴位置不变，而位置在刻度上体现出来；
/// TODO:

@Composable
fun PlotTrajectory(viewModel: ShotViewModel) {
    var scale by remember { mutableStateOf(1f) } // 缩放因子
    var zoomScale by remember { mutableStateOf(1f) } // 缩放因子
    var offsetX by remember { mutableStateOf(0f) } // 平移偏移 X
    var offsetY by remember { mutableStateOf(0f) } // 平移偏移 Y
    val FIXSCREENSTART = 20f

    var clickPosition by remember { mutableStateOf<Offset?>(null) } // 存储点击位置
    var clickWorldPosition by remember { mutableStateOf<Pair<Float, Float>?>(null) } // 存储点击的世界坐标
  //  val screenWidth = getScreenWidthInPx()
  //  val screenHeight =getScreenHeightInPx()
    ExtendedFloatingActionButton(
        onClick = {
            scale =1f
            zoomScale=1f
            offsetX=0f
            offsetY=0f
        },
        shape = FloatingActionButtonDefaults.smallShape,
        modifier = Modifier
            // TODO ,adjust the right padding.
            .padding(16.dp)  // Adds padding from the edges
            .clip(CircleShape)
            .zIndex(1f)
            .size(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = stringResource(id = R.string.edit),
        )

    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit)
            {

                detectTapGestures { tapOffset ->
                    clickPosition = tapOffset
                    Log.e("Scale", "detectTapGestures is called")
                }

            }
            .pointerInput(Unit) {


                        detectTransformGestures { centroid, pan, zoom, _ ->
                            // 缩放操作
                            if (zoom != 1f && scale in 0.1f..100f) {
                                val oldScale = zoomScale
                                zoomScale *= zoom

                                // 缩放是围绕手指中间点(centroid)进行的，计算新的偏移
                                val scaleDiff = zoomScale / oldScale

                                // 调整偏移量，以保证缩放中心在手指的中间点
                                offsetX = (offsetX -  centroid.x  ) * scaleDiff + centroid.x
                                offsetY = (offsetY  - centroid.y) * scaleDiff + centroid.y
                                //  zoomScale *= zoom
                            } else
                            // 平移操作
                            {
                                // 平移操作
                                // 限制 X 轴平移，只允许向右移动，并且不能超过画布宽度（考虑缩放）
                                val maxOffsetX = size.width * (scale - 1)
                                offsetX = (offsetX + pan.x).coerceIn(0f, maxOffsetX)

                                // 限制 Y 轴平移，保证上下移动在合理范围内
                                val maxOffsetY = size.height * (scale - 1)
                                offsetY = (offsetY - pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                            }
                          /*  Log.e(
                                "Scale",
                                "zoomScale is : ${zoomScale},zoom is : ${zoom},scale is ${scale}"
                            )
                            Log.e("Scale", "offsetX is : ${offsetX},offsetY is : ${offsetY}")*/
                        }

                    }


    ) {
        var canvasWidth = size.width
        var canvasHeight = size.height
        if (canvasWidth > 0 && canvasHeight > 0) {
            val tagetPos = viewModel.objectPosition
           // Log.e("TAG", "tagetPos is ${tagetPos}")
            val worldWidth = tagetPos.first * canvasHeight / canvasWidth
            val worldHeight = tagetPos.second * canvasHeight / canvasWidth
            //TODO: scale 在这里被写死了。没有办法修改了。
            scale = if (tagetPos.first != 0f && tagetPos.second != 0f)
                min(canvasWidth / worldWidth, canvasHeight / worldHeight)
            else
                min(canvasWidth / 30, canvasHeight / 30).toFloat()  //TODO: 默认是30米，30米的空间；
            scale *= zoomScale
        /*    Log.e("Shot", "scale is ${scale}")
            Log.e("Shot", "offsetX is ${offsetX}")
            Log.e("Shot", "offsetY is ${offsetY}")*/
            // 世界坐标转屏幕坐标函数
            fun worldToScreen(worldX: Float, worldY: Float): Offset {
                val screenX = (worldX * scale) + offsetX +FIXSCREENSTART//+ width / 2f
                val screenY = canvasHeight - ((worldY * scale) + offsetY +FIXSCREENSTART) // 翻转 Y 轴
                return Offset(screenX, screenY)
            }

            // 屏幕坐标转世界坐标函数
            fun screenToWorld(screenX: Float, screenY: Float): Offset {
                val worldX = (screenX - offsetX -FIXSCREENSTART) / scale
                val worldY = (canvasHeight - screenY - offsetY - FIXSCREENSTART) / scale
                return Offset(worldX, worldY)
            }

            // 绘制坐标系（基于世界坐标）
            drawCoordinateSystem(
                scale = scale,
                0f, //TODO : 可能需要修改为动态的值；
                0f,//TODO : 可能需要修改为动态的值；
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
                objectRadius = viewModel.radius_mm
            )


            // 绘制点击位置的提示
            clickPosition?.let { tapOffset ->

                val worldPosition = screenToWorld(tapOffset.x, tapOffset.y)
                clickWorldPosition = worldPosition.x to worldPosition.y

                // 判断是否在曲线附近
                if (isPointOnCurve(worldPosition.x to worldPosition.y, viewModel.positions, scale)) {
                    // 点击在曲线附近，记录点击位置
                    clickPosition = tapOffset
                    drawCircle(
                        color = Color.Green,
                        radius = 10f,
                        center = tapOffset
                    )
                    drawText(
                        text = "(${String.format("%.1f", clickWorldPosition?.first)}, ${String.format("%.1f", clickWorldPosition?.second)})",
                        x = tapOffset.x,
                        y = tapOffset.y - 20f,
                        paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.RED
                            textSize = 30f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }

            }
        }
    }
}

fun DrawScope.drawCoordinateSystem(
    scale: Float,
    worldstartx : Float,
    worldstarty:Float,
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


    var (xendscreen,yendscreen) = worldToScreen(worldstartx,worldstarty)
    val fixscreenstartx =  20f
    val fixscreenstarty = height -20f
   // Log.e("TAG","the yendScreen is ${fixscreenstarty}")
  //  Log.e("TAG","the xendScreen is ${fixscreenstartx}")
    // 绘制 X 轴
    drawLine(
        color = Color.Black,
        start = Offset(fixscreenstartx,  fixscreenstarty),
        end = Offset(width,  fixscreenstarty),
        strokeWidth = 2f
    )

    // 绘制 Y 轴
    drawLine(
        color = Color.Black,
        start = Offset(fixscreenstartx, 0f),
        end = Offset(fixscreenstartx, fixscreenstarty),
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
            start = Offset(screenPos.x, fixscreenstarty - 5f),
            end = Offset(screenPos.x,  fixscreenstarty + 5f),
            strokeWidth = 2f
        )
        // 绘制标签
        if (adjustedStep >= 50f / scale) { // 控制标签显示密度
            drawText(
                text = String.format("%.1f", x),
                x = screenPos.x,
                y = fixscreenstarty + 20f,
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
            start = Offset(  fixscreenstartx- 5f, screenPos.y),
            end = Offset( fixscreenstartx+ 5f, screenPos.y),
            strokeWidth = 2f
        )
        // 绘制标签
        if (adjustedStep >= 50f / scale) { // 控制标签显示密度
            drawText(
                text = String.format("%.1f", y),
                x = fixscreenstartx+ 20f,
                y = screenPos.y ,
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
    objectRadius: Float
) {
    if (positions.isEmpty()) return

    val path = Path()

    // 开始绘制路径
    val firstPoint = positions.first()
    val firstScreenPoint = worldToScreen(firstPoint.x, firstPoint.y)
    path.moveTo(firstScreenPoint.x , firstScreenPoint.y)

    // 使用二次贝塞尔曲线连接点
    for (i in 1 until positions.size - 1 step 2) {
        val controlPoint = positions[i]
        val endPoint = positions[i + 1]
        val controlScreen = worldToScreen(controlPoint.x,controlPoint.y)
        val endScreen = worldToScreen(endPoint.x,endPoint.y)

        path.quadraticBezierTo(controlScreen.x , controlScreen.y , endScreen.x, endScreen.y)
    }
    var objectR = objectRadius * scale/(2.0f*size.minDimension)
    if(objectR < 2f)
        objectR = 2f
    // 绘制路径
    Log.e("TAG","the objectR is: ${objectR}")
    drawPath(
        path = path,
        color = Color.Blue,
        style = Stroke(width = objectR)
    )

    // 绘制对象位置（红色圆圈）
    val (objX, objY) = objectPosition

    val objectScreenPos = worldToScreen(objX, objY)
    Log.e("TAG","the ObjectScreenPos is: ${objectScreenPos}")
    Log.e("TAG", "drawCurve: ${objectRadius * scale/(2.0f*size.minDimension)}")
    drawCircle(
        color = Color.Red,
        radius = objectR*3,//TODO 3 is default
        center = objectScreenPos
    )
    drawText(
        text = "(${String.format("%.1f", objX)}, ${String.format("%.1f", objY)})",
        x = objectScreenPos.x,
        y = objectScreenPos.y - 20f,
        paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLUE
            textSize = 30f
            textAlign = android.graphics.Paint.Align.CENTER
        }
    )
}


// 判断点击位置是否在曲线附近
fun isPointOnCurve(worldPos: Pair<Float, Float>, curvePoints: List<Position>, scale: Float): Boolean {
    val threshold = 10f / scale // 设置点击的容差值，根据缩放比例调整
    for (i in 0 until curvePoints.size - 1) {
        val point1 = curvePoints[i]
        val point2 = curvePoints[i + 1]

        // 计算点击点到曲线点的距离，如果距离小于某个阈值，则认为点击到了曲线
        val distance = distanceFromPointToLineSegment(worldPos, point1, point2)
        if (distance < threshold) {
            return true
        }
    }
    return false
}

// 计算点到线段的距离
fun distanceFromPointToLineSegment(p: Pair<Float, Float>, v: Position, w: Position): Float {
    val px = p.first
    val py = p.second

    val vx = v.x
    val vy = v.y

    val wx = w.x
    val wy = w.y

    val l2 = (wx - vx).pow(2) + (wy - vy).pow(2)
    if (l2 == 0f) return sqrt((px - vx).pow(2) + (py - vy).pow(2))

    val t = ((px - vx) * (wx - vx) + (py - vy) * (wy - vy)) / l2
    if (t < 0) return sqrt((px - vx).pow(2) + (py - vy).pow(2))
    if (t > 1) return sqrt((px - wx).pow(2) + (py - wy).pow(2))

    val projX = vx + t * (wx - vx)
    val projY = vy + t * (wy - vy)
    return sqrt((px - projX).pow(2) + (py - projY).pow(2))
}



