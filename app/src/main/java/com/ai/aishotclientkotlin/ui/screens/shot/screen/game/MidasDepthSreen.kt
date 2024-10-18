package com.ai.aishotclientkotlin.ui.screens.shot.screen.game

import android.graphics.Bitmap
import io.github.sceneview.collision.Vector3
import com.google.ar.sceneform.rendering.Color



val maxDepth : Int = 20
// SceneView中的方法
private fun generatePointCloudWithTexture(
    depthMap: Bitmap,
    colorImage: Bitmap,
    fx: Float, fy: Float, cx: Float, cy: Float
): List<Pair<Vector3, Color>> {
    val pointCloud = mutableListOf<Pair<Vector3, Color>>()
    for (y in 0 until depthMap.height) {
        for (x in 0 until depthMap.width) {
            val pixelDepth = depthMap.getPixel(x, y) and 0xFF
            val depthInMeters = pixelDepth / 255.0f * maxDepth

            val worldX = (x - cx) * depthInMeters / fx
            val worldY = (y - cy) * depthInMeters / fy
            val worldZ = depthInMeters

            // 获取颜色图像中的颜色
            val pixelColor = colorImage.getPixel(x, y)
            val color = Color(
                android.graphics.Color.red(pixelColor) / 255.0f,
                android.graphics.Color.green(pixelColor) / 255.0f,
                android.graphics.Color.blue(pixelColor) / 255.0f
            )

            pointCloud.add(Pair(Vector3(worldX, worldY, worldZ), color))
        }
    }
    return pointCloud
}

