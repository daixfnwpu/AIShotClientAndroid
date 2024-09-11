package com.ai.aishotclientkotlin.engine.opencv

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

object Conture {


    fun getContours(bitmap: android.graphics.Bitmap,
                    lowerBound: Scalar = Scalar(23.0, 109.0, 19.0),
                    upperBound: Scalar = Scalar(76.0, 255.0, 255.0),
                    maxArea: Int = 20000) :Bitmap{

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
                Imgproc.approxPolyDP(MatOfPoint2f(*contour.toArray()), approx, 0.02 * peri, true)

                // Draw bounding rectangle around the detected object
                val rect = Imgproc.boundingRect(MatOfPoint(*approx.toArray()))
                Imgproc.rectangle(mat, rect, Scalar(0.0, 255.0, 0.0), 5)
                Imgproc.drawContours(mat, listOf(contour), -1, Scalar(0.0, 0.0, 255.0), 3)
            }
        }
        val resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, resultBitmap)
        return resultBitmap;

    }

}