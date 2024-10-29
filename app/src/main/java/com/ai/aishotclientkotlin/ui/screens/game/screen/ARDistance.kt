package com.ai.aishotclientkotlin.ui.screens.game.screen

import android.graphics.PointF
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ai.aishotclientkotlin.engine.ar.ARPointEngine
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Pose
import com.google.ar.core.Session
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.math.Position
import kotlin.math.sqrt

//https://github.com/SceneView/sceneview-android/blob/2bed398b3e10e8e9737d6e4a38933e783c1ee75e/samples/ar-cloud-anchor/src/main/java/io/github/sceneview/sample/arcloudanchor/MainFragment.kt


@Composable
fun ARComposeView(screenPoint: PointF, onDistanceMeasured: (Float) -> Unit) {
    val lcycle =LocalLifecycleOwner.current.lifecycle

    AndroidView(
        factory = { context ->
            // 初始化 ARSceneView
            val arSceneView = ARSceneView(context)
            val arPointEngine: ARPointEngine = ARPointEngine(arSceneView)
            // 设置相机帧更新监听器
            arSceneView.apply {
                lifecycle=lcycle
                planeRenderer.isEnabled = true
                sessionConfiguration = { session, config ->
                    config.depthMode = when(session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        false -> Config.DepthMode.DISABLED
                    }
                    config.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                    config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                }
                onSessionUpdated = { session,frameTime ->
                        val frame: Frame? = arSceneView.frame
                        frame?.let {
                            // 在屏幕中心进行 hitTest
                            val hitResultList = frame.hitTest(0.5f, 0.5f)

                            if (hitResultList.isNotEmpty()) {
                                val hitResult = hitResultList[0]
                                val pose = hitResult.hitPose

                                // 获取相机的 pose
                                val cameraPose = frame.camera.pose

                                // 计算相机与物体的距离
                                val distance = cameraPose.distance(pose)
                                onDistanceMeasured(distance)
                            }else {
                                val distance = arPointEngine.get3DDistanceBetweenCameraAndObjectByScreen(screenPoint)
                                onDistanceMeasured(distance)
                            }
                        }
                }
            }
            arSceneView
        },
        modifier = Modifier.fillMaxSize()
    )
}


// 扩展函数：计算两个Pose之间的距离
fun Pose.distance(other: Pose): Float {
    val dx = this.tx() - other.tx()
    val dy = this.ty() - other.ty()
    val dz = this.tz() - other.tz()
    return sqrt(dx * dx + dy * dy + dz * dz)
}

// 扩展函数：计算两个Pose之间的距离
fun Pose.distance(other: Position): Float {
    val dx = this.tx() - other.x
    val dy = this.ty() - other.y
    val dz = this.tz() - other.z
    return sqrt(dx * dx + dy * dy + dz * dz)
}
@Composable
fun ARMeasurementScreen() {
    var distance by remember { mutableStateOf(0f) }
    var shotPointOnScreen by remember {
        mutableStateOf(PointF(0f,0f))
    }
    Column(modifier = Modifier.fillMaxSize()) {
        // 显示AR场景
        ARComposeView(shotPointOnScreen) { measuredDistance ->
            distance = measuredDistance
        }

        // 显示距离
        Text(
            text = "Distance: ${String.format("%.2f", distance)} meters",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}
// 设置 ARCore Session
fun ARSceneView.setupSession(session: Session) {
    this.setupSession(session)
}
