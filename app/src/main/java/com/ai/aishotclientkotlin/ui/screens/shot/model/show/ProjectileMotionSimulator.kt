package com.ai.aishotclientkotlin.ui.screens.shot.model.show
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

data class ProjectileMotionData(
    val time: Double,
    val xPosition: Double,
    val yPosition: Double,
    val xVelocity: Double,
    val yVelocity: Double,
    val totalVelocity: Double,
    val distance: Double,
    val yInitial: Double,
    val yDifference: Double,
    val objectAngle: Double,
    val pointsOnShotHead:Double
)



object ProjectileMotionSimulator {

    private val rho = 1.225  // 空气密度 (kg/m^3)
    private val g = 9.81     // 重力加速度 (m/s^2)
    private val Cd = 0.47    // 阻力系数
    private val r = 0.005    // 半径 (m)
    private val rhoM = 7850  // 钢球密度 (kg/m^3)
    private val m = 4 * Math.PI * r * r * r * rhoM / 3 // 钢球质量 (kg)
    private val eyeSlibber =0.7 //

    fun calculatePointsOnShotHead(eyeSlibber: Double, yY0Diff: DoubleArray, distanceValues: DoubleArray): DoubleArray {
        return yY0Diff.mapIndexed { index, diff ->
            eyeSlibber * diff / distanceValues[index]
        }.toDoubleArray()
    }
    suspend fun simulateProjectileMotion(v0: Double, theta: Double, timeSpan: Double): List<ProjectileMotionData> {
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
                data.add(ProjectileMotionData(i * tStep, x, y, vx, vy, totalVelocity,
                    distance, yInitial, yDifference, objectAngle,pointsOnShotHead))
            }

            return@withContext data
        }
    }
}