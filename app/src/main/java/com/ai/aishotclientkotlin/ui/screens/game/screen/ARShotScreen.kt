package com.ai.aishotclientkotlin.ui.screens.game.screen

import android.graphics.PointF
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ai.aishotclientkotlin.engine.ar.ARPointEngine
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Pose
import com.google.ar.core.Session
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.math.Position
import kotlin.math.sqrt
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.ui.theme.AIShotClientKotlinTheme
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView

//https://github.com/SceneView/sceneview-android/blob/2bed398b3e10e8e9737d6e4a38933e783c1ee75e/
// samples/ar-model-viewer-compose/src/main/java/io/github/sceneview/sample/armodelviewer/compose/MainActivity.kt
fun createAnchorNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelInstances: MutableList<ModelInstance>,
    anchor: Anchor
): AnchorNode {
    val kModelFile = "models/damaged_helmet.glb"
    val kMaxModelInstances = 10
    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
    val modelNode = ModelNode(
        modelInstance = modelInstances.apply {
            if (isEmpty()) {
                this += modelLoader.createInstancedModel(kModelFile, kMaxModelInstances)
            }
        }.removeLast(),
        // Scale to fit in a 0.5 meters cube
        scaleToUnits = 0.5f
    ).apply {
        // Model Node needs to be editable for independent rotation from the anchor rotation
        isEditable = true
    }
    val boundingBoxNode = CubeNode(
        engine,
        size = modelNode.extents,
        center = modelNode.center,
        materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
    ).apply {
        isVisible = false
    }
    modelNode.addChildNode(boundingBoxNode)
    anchorNode.addChildNode(modelNode)

    listOf(modelNode, anchorNode).forEach {
        it.onEditingChanged = { editingTransforms ->
            boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
        }
    }
    return anchorNode
}

@Composable
fun ARTempView(){

    AIShotClientKotlinTheme {
        // A surface container using the 'background' color from the theme
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            // The destroy calls are automatically made when their disposable effect leaves
            // the composition or its key changes.
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val materialLoader = rememberMaterialLoader(engine)
            val cameraNode = rememberARCameraNode(engine)
            val childNodes = rememberNodes()
            val view = rememberView(engine)
            val collisionSystem = rememberCollisionSystem(view)

            var planeRenderer by remember { mutableStateOf(true) }

            val modelInstances = remember { mutableListOf<ModelInstance>() }
            var trackingFailureReason by remember {
                mutableStateOf<TrackingFailureReason?>(null)
            }
            var frame by remember { mutableStateOf<Frame?>(null) }
            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                collisionSystem = collisionSystem,
                sessionConfiguration = { session, config ->
                    config.depthMode =
                        when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                            true -> Config.DepthMode.AUTOMATIC
                            else -> Config.DepthMode.DISABLED
                        }
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode =
                        Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                cameraNode = cameraNode,
                planeRenderer = planeRenderer,
                onTrackingFailureChanged = {
                    trackingFailureReason = it
                },
                onSessionUpdated = { session, updatedFrame ->
                    frame = updatedFrame

                    if (childNodes.isEmpty()) {
                        updatedFrame.getUpdatedPlanes()
                            .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                            ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                                childNodes += createAnchorNode(
                                    engine = engine,
                                    modelLoader = modelLoader,
                                    materialLoader = materialLoader,
                                    modelInstances = modelInstances,
                                    anchor = anchor
                                )
                            }
                    }
                },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { motionEvent, node ->
                        if (node == null) {
                            val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                            hitResults?.firstOrNull {
                                it.isValid(
                                    depthPoint = false,
                                    point = false
                                )
                            }?.createAnchorOrNull()
                                ?.let { anchor ->
                                    planeRenderer = false
                                    childNodes += createAnchorNode(
                                        engine = engine,
                                        modelLoader = modelLoader,
                                        materialLoader = materialLoader,
                                        modelInstances = modelInstances,
                                        anchor = anchor
                                    )
                                }
                        }
                    })
            )
            Text(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 32.dp, end = 32.dp),
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                color = Color.White,
                text = trackingFailureReason?.let {
                    it.getDescription(LocalContext.current)
                } ?: if (childNodes.isEmpty()) {
                    stringResource(R.string.point_your_phone_down)
                } else {
                    stringResource(R.string.tap_anywhere_to_add_model)
                }
            )
        }
    }

}

@Composable
fun ARShotView(screenPoint: PointF, onDistanceMeasured: (Float) -> Unit) {
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
        modifier = Modifier.fillMaxSize(),
        update = { arSceneView ->
            // 可以在这里更新或处理arSceneView
        }
    )
}
/*@Composable
fun ARCameraView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        // 显示CameraX预览
        CameraXPreview(
            modifier = Modifier.fillMaxSize()
        )

        // 显示ARSceneView
        ARSceneViewComposable(
            modifier = Modifier.fillMaxSize()
        )
    }
}*/
/*
@Composable
fun ARSceneViewComposable(modifier: Modifier = Modifier) {
    // 在 Compose 环境中获取 Context 和 LifecycleOwner
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            // 创建并初始化 ARSceneView
            val arSceneView = ARSceneView(ctx)
            val session = Session(ctx).apply {
                val config = Config(this).apply {
                    focusMode = Config.FocusMode.AUTO
                }
                configure(config)
            }
            arSceneView.setupSession(session)

            // 在生命周期观察者中处理 pause 和 resume
            lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        try {
                            arSceneView.resume()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    Lifecycle.Event.ON_PAUSE -> arSceneView.pause()
                    Lifecycle.Event.ON_DESTROY -> arSceneView.session?.close()
                    else -> {}
                }
            })

            arSceneView
        },
        update = { arSceneView ->
            // 在这里可以添加更新逻辑
        }
    )
}*/


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
        ARShotView(shotPointOnScreen) { measuredDistance ->
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
