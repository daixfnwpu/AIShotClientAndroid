package com.ai.aishotclientkotlin.engine.ar


import android.animation.ValueAnimator
import android.graphics.PointF
import android.opengl.Matrix
import android.util.Log
import android.view.MotionEvent
import com.ai.aishotclientkotlin.ui.screens.shot.screen.game.distance
import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.collision.Vector3
import io.github.sceneview.math.Position
import io.github.sceneview.math.toFloat3
import io.github.sceneview.math.toVector3
import io.github.sceneview.node.ModelNode
import kotlin.math.pow
import kotlin.math.sqrt

class ARPointEngine(
    private val arSceneView: ARSceneView) {
    private lateinit var frame: Frame
    private fun placeShotObjectAtPosition(hitResult: HitResult, position: Position? = null) {
        val anchor = hitResult.createAnchor()
        addAnchorNode(anchor)
        if (position != null) {
            addDirectNode(position)
        }
    }

    var itimes = 1.0f;

    fun addDirectNode(position: Position?){

        val pointCloudNode = buildModelNode("models/point_cloud.glb")?.apply {
            if (position != null) {
                this.position = position
            }
        }

        if (pointCloudNode != null) {
            arSceneView.addChildNode(pointCloudNode)
            Log.e("AR","${position.toString()}, ${position!!.x},${position.y},${position.z}")
        }
    }

    private fun moveModelFromStartToEnd(modelNode: ModelNode,startPos: Position,endPos: Position) {
        // Make sure both the model and end position are available
        if (modelNode == null || startPos == null || endPos == null) return

        // Get the start and end positions from the anchors
//        val startPosition = Vector3(
//            startAnchor!!.pose.tx(),
//            startAnchor!!.pose.ty(),
//            startAnchor!!.pose.tz()
//        )
//
//        val endPosition = Vector3(
//            endAnchor!!.pose.tx(),
//            endAnchor!!.pose.ty(),
//            endAnchor!!.pose.tz()
//        )

        // Animate movement from start to end using ValueAnimator
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 3000  // Duration in milliseconds
        animator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction
            // Interpolate the position between start and end
            val newPosition = Vector3.lerp(startPos.toVector3(), endPos.toVector3(), fraction)
            // Update model's position to the interpolated value
            modelNode!!.position = newPosition.toFloat3()
        }

        // Start the animation
        animator.start()
    }



    fun addAnchorNode(anchor: Anchor) {
        // TODO : this node must anchorable;
        arSceneView.addChildNode(
            AnchorNode(arSceneView.engine, anchor)
                .apply {
                    isEditable = true

                    buildModelNode("models/point_cloud.glb")?.let { addChildNode(it) }
//                        buildViewNode()?.let { addChildNode(it) }
                    // anchorNode = this
                }
        )


    }


    fun buildModelNode(modelFileName:String): ModelNode? {
        arSceneView.modelLoader.createInstancedModel(
            assetFileLocation = modelFileName, count = 1
        )[0]?.let { modelInstance ->
            return ModelNode(
                modelInstance = modelInstance,
                // Scale to fit in a 0.5 meters cube
                scaleToUnits = 0.05f,
                // Bottom origin instead of center so the model base is on floor
                centerOrigin = Position(y = -0.05f)
            ).apply {
                isEditable = true
            }
        }
        return null
    }
    init {
        arSceneView.setOnTouchListener { hitTestResult, motionEvent ->
            // 获取当前帧
            //
            // val frame = arFragment.arSceneView.arFrame
            frame = arSceneView.frame!!

            val p = Position(x= 4.17615F * itimes, y= (-1.3344656).toFloat() * itimes, z= (-1.8140525).toFloat() * itimes)
            addDirectNode(p)
            itimes = itimes * 1.1f

            if (frame != null && motionEvent != null && motionEvent.action == MotionEvent.ACTION_UP) {
                val hitResultList = frame?.hitTest(motionEvent)

                // 如果有有效的点击结果
                if (hitResultList != null) {
                    for (hit in hitResultList) {
                        val trackable = hit.trackable
                        // 检查是否点击在了某个检测到的平面上
                        if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                            Log.e("AR",hit.toString())
                            placeShotObjectAtPosition(hit)  // 放置3D对象
                            break
                        }else
                        {
                            Log.e("AR","${hit.toString()} ,${hit.distance} is not on trackable")
                            placeShotObjectAtPosition(hit)
                            val p = getShotObject3DPositionFromScreen(hit.hitPose.position.x,hit.hitPose.position.y)
                            Log.e("AR","${p.toString()}, ${p.x},${p.y},${p.z}")
                        }

                        placeShotObjectAtPosition(hit)
                        val p = getShotObject3DPositionFromScreen(hit.hitPose.position.x,hit.hitPose.position.y)
                        placeShotObjectAtPosition(hit,p)
                    }
                }
            }
            true
        }
    }

    fun get3DDistanceBetweenCameraAndObjectByScreen(point: PointF): Float {
        val cameraPose = frame.camera.pose

        // 计算相机与物体的距离
        val distance = cameraPose.distance(getShotObject3DPositionFromScreen(point.x,point.y))
        return distance
    }

    fun getShotObject3DPositionFromScreen(x2D: Float, y2D: Float): Position {
        // val frame = arview.arFrame
        val pointCloud = frame?.acquirePointCloud()

        var closestPoint: Position? = null
        var minDistance = Float.MAX_VALUE

        pointCloud?.let {
            val pointsBuffer = pointCloud.points
            val idsBuffer = pointCloud.ids
            val pointsSize = idsBuffer.limit()
            val ids = mutableListOf<Int>()
            for (index in 0 until pointsSize) {
                val id = idsBuffer[index]
                ids += id
                val pointIndex = index * 4
                val position = Position(
                    pointsBuffer[pointIndex],
                    pointsBuffer[pointIndex + 1],
                    pointsBuffer[pointIndex + 2]
                )
                // 将3D点投影到2D屏幕，找到与2D坐标最接近的点
                val screenPoint = worldToScreen(position.x, position.y, position.z)
                val distance = sqrt(
                    ((x2D - screenPoint.x).toDouble().pow(2.0) + (y2D - screenPoint.y).toDouble()
                        .pow(2.0))
                ).toFloat()

                if (distance < minDistance) {
                    minDistance = distance
                    closestPoint = Position(position.x, position.y, position.z)
                }
            }
            pointCloud.release()
        }

        return closestPoint ?: Position(0f, 0f, 0f)
    }

    private fun worldToScreen(x: Float, y: Float, z: Float): Position {
        val projectionMatrix = FloatArray(16)
        val viewMatrix = FloatArray(16)
        val modelViewProjectionMatrix = FloatArray(16)
        frame?.camera?.getProjectionMatrix(projectionMatrix, 0, 0.1f, 100f)
        frame?.camera?.getViewMatrix(viewMatrix, 0)
        // 计算模型视图投影矩阵 (MVP = Projection * View)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // 世界坐标 (x, y, z, 1.0f)
        val worldCoords = floatArrayOf(x, y, z, 1.0f)
        val screenCoords = FloatArray(4)

        // 将世界坐标转换为屏幕坐标 (乘以 MVP 矩阵)
        Matrix.multiplyMV(screenCoords, 0, modelViewProjectionMatrix, 0, worldCoords, 0)

        // 进行透视除法
        return if (screenCoords[3] != 0f) {
            Position(screenCoords[0] / screenCoords[3], screenCoords[1] / screenCoords[3])
        } else {
            Position(Float.NaN, Float.NaN) // 错误情况：防止除以零
        }
    }
}