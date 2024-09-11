package com.ai.aishotclientkotlin.engine

import kotlin.math.*


object IsoscelesTriangle {
    // if points number is less than 6:
    //find the fix points ;

    fun findAdjustDirection(indices: List<Point>, width: Int,rubberWidthByPixel: Int = 50 ): RubberDirection {
        val means_y = indices.map { p -> p.y }.average()
        var y_smalls = indices.filter { it.y < means_y }
        var y_larges = indices.filter { it.y > means_y }
        val means_x = indices.map { p -> p.x }.average()
        y_larges = y_larges.sortedBy { it.x }
        val maxY = y_larges.maxByOrNull { it.y }?.y
        y_larges = y_larges.filter { it.y == maxY }
        val p11 = y_larges.get(1)
        val p12 = y_larges.get(2)

        y_smalls = y_smalls.sortedBy { it.x }

        val minDifferenceX = y_smalls.minByOrNull { Math.abs(it.x - means_x) }?.let { Math.abs(it.x - means_x) }
        /// !!! TODO 这里有bug，没有筛选到想要的点。
        // 筛选出 x 值与目标值差等于最小差值的所有元素
        if (minDifferenceX!=null)
            y_smalls = y_smalls.filter { (Math.abs(it.x - means_x) -  minDifferenceX) < rubberWidthByPixel}

        // 在这些元素中找到 y 值最小的元素
        val minY = y_smalls.minByOrNull { it.y }?.y

        // 筛选出 y 值等于最小 y 值的所有元素
        y_smalls = y_smalls.filter { it.y == minY }

        var k: Double = Double.POSITIVE_INFINITY
        // !!TODO need judge the number of the list;
        if (y_smalls.size <= 3) {
            val p01_02 = y_smalls.maxByOrNull { it.y }

            val M1 = p01_02
            val M2 = midpoint(p11, p12)

            // 计算 M1 和 M2 连线的斜率
            k = slope(M1!!, M2)
        }
        else
        {
            // 找到 x 值与目标值差最小的绝对值


            val p01 = y_smalls.get(1)
            val p02 = y_smalls.get(2)
            // line is: p01 ---- p11
            // line2 is : p02 ---- P 12
            // 计算两条直线的中点
            val M1 = midpoint(p01, p02)
            val M2 = midpoint(p11, p12)

            // 计算 M1 和 M2 连线的斜率
            k = slope(M1, M2)
        }
        // 计算对称轴的斜率
        val kAxis = if (k != 0.0)
            -1.0f / k
        else
            Double.POSITIVE_INFINITY
        if (kAxis == Double.POSITIVE_INFINITY)
            return RubberDirection()
        else if (kAxis > 0)
            return RubberDirection(top = 1)
        else
            return RubberDirection(down = 1)

    }

    // 计算两个点的中点
    fun midpoint(p1: Point, p2: Point): Point {
        return Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
    }

    // 计算两个点之间的斜率
    fun slope(p1: Point, p2: Point): Double {
        return if (p1.x != p2.x) {
            (p2.y - p1.y).toDouble() / (p2.x - p1.x)
        } else {
            Double.POSITIVE_INFINITY // 垂直线的斜率
        }
    }

    // Data classes for Line, Point, RubberDirection
    data class Point(val x: Int, val y: Int)
    data class Line(val slope: Double, val intercept: Double)
    data class RubberDirection(var top: Int = 0, var down: Int = 0)

    // Check if colors are similar (this will depend on how RGB is structured in Kotlin)
    data class Pixel(val r: Int, val g: Int, val b: Int)
    data class RGB(
        val rmin: Int,
        val rmax: Int,
        val gmin: Int,
        val gmax: Int,
        val bmin: Int,
        val bmax: Int
    )

    fun colorSimilar(p: Pixel, b: RGB): Boolean {
        return (p.r in b.rmin..b.rmax) && (p.g in b.gmin..b.gmax) && (p.b in b.bmin..b.bmax)
    }

    // Find the intersection point of two lines
    private fun findIntersection(l1: Line, l2: Line): Point {
        val x = (l2.intercept - l1.intercept) / (l1.slope - l2.slope)
        val y = l1.slope * x + l1.intercept
        return Point(x.toInt(), y.toInt())
    }

    // Find the slope of the symmetry axis
    private fun findSymmetryAxisSlope(l1: Line, l2: Line): Double {
        return (l1.slope + l2.slope) / (1 - l1.slope * l2.slope)
    }

    // Find the intercept of the symmetry axis
    private fun findSymmetryAxisIntercept(slope: Double, intersection: Point): Double {
        return intersection.y - slope * intersection.x
    }

    // Calculate perpendicular distance from a point to a line
    private fun perpendicularDistance(line: Line, point: Point): Float {
        val px = point.x.toFloat()
        val py = point.y.toFloat()
        val a = -line.slope.toFloat()
        val b = 1f
        val c = -line.intercept.toFloat()

        val numerator = abs(a * px + b * py + c)
        val denominator = sqrt(a * a + b * b)
        return numerator / denominator
    }

    // Calculate perpendicular distance to the axis
    private fun perpendicularDistanceByAxis(point: Point, l1: Line, l2: Line): Float {
        val axisLine = Line(
            findSymmetryAxisSlope(l1, l2),
            findSymmetryAxisIntercept(findSymmetryAxisSlope(l1, l2), findIntersection(l1, l2))
        )
        return perpendicularDistance(axisLine, point)
    }

    // Function to check if a value is in an array
    private fun inarr(i: Int, vals: IntArray): Boolean {
        return vals.contains(i)
    }

    // Convert radians to degrees
    private fun degrees(radians: Float): Float {
        return radians * 180.0f / 3.1415926f
    }

    // Core function that simulates the behavior of the original algorithm
    public fun linearLines(indices: List<Point>, height: Int): RubberDirection {
        val absdiff = IntArray(4)
        val intercept = IntArray(4)
        val pixels = Array(height) { IntArray(5) { -1 } }

        for (ind in indices) {
            val y = ind.y
            val indexY = pixels[y][0]
            if (indexY == 4) continue
            pixels[y][indexY + 1] = ind.x
            pixels[y][0]++
        }

        val points = Array(4) { Array(height) { Point(0, 0) } }
        for (j in 1..4) {
            var ysize = 0
            for (i in 0 until height) {
                if (pixels[i][0] == 4) {
                    val p = Point(pixels[i][j], i)
                    points[j - 1][ysize] = p
                    ysize++
                }
            }

            val linearPoints = points[j - 1]
            // Assume linearRegression and other helper methods are defined
            val bestFitLine = linearRegression(linearPoints, ysize)
            val residuals = DoubleArray(ysize)
            calculateResiduals(linearPoints, ysize, bestFitLine, residuals)
            val meanResidual = calculateMean(residuals, ysize)
            val stdDevResidual = calculateStdDev(residuals, ysize, meanResidual)
            val threshold = meanResidual + 2 * stdDevResidual
            val filteredPoints =
                linearPoints.filterIndexed { idx, _ -> residuals[idx] < threshold }.toTypedArray()
            val filteredBestFitLine = linearRegression(filteredPoints, filteredPoints.size)

            absdiff[j - 1] = filteredBestFitLine.slope.toInt()
            intercept[j - 1] = filteredBestFitLine.intercept.toInt()
        }

        val a1a4 = abs(absdiff[0]) - abs(absdiff[3])
        val angle1 = degrees(atan(abs(absdiff[0]).toFloat()))
        val angle4 = degrees(atan(abs(absdiff[3]).toFloat()))
        val needAdjust = angle1 - angle4

        val rd = RubberDirection()
        if (a1a4 > 0) {
            rd.top = a1a4
        } else {
            rd.down = a1a4
        }

        val topLine = Line(absdiff[0].toDouble(), intercept[0].toDouble())
        val bottomLine = Line(absdiff[3].toDouble(), intercept[3].toDouble())

        val eye = Point(400, 600)
        val distance = perpendicularDistance(topLine, eye)
        println("For test, the distance between the eye and topLine is: $distance")

        val axisDistance = perpendicularDistanceByAxis(eye, topLine, bottomLine)
        println("For test, the distance between the eye and axisLine is: $axisDistance")

        return rd
    }

    // Linear regression function for a set of points
    private fun linearRegression(points: Array<Point>, size: Int): Line {
        var sumX = 0.0
        var sumY = 0.0
        var sumXY = 0.0
        var sumX2 = 0.0
        val N = size

        for (i in 0 until size) {
            val point = points[i]
            sumX += point.x
            sumY += point.y
            sumXY += point.x * point.y
            sumX2 += point.x * point.x
        }

        val slope = (N * sumXY - sumX * sumY) / (N * sumX2 - sumX * sumX)
        val intercept = (sumY * sumX2 - sumX * sumXY) / (N * sumX2 - sumX * sumX)

        return Line(slope, intercept)
    }

    // Calculate residuals for linear regression
    private fun calculateResiduals(
        points: Array<Point>,
        size: Int,
        line: Line,
        residuals: DoubleArray
    ) {
        for (i in 0 until size) {
            val yPred = line.slope * points[i].x + line.intercept
            residuals[i] = abs(points[i].y - yPred)
        }
    }

    // Calculate the mean of an array of values
    private fun calculateMean(values: DoubleArray, size: Int): Double {
        var sum = 0.0
        for (i in 0 until size) {
            sum += values[i]
        }
        return sum / size
    }

    // Calculate the standard deviation of an array of values
    private fun calculateStdDev(values: DoubleArray, size: Int, mean: Double): Double {
        var sum = 0.0
        for (i in 0 until size) {
            sum += (values[i] - mean).pow(2)
        }
        return sqrt(sum / size)
    }

    // Remove outliers based on residuals and threshold
    private fun removeOutliers(
        points: Array<Point>,
        size: Int,
        residuals: DoubleArray,
        threshold: Double,
        filteredPoints: Array<Point>
    ): Int {
        var j = 0
        for (i in 0 until size) {
            if (residuals[i] <= threshold) {
                filteredPoints[j++] = points[i]
            }
        }
        return j
    }


}