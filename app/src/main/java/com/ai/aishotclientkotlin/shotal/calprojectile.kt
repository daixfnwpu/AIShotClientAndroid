package com.ai.aishotclientkotlin.shotal

import android.util.Log
import kotlin.math.*

data class Position(
    var x: Double,
    var y: Double,
    var vx: Double,
    var vy: Double,
    var t: Double
)

fun dragForce(va: Double, A: Double): Double {
    val CD_AIR = 0.47 // Coefficient of drag for a sphere
    val RHO = 1.225   // Air density (kg/m^3)
    return 0.5 * CD_AIR * RHO * A * va * va
}

fun calplg(r: Double, v0: Double, theta0: Double, r0: Double): List<Position> {
    val PI = Math.PI
    val G = 9.81 // Gravitational constant
    val A = PI * r * r // Cross-sectional area (m^2)
    val m = r0 * 4 * PI * r * r * r * 1000 / 3 // Projectile mass (kg), density assumed 2.5
    val thetaRad = Math.toRadians(theta0)
    val v0X = v0 * cos(thetaRad)
    val v0Y = v0 * sin(thetaRad)
    val dt = 0.001 // Time step (s)

    val positions = mutableListOf(Position(0.0, 0.0, v0X, v0Y, 0.0))

    var x = 0.0
    var y = 0.0
    var vx = v0X
    var vy = v0Y
    var t = 0.0

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

fun calculateSlopeIntercept(p1: Pair<Double, Double>, p2: Pair<Double, Double>): Pair<Double, Double> {
    if (p1.first == p2.first) {
        Log.d("calprojectile",("The points are vertical, cannot define a unique slope."))
    }
    val slope = (p2.second - p1.second) / (p2.first - p1.first)
    val intercept = p1.second - slope * p1.first
    return Pair(slope, intercept)
}

fun findIntersection(m1: Double, b1: Double, m2: Double, b2: Double): Pair<Double, Double> {
    if (m1 == m2) {
        return Pair(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY) // Parallel lines, no intersection
    }

    val x = (b2 - b1) / (m1 - m2)
    val y = m1 * x + b1
    return Pair(x, y)
}

fun perpendicularSlopeAtPoint(slope: Double): Double {
    return when {
        slope == 0.0 -> Double.POSITIVE_INFINITY
        slope.isInfinite() -> 0.0
        else -> -1 / slope
    }
}

fun perpendicularLineEquation(slope: Double, x1: Double, y1: Double): (Double) -> Double {
    val perpSlope = perpendicularSlopeAtPoint(slope)
    return { x -> perpSlope * (x - x1) + y1 }
}

fun getSegmentEndpoints(
    x1: Double,
    y1: Double,
    segmentLength: Double,
    perpLineEq: (Double) -> Double,
    slope: Double
): Pair<Pair<Double, Double>, Pair<Double, Double>> {
    val direction = if (slope >= 0) 1 else -1
    val cosTheta = 1 / sqrt(1 + slope * slope)
    val xEnd = direction * segmentLength * cosTheta
    val yEnd = perpLineEq(xEnd)
    return Pair(Pair(x1, y1), Pair(xEnd, yEnd))
}

fun createLineLambda(x1: Double, y1: Double, x2: Double, y2: Double): (Double) -> Double {
    val (m, b) = calculateSlopeIntercept(Pair(x1, y1), Pair(x2, y2))
    return { x -> m * x + b }
}

fun findPosByShotDistance(
    arg: Double, poss: List<Position>, shotDistance: Double
): Pair<Double, Double> {
    val yFunByX = { xVal: Double ->
        val (x, y) = calyatx_(poss, xVal)
        y
    }

    val equation = { x: Double, r: Double ->
        val y = yFunByX(x)
        abs(sqrt(x * x + y * y) - r)
    }

    val thetaRad = Math.toRadians(arg)
    val initXGuess = shotDistance * cos(thetaRad)
    val constGuess = 10 * cos(thetaRad)

    val initialGuesses = (0..99).map { i ->
        initXGuess - constGuess + i * (2 * constGuess / 100)
    }

    val solutions = mutableListOf<Double>()
    var counterFind = 0
    var cont = false

    for (guess in initialGuesses) {
        if (equation(guess, shotDistance) <= 0.2) {
            if (counterFind > 3) break
            solutions.add(guess)
            counterFind++
            cont = true
        } else if (counterFind >= 1 && !cont) {
            break
        }
    }

    if (solutions.isNotEmpty()) {
        val ySolutions = solutions.map { yFunByX(it) }
        val yValue = ySolutions[0]
        val xValue = solutions[0]
        return Pair(xValue, yValue)
    } else {
        Log.d("calprojectile","findposbyshotdistance before return")
        return Pair(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    }
}

fun calShotPointWithArgs(
    theta0: Double, targetPos: Pair<Double, Double>, distanceHandToEye: Double, eyeToAxis: Double,
    shotDistance: Double, shotDoorWidth: Double = 0.04, powerScala: Double = 1.0
): Double {
    if (targetPos.first != Double.POSITIVE_INFINITY) {
        val targetX = targetPos.first
        val targetY = targetPos.second
        val thetaRad = Math.toRadians(theta0)
        val slope = tan(thetaRad)

        val (x0, y0) = getSegmentEndpoints(
            0.0, 0.0, -distanceHandToEye,
            { x -> slope * x }, slope
        ).second

        val eyeOnAxisLine = perpendicularLineEquation(slope, x0, y0)
        val perpSlope = perpendicularSlopeAtPoint(slope)
        val (eyeX0, eyeY0) = getSegmentEndpoints(x0, y0, eyeToAxis, eyeOnAxisLine, perpSlope).second

        val lineEq = createLineLambda(eyeX0, eyeY0, targetX, targetY)

        val (shotLineSlope, shotLineIntercept) = calculateSlopeIntercept(Pair(eyeX0, eyeY0), Pair(targetX, targetY))
        val (interactX, interactY) = findIntersection(perpendicularSlopeAtPoint(slope), 0.0, shotLineSlope, shotLineIntercept)

        val shotHeadPos = 0.025 + shotDoorWidth / 2 - (interactY / cos(thetaRad))
        Log.d("calprojectile",("shotHeadPos: $shotHeadPos"))
        return shotHeadPos
    } else {
        return 0.0
    }
}

fun calyatx_(poss: List<Position>, x: Double): Pair<Double, Double> {
    val absoluteDiff = poss.map { abs(it.x - x) }
    val indexMinDiff = absoluteDiff.indexOf(absoluteDiff.minOrNull())
    val closestValueX = poss[indexMinDiff].x
    val closestValueY = poss[indexMinDiff].y
    return Pair(closestValueX, closestValueY)
}

fun initDistanceAndTrajectory(
    r: Double, v0: Double, theta0: Double, r0: Double, shotDistance: Double
): Pair<Double, Double> {
    val positions = calplg(r, v0, theta0, r0)
    return findPosByShotDistance(theta0, positions, shotDistance)
}

fun run(
    tpos: Pair<Double, Double>, theta0: Double, distanceHandToEye: Double, eyeToAxis: Double,
    shotDistance: Double, shotDoorWidth: Double = 0.04, powerScala: Double = 1.0
): Double {
    Log.d("calprojectile",("run start"))
    val positionOnShotHead = calShotPointWithArgs(theta0, tpos, distanceHandToEye, eyeToAxis, shotDistance, shotDoorWidth, powerScala)
    Log.d("calprojectile",("calshotpointwithargs end"))
    return positionOnShotHead
}
