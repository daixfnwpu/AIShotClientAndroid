package com.ai.aishotclientkotlin.engine

import kotlin.math.*

// Data class for Position
data class Position(val x: Double, val y: Double, val vx: Double, val vy: Double, val t: Double)

// Constants
const val PI = 3.141592653589793
const val G = 9.81 // Gravity constant (m/s^2)
const val CD_AIR = 0.47 // Drag coefficient (typical for a sphere)
const val RHO = 1.225 // Air density (kg/m^3)
const val SHOT_HEAD_WIDTH = 0.025

// Function to calculate air resistance
fun dragForce(va: Double, A: Double): Double {
    return 0.5 * CD_AIR * RHO * A * va * va
}

// Calculate the projectile trajectory
fun calculateTrajectory(r: Double, v0: Double, theta0: Double, r0: Double): List<Position> {
    val A = PI * r * r // Cross-sectional area (m^2)
    val m = r0 * 4 * PI * r * r * r * 1000 / 3 // Mass (kg), density 2.5 g/cm^3
    val thetaRad = theta0 * PI / 180.0
    val v0x = v0 * cos(thetaRad)
    val v0y = v0 * sin(thetaRad)
    val dt = 0.001 // Time step (s)

    val positions = mutableListOf(Position(0.0, 0.0, v0x, v0y, 0.0))

    var x = 0.0
    var y = 0.0
    var vx = v0x
    var vy = v0y
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

// Calculate slope and intercept
fun calculateSlopeIntercept(p1: Pair<Double, Double>, p2: Pair<Double, Double>): Pair<Double, Double> {
    if (p1.first == p2.first) {
        println("The points are vertical, cannot define a unique slope.")
        return 0.0 to 0.0
    }
    val slope = (p2.second - p1.second) / (p2.first - p1.first)
    val intercept = p1.second - slope * p1.first
    return slope to intercept
}

// Find intersection of two lines
fun findIntersection(m1: Double, b1: Double, m2: Double, b2: Double): Pair<Double, Double> {
    if (m1 == m2) {
        return Double.POSITIVE_INFINITY to Double.POSITIVE_INFINITY
    }
    val x = (b2 - b1) / (m1 - m2)
    val y = m1 * x + b1
    return x to y
}

// Calculate perpendicular slope at a given point
fun perpendicularSlopeAtPoint(slope: Double): Double {
    return when {
        slope == 0.0 -> Double.POSITIVE_INFINITY
        slope.isInfinite() -> 0.0
        else -> -1 / slope
    }
}

// Perpendicular line equation at a given point
fun perpendicularLineEquation(slope: Double, x1: Double, y1: Double): (Double) -> Double {
    val perpSlope = perpendicularSlopeAtPoint(slope)
    return { x: Double -> perpSlope * (x - x1) + y1 }
}

// Calculate end points of a line segment
fun getSegmentEndpoints(
    x1: Double, y1: Double, segmentLength: Double, perpLineEq: (Double) -> Double, slope: Double
): Pair<Pair<Double, Double>, Pair<Double, Double>> {
    val direction = if (slope >= 0) 1 else -1
    val cosTheta = 1 / sqrt(1 + slope.pow(2))
    val xEnd = x1 + direction * segmentLength * cosTheta
    val yEnd = perpLineEq(xEnd)

    return (x1 to y1) to (xEnd to yEnd)
}

// Create line equation (lambda)
fun createLineLambda(x1: Double, y1: Double, x2: Double, y2: Double): (Double) -> Double {
    val (m, b) = calculateSlopeIntercept(x1 to y1, x2 to y2)
    return { x: Double -> m * x + b }
}

// Calculate position at a given shot distance
fun findPosByShotDistance(arg: Double, poss: List<Position>, shotDistance: Double): Pair<Double, Double> {
    fun yFunByX(xVal: Double): Double {
        val position = poss.minByOrNull { abs(it.x - xVal) }
        return position?.y ?: 0.0
    }

    fun equation(x: Double, r: Double): Double {
        val y = yFunByX(x)
        return abs(sqrt(x.pow(2) + y.pow(2)) - r)
    }

    val thetaRad = arg * PI / 180.0
    val initXGuess = shotDistance * cos(thetaRad)
    val constGuess = 10 * cos(thetaRad)

    val initialGuesses = List(100) { initXGuess - constGuess + it * (2 * constGuess / 100) }

    val solutions = initialGuesses.filter { guess -> equation(guess, shotDistance) <= 0.2 }

    return if (solutions.isNotEmpty()) {
        val ySolutions = solutions.map { yFunByX(it) }
        solutions.first() to ySolutions.first()
    } else {
        Double.POSITIVE_INFINITY to Double.POSITIVE_INFINITY
    }
}

// Calculate shot point
fun calculateShotPointWithArgs(
    theta0: Double, targetPos: Pair<Double, Double>, distanceHandToEye: Double,
    eyeToAxis: Double, shotDistance: Double, shotDoorWidth: Double = 0.04, powerScala: Double = 1.0
): Double {
    if (targetPos.first != Double.POSITIVE_INFINITY) {
        val (targetX, targetY) = targetPos
        val thetaRad = (theta0 * PI) / 180.0
        val slope = tan(thetaRad)

        val (x0, y0) = getSegmentEndpoints(0.0, 0.0, -distanceHandToEye, { x -> slope * x }, slope).second
        val eyeOnAxisLine = perpendicularLineEquation(slope, x0, y0)
        val (eyeX0, eyeY0) = getSegmentEndpoints(x0, y0, eyeToAxis, eyeOnAxisLine, slope).second

        val shotLineSlope = (targetY - eyeY0) / (targetX - eyeX0)
        val shotLineIntercept = eyeY0 - shotLineSlope * eyeX0
        val (intersectX, intersectY) = findIntersection(slope, 0.0, shotLineSlope, shotLineIntercept)

        return SHOT_HEAD_WIDTH + shotDoorWidth / 2 - intersectY / cos(thetaRad)
    }
    return 0.0
}

// Calculate trajectory and distance intersection
fun initDistanceAndTrajectory(r: Double, v0: Double, theta0: Double, r0: Double, shotDistance: Double): Pair<Double, Double> {
    val positions = calculateTrajectory(r, v0, theta0, r0)
    return findPosByShotDistance(theta0, positions, shotDistance)
}

fun main() {
    // Example values
    val r = 0.05 // Radius (meters)
    val v0 = 10.0 // Initial velocity (m/s)
    val theta0 = 45.0 // Angle (degrees)
    val r0 = 2.5 // Density (g/cm^3)
    val shotDistance = 1.0 // Distance of the shot

    // Calculate trajectory and intersection point
    val (x, y) = initDistanceAndTrajectory(r, v0, theta0, r0, shotDistance)

    println("X: $x, Y: $y")
}
