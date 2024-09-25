package com.ai.aishotclientkotlin.ui.screens.shot.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.platform.LocalContext
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.rememberEngine


@Composable
fun ARSceneView() {
    val engine = rememberEngine()

    val context = LocalContext.current
    val materialLoader = MaterialLoader(engine, context = context)

    ARScene(

//...
//  Everything from a Scene
//...

// Fundamental session features that can be requested.
        sessionFeatures = setOf(),
// The camera config to use.
// The config must be one returned by [Session.getSupportedCameraConfigs].
// Provides details of a camera configuration such as size of the CPU image and GPU texture.
        sessionCameraConfig = null,
// Configures the session and verifies that the enabled features in the specified session config
// are supported with the currently set camera config.
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
        planeRenderer = true,
// The [ARCameraStream] to render the camera texture.
// Use it to control if the occlusion should be enabled or disabled.
        cameraStream = rememberARCameraStream(materialLoader),
// The session is ready to be accessed.
        onSessionCreated = { session ->
        },
// The session has been resumed.
        onSessionResumed = { session ->
        },
// The session has been paused
        onSessionPaused = { session ->
        },
// Updates of the state of the ARCore system.
// This includes: receiving a new camera frame, updating the location of the device, updating
// the location of tracking anchors, updating detected planes, etc.
// This call may update the pose of all created anchors and detected planes. The set of updated
// objects is accessible through [Frame.getUpdatedTrackables].
// Invoked once per [Frame] immediately before the Scene is updated.
        onSessionUpdated = { session, updatedFrame ->
        },
// Invoked when an ARCore error occurred.
// Registers a callback to be invoked when the ARCore Session cannot be initialized because
// ARCore is not available on the device or the camera permission has been denied.
        onSessionFailed = { exception ->
        },
// Listen for camera tracking failure.
// The reason that [Camera.getTrackingState] is [TrackingState.PAUSED] or `null` if it is
// [TrackingState.TRACKING]
        onTrackingFailureChanged = { trackingFailureReason ->
        }
    )
}