package com.ai.aishotclientkotlin.engine

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.*

// Data class for Position
data class Position(val x: Float, val y: Float, val vx: Float, val vy: Float, val t: Float)

// Constants
const val PI = 3.1416f
const val G = 9.81f // Gravity constant (m/s^2)
const val CD_AIR = 0.47f // Drag coefficient (typical for a sphere)
const val RHO = 1.225f // Air density (kg/m^3)

// Function to calculate air resistance
fun dragForce(va: Float, A: Float): Float {
    return (0.5f * CD_AIR * RHO * A * va * va)
}

// Calculate the projectile trajectory
fun calculateTrajectory(r: Float, v0: Float, theta0: Float, destiny: Float): List<Position> {
    val A = PI * r * r // Cross-sectional area (m^2)
    val m = destiny * 4 * PI * r * r * r * 1000 / 3 // Mass (kg), density 2.5 g/cm^3
    val thetaRad = theta0 * PI / 180.0
    val v0x = v0 * cos(thetaRad).toFloat()
    val v0y = v0 * sin(thetaRad).toFloat()
    val dt = 0.001f// Time step (s)

    val positions = mutableListOf(Position(0.0f, 0.0f, v0x, v0y, 0.0f))

    var x = 0.0f
    var y = 0.0f
    var vx = v0x
    var vy = v0y
    var t = 0.0f

    while (y >= 0) {
        val ax = -dragForce(vx, A) / m
        val ay = if (vy > 0) -G - dragForce(vy, A) / m else -G + dragForce(vy, A) / m

        vx += ax * dt
        vy += ay * dt

        x += vx * dt
        y += vy * dt

        positions.add(Position(x, y, vx, vy, t))
        t += dt
    }

    return positions
}

// Calculate slope and intercept
fun calculateSlopeIntercept(p1: Pair<Float, Float>, p2: Pair<Float, Float>): Pair<Float, Float> {
    if (p1.first == p2.first) {
        println("The points are vertical, cannot define a unique slope.")
        return 0.0f to 0.0f
    }
    val slope = (p2.second - p1.second) / (p2.first - p1.first)
    val intercept = p1.second - slope * p1.first
    return slope to intercept
}

// Find intersection of two lines
fun findIntersection(m1: Float, b1: Float, m2: Float, b2: Float): Pair<Float, Float> {
    if (m1 == m2) {
        return Float.POSITIVE_INFINITY to Float.POSITIVE_INFINITY
    }
    val x = (b2 - b1) / (m1 - m2)
    val y = m1 * x + b1
    return x to y
}

// Calculate perpendicular slope at a given point
fun perpendicularSlopeAtPoint(slope: Float): Float {
    return when {
        slope == 0.0f -> Float.POSITIVE_INFINITY
        slope.isInfinite() -> 0.0f
        else -> -1 / slope
    }
}

// Perpendicular line equation at a given point
fun perpendicularLineEquation(slope: Float, x1: Float, y1: Float): (Float) -> Float {
    val perpSlope = perpendicularSlopeAtPoint(slope)
    return { x: Float -> perpSlope * (x - x1) + y1 }
}

// Calculate end points of a line segment
fun getSegmentEndpoints(
    x1: Float, y1: Float, segmentLength: Float, perpLineEq: (Float) -> Float, slope: Float
): Pair<Pair<Float, Float>, Pair<Float, Float>> {
    val direction = if (slope >= 0) 1 else -1
    val cosTheta = 1 / sqrt(1 + slope.pow(2))
    val xEnd = x1 + direction * segmentLength * cosTheta
    val yEnd = perpLineEq(xEnd)

    return (x1 to y1) to (xEnd to yEnd)
}

// Create line equation (lambda)
fun createLineLambda(x1: Float, y1: Float, x2: Float, y2: Float): (Float) -> Float {
    val (m, b) = calculateSlopeIntercept(x1 to y1, x2 to y2)
    return { x: Float -> m * x + b }
}
fun findPosByX( poss: List<Position>,targetPos: Pair<Float, Float>,shotCause: ShotCauseState) : Position?
{
    //
    var loop = 1
    var possnew = poss.filter { it -> (targetPos.first - 100* shotCause.radius < it.x) and (it.x <  targetPos.first + 100* shotCause.radius) } //. filter { it -> abs(it.y - targetPos.second) < shotCause.radius * 100  }
    var smallest = possnew.minByOrNull { it -> abs(it.x - targetPos.first) }
    while (smallest == null){
        loop ++
        possnew =poss.filter { it -> (targetPos.first - 100* loop* shotCause.radius < it.x) and (it.x <  targetPos.first + 100* loop * shotCause.radius) }
        smallest = possnew.minByOrNull { it -> abs(it.x - targetPos.first) }
    }
    return smallest
}
// Calculate position at a given shot distance
fun findPosByShotDistance(arg: Float, poss: List<Position>, shotDistance: Float): Pair<Float, Float> {
    fun yFunByX(xVal: Float): Float {
        val position = poss.minByOrNull { abs(it.x - xVal) }
        return position?.y ?: 0.0f
    }

    fun equation(x: Float, r: Float): Float {
        val y = yFunByX(x)
        return abs(sqrt(x.pow(2) + y.pow(2)) - r)
    }

    val thetaRad = arg * PI / 180.0f
    val initXGuess = shotDistance * cos(thetaRad)
    val constGuess = 10 * cos(thetaRad)

    val initialGuesses = List(100) { initXGuess - constGuess + it * (2 * constGuess / 100) }

    val solutions = initialGuesses.filter { guess -> equation(guess, shotDistance) <= 0.2 }

    return if (solutions.isNotEmpty()) {
        val ySolutions = solutions.map { yFunByX(it) }
        solutions.first() to ySolutions.first()
    } else {
        Float.POSITIVE_INFINITY to Float.POSITIVE_INFINITY
    }
}

// Calculate shot point
fun calculateShotPointWithArgs(
    theta0: Float, targetPos: Pair<Float, Float>, distanceHandToEye: Float,
    eyeToAxis: Float, shotDistance: Float, shotDoorWidth: Float = 0.04f,shotHeadWidth :Float= 0.02f, powerScala: Float = 1.0f
): Float {
    if (targetPos.first != Float.POSITIVE_INFINITY) {
        val (targetX, targetY) = targetPos
        val thetaRad = (theta0 * PI) / 180.0f
        val slope = tan(thetaRad)

        val (x0, y0) = getSegmentEndpoints(0.0f, 0.0f, -distanceHandToEye, { x -> slope * x }, slope).second
        val eyeOnAxisLine = perpendicularLineEquation(slope, x0, y0)

        //TODO : eyex0 and eyey0 的位置是否正确？
        val (eyeX0, eyeY0) = getSegmentEndpoints(x0, y0, eyeToAxis, eyeOnAxisLine, perpendicularSlopeAtPoint(slope)).second

        val shotLineSlope = (targetY - eyeY0) / (targetX - eyeX0)
        val shotLineIntercept =eyeY0 - shotLineSlope * eyeX0
        val (intersectX, intersectY) = findIntersection(-1/slope, 0.0f, shotLineSlope, shotLineIntercept)

        return shotHeadWidth + shotDoorWidth / 2 - intersectY / cos(thetaRad)
    }
    return 0.0f
}

fun calDifftPosAndPosOnTraj(targetPosOnTrajectory : Pair<Float,Float>,targetPosReal: Pair<Float,Float>,shotCause: ShotCauseState): Float {
    val diffx = targetPosReal.first - targetPosOnTrajectory.first
    val diffy = targetPosReal.second - targetPosOnTrajectory.second
  //  val diffxy = sqrt(diffx*diffx + diffy*diffy)
    // TODO : 这里有bug。在90度左右，虽然相差很大的Y，但是X相差很少，依然会返回很大的差别，（真实情况是，目标已经达到）。
    var espion = shotCause.radius * 3.0f
    if(targetPosReal.first < 0.1f ) // X很小，说明是垂直的。
    {
        if(abs(diffx) < espion )
            return abs(diffx)
    }
    if (abs(diffy) > espion)
    {
        return diffy
    }
    else
        return  diffy
}

fun optimizeTrajectory(
    shotCause: ShotCauseState,
    updateFun: (ShotCauseState,Float) -> Unit,
    function: ((List<Position>, Position?) -> Unit)? = null) : Pair<List<Position>, Position?> {

    val rad = Math.toRadians(shotCause.angle.toDouble())
    val targetPosReal : Pair<Float,Float> = Pair(cos(rad).toFloat() * shotCause.shotDistance, sin(rad).toFloat() * shotCause.shotDistance)
    var positions = calculateTrajectory(shotCause.radius, shotCause.velocity, shotCause.angle, shotCause.density)
    var targetPosOnTrajectory = findPosByX(positions,targetPosReal,shotCause)
    var diff = calDifftPosAndPosOnTraj((targetPosOnTrajectory!!.x to targetPosOnTrajectory.y),targetPosReal,shotCause)
   // var smallest =diff
    val maxIterations = 100 // 最大迭代次数
    var iterationCount = 0
    val epsilon = shotCause.radius * 3.0f // 设置一个很小的收敛阈值
    while(abs(diff) > epsilon && iterationCount < maxIterations){
        updateFun(shotCause,diff)
        shotCause.velocityAngle = shotCause.angle
        positions= calculateTrajectory(shotCause.radius, shotCause.velocity, shotCause.angle, shotCause.density)
        targetPosOnTrajectory = findPosByX(positions,targetPosReal,shotCause)
        var  diffNew = calDifftPosAndPosOnTraj((targetPosOnTrajectory!!.x to targetPosOnTrajectory.y),targetPosReal,shotCause)
        function?.let { it(positions,targetPosOnTrajectory) }
        // Smooth adjustment to avoid oscillation
        if (diffNew * diff < 0) {
            diff = - diff / 2  // 跨越目标时，减小调整幅度
        } else if (abs(diffNew) > abs(diff)) {

            if (abs(diffNew) / abs(diff) > 1)  // 突然增长很多。
                diff = -diff + diff * 0.75f
            else
                diff = -diff +  diffNew * 0.75f  // 控制调整的增幅，避免剧烈变化
        } else {
            diff = diffNew
        }
        iterationCount++
    }
    shotCause.shotDiffDistance = diff
    return positions to targetPosOnTrajectory

}



fun optimizeTrajectoryByAngle(
    shotCause: ShotCauseState,
  function: ((List<Position>, Position?) -> Unit)? = null) : Pair<List<Position>, Position?> {


  return  optimizeTrajectory(shotCause, { shotCause, diff ->
      run {
          val argdiff = Math.toDegrees(asin(diff.toDouble() / shotCause.shotDistance))
          shotCause.angle = (shotCause.angle + argdiff).toFloat()

      }
  },function)

}

fun optimizeTrajectoryByAngleAndVelocity(shotCause: ShotCauseState,function: ((List<Position>, Position?) -> Unit)? = null) : Pair<List<Position>, Position?> {

    return  optimizeTrajectory(shotCause, { shotCause, diff ->
        run {
            val argdiff = Math.toDegrees(asin(diff.toDouble() / shotCause.shotDistance))
            shotCause.angle = (shotCause.angle + argdiff).toFloat()
            shotCause.velocity += (diff / shotCause.shotDistance) * shotCause.velocity
        }
    },function)

}



fun optimizeTrajectoryByVelocity(shotCause: ShotCauseState,function: ((List<Position>, Position?) -> Unit)? = null): Pair<List<Position>, Position?> {

    return  optimizeTrajectory(shotCause, { shotCause, diff ->
        run {
            shotCause.velocity += (diff / shotCause.shotDistance) * shotCause.velocity
        }
    },function)
}

// Calculate trajectory and distance intersection
fun initDistanceAndTrajectory(shotCause: ShotCauseState): Float {
    val optimizeValue = optimizeTrajectoryByAngle(shotCause)

    val targetPosOnTrajectory = optimizeValue.second!!
    val targetPos : Pair<Float,Float> = targetPosOnTrajectory.x to targetPosOnTrajectory.y
    val positionShotHead = calculateShotPointWithArgs(shotCause.angle,
        targetPos = targetPos,
        shotCause.eyeToBowDistance,
        shotCause.eyeToAxisDistance,
        shotCause.shotDistance,
        shotCause.shotDoorWidth)
   // optimizeTrajectoryByVelocity(shotCause)
   // optimizeTrajectoryByAngleAndVelocity(shotCause)
    return positionShotHead
}

fun main() {
    val workbook: Workbook = XSSFWorkbook() // 创建一个新的工作簿
    val sheet = workbook.createSheet("Sheet1") // 创建一个新的表格
    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("distance")
    headerRow.createCell(1).setCellValue("angle")

    headerRow.createCell(2).setCellValue("eyetoaxis")

    headerRow.createCell(3).setCellValue("headPosition")
    headerRow.createCell(4).setCellValue("velocity")
    headerRow.createCell(5).setCellValue("diffDistance")
    var row = 2
    for (i in  5 .. 20 step 2) {
        for (a in 5 .. 90 step 5) {
            for (e in 5..9 step 1) {
                var eye = e * 0.01

                val shotCauseState = ShotCauseState().apply {
                    radius = 0.005f
                    velocity = (60f)
                    angle = a.toFloat()
                    density = 2.5f
                    eyeToBowDistance = (0.7f)
                    eyeToAxisDistance = eye.toFloat()
                    shotDistance = i.toFloat()
                    shotHeadWidth = 0.025f
                }
                // Calculate trajectory and intersection point

                val p = initDistanceAndTrajectory(shotCauseState)
              //  println("distance: $i ; angle : $a ; eyetoaxis: $eye ; headPosition: $p")
                val dataRow = sheet.createRow(row++)
                dataRow.createCell(0).setCellValue(i.toDouble())
                dataRow.createCell(1).setCellValue(a.toDouble())
                dataRow.createCell(2).setCellValue(eye)
                dataRow.createCell(3).setCellValue(p.toDouble())
                dataRow.createCell(4).setCellValue(shotCauseState.velocity.toDouble())
                dataRow.createCell(5).setCellValue(shotCauseState.shotDiffDistance.toDouble())
           }
        }
    }
    // 保存文件
    val fileName = "app\\build\\outputs\\my_excel_file.xlsx"
//    val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/" + fileName
    val file = File(fileName)

    try {
        val fileOut = FileOutputStream(file)
        workbook.write(fileOut) // 将工作簿写入文件
        fileOut.close()
        workbook.close() // 关闭工作簿
        println("Excel 文件已保存到: $fileName")
    } catch (e: IOException) {
        e.printStackTrace()
    }

}




