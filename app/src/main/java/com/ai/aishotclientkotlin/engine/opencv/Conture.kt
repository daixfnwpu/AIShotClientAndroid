package com.ai.aishotclientkotlin.engine.opencv

import android.graphics.Bitmap
import com.ai.aishotclientkotlin.engine.shot.IsoscelesTriangle
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.roundToInt


class Conture constructor(private val bitmap: Bitmap,
    private val lowerBound: Scalar = Scalar(23.0, 109.0, 19.0),
    private val upperBound: Scalar = Scalar(76.0, 255.0, 255.0),
    private val maxArea: Int = 20000,
    private val epsilon :Float = 0.005f) {

    private val IMAGE_WIDTH : Int = 640
//    private val lowerBound: Scalar = Scalar(23.0, 109.0, 19.0)
//    private val upperBound: Scalar = Scalar(76.0, 255.0, 255.0)
//    private val maxArea: Int = 20000
//    private val epsilon :Float = 0.015f
    private  var contureBitmap :Bitmap? = null;
    private var conturePoints : List<MatOfPoint>?  = null
    fun getContourImage( ) : Bitmap? {
        return contureBitmap;
    }
    fun getImageWidth() : Int {
        return contureBitmap?.width ?: IMAGE_WIDTH
    }
    fun getPointsOfContours() : List<IsoscelesTriangle.Point>? {
        val matOfPoints  = getContours()
        val points = matOfPoints?.get(0)?.toList()?.map { mp ->
            IsoscelesTriangle.Point(mp.x.roundToInt(), mp.y.roundToInt())
        }
        return points

    }
    fun getContours( ) : List<MatOfPoint>? {

        // Convert the image to OpenCV's Mat object (BGR format)
        val mat = Mat() // OpenCV 的 Mat 对象
        Utils.bitmapToMat(bitmap, mat) // 将 bitmap 转换为 Mat


        // Convert RGB to HSV for color detection
        val hsvMat = Mat()
        Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV)


        // 应用高斯模糊来减少噪声
        Imgproc.GaussianBlur(hsvMat, hsvMat, Size(5.0, 5.0), 0.0)


        // Create mask with inRange
        val mask = Mat()
        Core.inRange(hsvMat, lowerBound, upperBound, mask)
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))

        //进行开运算
        Imgproc.morphologyEx(hsvMat, hsvMat, Imgproc.MORPH_OPEN, kernel)

        //进行闭运算
        Imgproc.morphologyEx(hsvMat, hsvMat, Imgproc.MORPH_CLOSE, kernel)
        // Find contours
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_KCOS)
       // Imgproc.drawContours(mat, contours, -1, Scalar(0.0, 255.0, 0.0), 3)
        for (contour in contours) {
            val area = Imgproc.contourArea(contour)
            if (area > maxArea) {
                // Process the contour
                val peri = Imgproc.arcLength(MatOfPoint2f(*contour.toArray()), true)
                val approx = MatOfPoint2f()
                Imgproc.approxPolyDP(MatOfPoint2f(*contour.toArray()), approx, epsilon * peri, true)
                conturePoints=  listOf(MatOfPoint(*approx.toArray()))
                // Draw bounding rectangle around the detected object
                val rect = Imgproc.boundingRect(MatOfPoint(*approx.toArray()))
                Imgproc.rectangle(mat, rect, Scalar(0.0, 255.0, 0.0), 5)
                Imgproc.drawContours(mat, conturePoints, -1, Scalar(0.0, 0.0, 255.0), 3)

            }
        }
        val resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, resultBitmap)
        contureBitmap =resultBitmap;
        return conturePoints
    }

}