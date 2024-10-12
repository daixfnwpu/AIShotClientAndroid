package com.ai.aishotclientkotlin.engine.shot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

data class ProjectileMotionData(
    val time: Float,
    val xPosition: Float,
    val yPosition: Float,
    val xVelocity: Float,
    val yVelocity: Float,
    val totalVelocity: Float,
    val distance: Float,
    val yInitial: Double,
    val yDifference: Double,
    val objectAngle: Double,
    val pointsOnShotHead:Double
)



object ProjectileMotionSimulator {



    fun calculatePointsOnShotHead(eyeSlibber: Double, yY0Diff: DoubleArray, distanceValues: DoubleArray): DoubleArray {
        return yY0Diff.mapIndexed { index, diff ->
            eyeSlibber * diff / distanceValues[index]
        }.toDoubleArray()
    }

    // Calculate the projectile trajectory
    suspend fun calculateTrajectory( shotCause: ShotCauseState): List<Position> {
        var r: Float = shotCause.shotConfig.radius_mm/1000
        val v0: Float = shotCause.velocity
        val theta0:Float = shotCause.velocityAngle
        val destiny: Float = shotCause.destiny
        val A = PI * r * r // Cross-sectional area (m^2)
        val m = destiny * 4 * PI * r * r * r  / 3 // Mass (kg), density 2.5 g/cm^3
        val thetaRad = theta0 * PI / 180.0
        val v0x = v0 * cos(thetaRad).toFloat()
        val v0y = v0 * sin(thetaRad).toFloat()
        val dt = 0.001f// Time step (s)

        val positions = mutableListOf(Position(0.0f, 0.0f, v0x, v0y, 0.0f,0.0f,0.0f))

        var x = 0.0f
        var y = 0.0f
        var vx = v0x
        var vy = v0y
        var t = 0.0f
        val yEndPositionUP = shotCause.targetPosReal().second + abs(shotCause.targetPosReal().second) * abs(shotCause.targetPosReal().second)/100
        val xEndPositionUP = shotCause.targetPosReal().first * 1.3
        val yEndPositionDown = shotCause.targetPosReal().second - abs(shotCause.targetPosReal().second) * abs(shotCause.targetPosReal().second)/100
        return withContext(Dispatchers.Default) {
            while ((x < xEndPositionUP) && ((vy >= 0 && y <= yEndPositionUP) || (vy <= 0 && y > yEndPositionDown))) {

                val v = sqrt(vx * vx + vy * vy)

                // 基于速度模计算空气阻力
                val ax = (-dragForce(v, A) * vx / v) / m // 沿 x 方向的空气阻力
                val ay = -G - (abs(vy) / vy) * ((dragForce(v, A) * vy / v) / m) // 沿 y 方向的加速度，包括重力

//       val ax = -dragForce(vx, A) / m
//       val ay = if (vy > 0) -G - dragForce(vy, A) / m else -G + dragForce(vy, A) / m

                vx += ax * dt
                vy += ay * dt

                x += vx * dt
                y += vy * dt

                positions.add(Position(x, y, vx, vy, t, ax, ay))
                t += dt
            }

            return@withContext positions
        }

    }

    fun transformPostionsToMotion( positions : List<Position>,eyeSlibber: Double,thetaRad: Double) : List<ProjectileMotionData> {
         return positions.map { it ->

            val totalVelocity = sqrt(it.vx * it.vx + it.vy * it.vy)
            val distance = sqrt(it.x * it.x + it.y * it.y)
            val yInitial = it.x * tan(thetaRad)
            val yDifference = yInitial - it.y
            val objectAngle = Math.toDegrees(atan2(it.y, it.x).toDouble())
            val pointsOnShotHead = if (distance.toDouble() != 0.0) {
                eyeSlibber * yDifference / distance
            } else {
                0.0 // 或者选择其他默认值
            }

            val motion = ProjectileMotionData(
                time = it.t,
                xPosition = it.x,
                yPosition =  it.y,
                xVelocity =  it.vx,
                yVelocity = it.vy,
                totalVelocity = totalVelocity,
                distance = distance,
                yInitial = yInitial,
                yDifference = yDifference,
                objectAngle = objectAngle,
                pointsOnShotHead = pointsOnShotHead
            )
             return@map motion
        }
    }
/*
    suspend fun simulateProjectileMotion(v0: Double, theta: Double, timeSpan: Double,shotCause: ShotCauseState): List<ProjectileMotionData> {
         val rho = shotCause.air_rho// 1.225  // 空气密度 (kg/m^3)
         val g = shotCause.G//9.81     // 重力加速度 (m/s^2)
         val Cd = shotCause.Cd//    // 阻力系数
         val r = shotCause.radius    // 半径 (m)
         val rhoM = shotCause.density  // 钢球密度 (kg/m^3)
         val m = 4 * Math.PI * r * r * r * rhoM / 3 // 钢球质量 (kg)
         val eyeSlibber =shotCause.eyeToAxisDistance //

        return withContext(Dispatchers.Default) {
            val data = mutableListOf<ProjectileMotionData>()
            val thetaRad = Math.toRadians(theta)

            // 初始速度分解
            val v0x = v0 * cos(thetaRad)
            val v0y = v0 * sin(thetaRad)

            // 时间离散化
            val tSteps = 100
            val tStep = timeSpan / tSteps

            var x = 0.0
            var y = 0.0
            var vx = v0x
            var vy = v0y

            for (i in 0 until tSteps) {
                val v = sqrt(vx * vx + vy * vy)  // 当前速度
                val dragX = -0.5 * Cd * rho * Math.PI * r * r * v * vx * vx / abs(vx) // x方向的阻力
                val dragY = -0.5 * Cd * rho * Math.PI * r * r * v * vy * vy / abs(vy) // y方向的阻力

                // 更新速度
                vx += dragX / m * tStep
                vy += (-g + dragY / m) * tStep

                // 更新位置
                x += vx * tStep
                y += vy * tStep

                val totalVelocity = sqrt(vx * vx + vy * vy)
                val distance = sqrt(x * x + y * y)
                val yInitial = x * tan(thetaRad)
                val yDifference = yInitial - y
                val objectAngle = Math.toDegrees(atan2(y, x))
                val pointsOnShotHead = if (distance != 0.0) {
                    eyeSlibber * yDifference / distance
                } else {
                    0.0 // 或者选择其他默认值
                }
                // 添加数据点
                data.add(
                    ProjectileMotionData(
                        (i * tStep).toFloat(), x.toFloat(), y.toFloat(),
                        vx.toFloat(), vy.toFloat(), totalVelocity.toFloat(),
                        distance.toFloat(), yInitial, yDifference, objectAngle,pointsOnShotHead)
                )
            }

            return@withContext data
        }
    }*/
}