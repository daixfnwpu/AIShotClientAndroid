package com.ai.aishotclientkotlin.engine.opencv

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

object Conture {

    fun findContours(bitmap: android.graphics.Bitmap, ): Bitmap {
        val mat = Mat() // OpenCV 的 Mat 对象
        Utils.bitmapToMat(bitmap, mat) // 将 bitmap 转换为 Mat

// 转换为灰度图像
        val grayMat = Mat()
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY)

// 应用高斯模糊来减少噪声
        Imgproc.GaussianBlur(grayMat, grayMat, Size(5.0, 5.0), 0.0)

// 使用自适应阈值进行二值化
        val binaryMat = Mat()
        Imgproc.adaptiveThreshold(
            grayMat, binaryMat, 255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV, 11, 2.0
        )
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            binaryMat, contours, hierarchy,
            Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE
        )
// 在原始图像上绘制轮廓
        Imgproc.drawContours(mat, contours, -1, Scalar(0.0, 255.0, 0.0), 3)
        for (contour in contours) {
            val peri = Imgproc.arcLength(MatOfPoint2f(*contour.toArray()), true)
            val approx = MatOfPoint2f()
            Imgproc.approxPolyDP(MatOfPoint2f(*contour.toArray()), approx, 0.02 * peri, true)

            // 判断是否为皮筋形状，进行进一步筛选
            if (Imgproc.contourArea(contour) > 1000 && approx.toArray().size > 5) {
                // 这是一个可能的皮筋轮廓
                Imgproc.drawContours(mat, listOf(contour), -1, Scalar(0.0, 0.0, 255.0), 3)
            }
        }
        val resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, resultBitmap)
        //imageView.setImageBitmap(resultBitmap)
        return resultBitmap;

    }
}