package com.ai.aishotclientkotlin.engine.shot

import android.util.Log
import com.ai.aishotclientkotlin.engine.shot.ProjectileMotionSimulator.calculateTrajectory
import kotlin.math.*

// Data class for Position
data class Position(val x: Float, val y: Float, val vx: Float, val vy: Float, val t: Float,val ax: Float,val ay: Float)

// Constants
const val PI = 3.1416f
const val G = 9.81f // Gravity constant (m/s^2)


//// Function to calculate air resistance
//fun dragForce(va: Float, A: Float): Float {
//    return (0.5f * CD_AIR * RHO * A * va * va)
//}
// 空气阻力的计算
fun dragForce(v: Float, A: Float, Cd: Float = 0.47f, rho: Float = 1.225f): Float {
    return 0.5f * Cd * rho * A * v * v
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
fun findPosByX(poss: List<Position>, shotCause: ShotCauseState) : Position?
{
    //
    val radius_m = 100 * shotCause.shotConfig.radius_mm/1000
    val targetPos: Pair<Float, Float>  = shotCause.targetPosReal()
    var loop = 1
    var possnew = poss.filter { it -> (targetPos.first - radius_m < it.x) and (it.x <  targetPos.first +  radius_m) } //. filter { it -> abs(it.y - targetPos.second) < shotCause.radius * 100  }
    var smallest = possnew.minByOrNull { it -> abs(it.x - targetPos.first) }
    if(shotCause.velocityAngle > 75.0f)
    {
         possnew = possnew.filter { it -> (targetPos.second - radius_m< it.y) and (it.y <  targetPos.second + radius_m) } //. filter { it -> abs(it.y - targetPos.second) < shotCause.radius * 100  }
         smallest = possnew.minByOrNull { it -> abs(it.y - targetPos.second) }
        while (smallest == null){
            loop ++
            possnew =possnew.filter { it -> (targetPos.second - radius_m * loop < it.y) and (it.y <  targetPos.second + radius_m * loop) }
            smallest = possnew.minByOrNull { it -> abs(it.y - targetPos.second) }
        }
    }

    while (smallest == null){
        loop ++
        possnew =possnew.filter { it -> (targetPos.first - radius_m * loop< it.x) and (it.x <  targetPos.first + radius_m * loop) }
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

fun calDifftPosAndPosOnTraj(targetPosOnTrajectory : Pair<Float,Float>,shotCause: ShotCauseState): Float {
    val targetPosReal: Pair<Float,Float> =shotCause.targetPosReal()
    val diffx = targetPosReal.first - targetPosOnTrajectory.first
    val diffy = targetPosReal.second - targetPosOnTrajectory.second
  //  val diffxy = sqrt(diffx*diffx + diffy*diffy)
    // TODO : 这里有bug。在90度左右，虽然相差很大的Y，但是X相差很少，依然会返回很大的差别，（真实情况是，目标已经达到）。
    val radius_m = shotCause.shotConfig.radius_mm/1000
    var espion = radius_m * 3.0f
    if(targetPosReal.first < 0.1f  ) // X很小，说明是垂直的。
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

suspend fun optimizeTrajectory(
    shotCause: ShotCauseState,
    updateFun: (ShotCauseState, Float) -> Unit,
    function: ((List<Position>, Position?) -> Unit)? = null) : Pair<List<Position>, Position?> {
    val radius_m = shotCause.shotConfig.radius_mm/1000
//    val rad = Math.toRadians(shotCause.angle.toDouble())
//    val targetPosReal : Pair<Float,Float> = Pair(cos(rad).toFloat() * shotCause.shotDistance, sin(rad).toFloat() * shotCause.shotDistance)
    var positions = calculateTrajectory(shotCause)
    //Log.e("Dispatchers","have cal the positions is : ${positions.size}")
    var targetPosOnTrajectory = findPosByX(positions,shotCause)
    var diff = calDifftPosAndPosOnTraj((targetPosOnTrajectory!!.x to targetPosOnTrajectory.y),shotCause)
   // var smallest =diff
    val maxIterations = 100 // 最大迭代次数
    var iterationCount = 0
    val epsilon = radius_m * 3.0f // 设置一个很小的收敛阈值
    while(abs(diff) > epsilon && iterationCount < maxIterations){
        updateFun(shotCause,diff)
       // shotCause.velocityAngle = shotCause.angle
        positions= calculateTrajectory(shotCause)
     //   Log.e("Dispatchers","have cal the positions is : ${positions.size}")
        targetPosOnTrajectory = findPosByX(positions,shotCause)
        var  diffNew = calDifftPosAndPosOnTraj((targetPosOnTrajectory!!.x to targetPosOnTrajectory.y),shotCause)
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
      //  Log.e("Dispatchers","the iterationCount is : ${iterationCount} ")
    }
    shotCause.shotDiffDistance = diff
    if (targetPosOnTrajectory != null) {
        shotCause.targetPosOnTrajectory = targetPosOnTrajectory
    }
    return positions to targetPosOnTrajectory

}



suspend fun optimizeTrajectoryByAngle(
    shotCause: ShotCauseState,
    function: ((List<Position>, Position?) -> Unit)? = null) : Pair<List<Position>, Position?> {


  return  optimizeTrajectory(shotCause, { shotCause, diff ->
      run {
          // TODO: 这里容易出BUG，当diff太大的时候，容易出现巨大的偏差。采用更平滑的方式；
          val argdiff = Math.toDegrees(asin(diff.toDouble() / shotCause.shotDistance))
          shotCause.velocityAngle = (shotCause.velocityAngle + argdiff).toFloat()

      }
  },function)

}

suspend fun optimizeTrajectoryByAngleAndVelocity(shotCause: ShotCauseState, function: ((List<Position>, Position?) -> Unit)? = null) : Pair<List<Position>, Position?> {

    return  optimizeTrajectory(shotCause, { shotCause, diff ->
        run {
            val argdiff = Math.toDegrees(asin(diff.toDouble() / shotCause.shotDistance))
            shotCause.velocityAngle = (shotCause.velocityAngle + argdiff).toFloat()
            shotCause.velocity += (diff / shotCause.shotDistance) * shotCause.velocity
        }
    },function)

}



suspend fun optimizeTrajectoryByVelocity(shotCause: ShotCauseState, function: ((List<Position>, Position?) -> Unit)? = null): Pair<List<Position>, Position?> {

    return  optimizeTrajectory(shotCause, { shotCause, diff ->
        run {
            shotCause.velocity += (diff / shotCause.shotDistance) * shotCause.velocity
        }
    },function)
}

// Calculate trajectory and distance intersection
suspend fun initDistanceAndTrajectory(shotCause: ShotCauseState): Float {
    val optimizeValue = optimizeTrajectoryByAngle(shotCause)

    val targetPosOnTrajectory = optimizeValue.second!!
    val targetPos : Pair<Float,Float> = targetPosOnTrajectory.x to targetPosOnTrajectory.y
    val positionShotHead = calculateShotPointWithArgs(shotCause.velocityAngle,
        targetPos = targetPos,
        shotCause.shotConfig.eyeToBowDistance,
        shotCause.shotConfig.eyeToAxisDistance,
        shotCause.shotDistance,
        shotCause.shotConfig.shotDoorWidth)
   // optimizeTrajectoryByVelocity(shotCause)
   // optimizeTrajectoryByAngleAndVelocity(shotCause)
    return positionShotHead
}

fun main() {

}




