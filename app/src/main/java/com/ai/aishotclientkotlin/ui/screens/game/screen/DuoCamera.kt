package com.ai.aishotclientkotlin.ui.screens.game.screen

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.CompositionSettings
import androidx.camera.core.ConcurrentCamera
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


suspend fun getCameraProvider(context: Context): ProcessCameraProvider {
    return suspendCancellableCoroutine { continuation ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                continuation.resume(cameraProviderFuture.get())
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DualCameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasCameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var isConcurrentSupported by remember { mutableStateOf(false) }

    // 检查设备是否支持多摄像头并发流
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val concurrentCameraIds = cameraManager.concurrentCameraIds

            if (concurrentCameraIds != null) {
                Log.e("Camera", "Concurrent Camera ID Sets: $concurrentCameraIds")

                // 检查是否有至少一个大小为 2 或以上的 Set，表示支持并发摄像头
                isConcurrentSupported = concurrentCameraIds.any { it.size >= 2 }
            } else {
                Log.e("Camera", "Concurrent camera not supported on this device.")
                isConcurrentSupported = false
            }

            Log.e("Camera", "Is Concurrent Supported: $isConcurrentSupported")
        } else {
            Log.e("Camera", "Concurrent camera support requires Android 12 or higher.")
            isConcurrentSupported = false
        }
    }

    // 异步获取 cameraProvider 实例
    LaunchedEffect(Unit) {
        cameraProvider = awaitCameraProvider(context)
    }

    if (hasCameraPermission.status.isGranted && cameraProvider != null) {
        DualCameraPreview(
            cameraProvider = cameraProvider!!,
            lifecycleOwner = lifecycleOwner,
            isConcurrentSupported = isConcurrentSupported
        )
    } else {
        // 确保 ActivityResultLauncher 已经初始化后才请求权限
        if (!hasCameraPermission.status.isGranted && hasCameraPermission.status.shouldShowRationale.not()) {
            hasCameraPermission.launchPermissionRequest()
        } else {
            Text("Camera permission required")
        }
    }
}

// 将 ListenableFuture 转换为挂起函数
suspend fun awaitCameraProvider(context: Context): ProcessCameraProvider {
    return suspendCancellableCoroutine { continuation ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                continuation.resume(cameraProviderFuture.get())
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
@Composable
fun DualCameraPreview(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    isConcurrentSupported: Boolean
) {
    val context = LocalContext.current


    // Set up primary and secondary camera selectors if supported on device.
    var primaryCameraSelector: CameraSelector? = null
    var secondaryCameraSelector: CameraSelector? = null

    for (cameraInfos in cameraProvider.availableConcurrentCameraInfos) {
        primaryCameraSelector = cameraInfos.first {
            it.lensFacing == CameraSelector.LENS_FACING_FRONT
        }.cameraSelector
        secondaryCameraSelector = cameraInfos.first {
            it.lensFacing == CameraSelector.LENS_FACING_BACK
        }.cameraSelector

        if (primaryCameraSelector == null || secondaryCameraSelector == null) {
            // If either a primary or secondary selector wasn't found, reset both
            // to move on to the next list of CameraInfos.
            primaryCameraSelector = null
            secondaryCameraSelector = null
        } else {
            // If both primary and secondary camera selectors were found, we can
            // conclude the search.
            break
        }
    }

    if (primaryCameraSelector == null || secondaryCameraSelector == null) {
        // Front and back concurrent camera not available. Handle accordingly.
    }

    val preview1 = Preview.Builder().build()  // 后置摄像头预览
    val preview2 = Preview.Builder().build()

// If 2 concurrent camera selectors were found, create 2 SingleCameraConfigs
// and compose them in a picture-in-picture layout.
    val primary = primaryCameraSelector?.let {
        ConcurrentCamera.SingleCameraConfig(
            it,
            UseCaseGroup.Builder().addUseCase(preview1).build(),
        CompositionSettings.Builder()
            .setAlpha(1.0f)
            .setOffset(0.0f, 0.0f)
            .setScale(1.0f, 1.0f)
            .build(),
        lifecycleOwner
    )
    };
    val secondary = secondaryCameraSelector?.let {
        ConcurrentCamera.SingleCameraConfig(
            it,
            UseCaseGroup.Builder().addUseCase(preview2).build(),
        CompositionSettings.Builder()
            .setAlpha(1.0f)
            .setOffset(2 / 3f - 0.1f, -2 / 3f + 0.1f)
            .setScale(1 / 3f, 1 / 3f)
            .build(),
            lifecycleOwner
    )
    };

// Bind to lifecycle
    var concurrentCamera : ConcurrentCamera =
    cameraProvider.bindToLifecycle(listOf(primary, secondary));

































  /*  val preview1 = Preview.Builder().build()  // 后置摄像头预览
    val preview2 = Preview.Builder().build()  // 前置摄像头预览

    // 解绑所有绑定的使用案例，避免重复绑定
    cameraProvider.unbindAll()  // 确保清除先前的绑定

    try {
        // 创建前后摄像头选择器
        val cameraSelector1 = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val cameraSelector2 = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        // 如果设备支持并发摄像头，可以绑定两个不同的使用案例
        if (isConcurrentSupported) {
            // 绑定后置摄像头
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector1,
                preview1
            )
            // 绑定前置摄像头
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector2,
                preview2
            )
        } else {
            Log.e("Camera", "Concurrent streaming not supported on this device.")
        }
    } catch (e: Exception) {
        Log.e("Camera", "Failed to bind use cases: ${e.message}")
    }
*/
    // 使用两个独立的 PreviewView 来显示两个摄像头
    Row(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                val previewView1 = PreviewView(context)
                preview1.setSurfaceProvider(previewView1.surfaceProvider)  // 设置第一个摄像头的 SurfaceProvider
                previewView1
            }
        )
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                val previewView2 = PreviewView(context)
                preview2.setSurfaceProvider(previewView2.surfaceProvider)  // 设置第二个摄像头的 SurfaceProvider
                previewView2
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.S)
private suspend fun checkConcurrentSupport(context: Context): Boolean = withContext(Dispatchers.IO) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val concurrentCameraIds = cameraManager.concurrentCameraIds
    concurrentCameraIds != null && concurrentCameraIds.size >= 2
}




/*
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

*/
/**
 * A simple [Fragment] subclass.
 *//*

class CameraFragment : Fragment() {

    */
/**
     * An additional thread for running tasks that shouldn't block the UI.
     *//*

    private var backgroundThreadFront: HandlerThread? = null

    */
/**
     * An additional thread for running tasks that shouldn't block the UI.
     *//*

    private var backgroundThreadRear: HandlerThread? = null

    */
/**
     * A [Handler] for running tasks in the background.
     *//*

    private var backgroundHandlerFront: Handler? = null

    */
/**
     * A [Handler] for running tasks in the background.
     *//*

    private var backgroundHandlerRear: Handler? = null


    */
/**
     * An [ImageReader] that handles still image capture.
     *//*

    private var imageReaderFront: ImageReader? = null

    */
/**
     * An [ImageReader] that handles still image capture.
     *//*

    private var imageReaderRear: ImageReader? = null

    */
/**
     * An [AutoFitTextureView] to show the camera preview from front camera.
     *//*

    private lateinit var textureViewFront: AutoFitTextureView

    */
/**
     * An [AutoFitTextureView] to show the camera preview from rear camera.
     *//*

    private lateinit var textureViewRear: AutoFitTextureView

    */
/**
     * A [CameraCaptureSession] for camera preview.
     *//*

    private var captureSessionFront: CameraCaptureSession? = null

    */
/**
     * A [CameraCaptureSession] for camera preview.
     *//*

    private var captureSessionRear: CameraCaptureSession? = null

    */
/**
     * A reference to the opened [CameraDevice].
     *//*

    private var cameraDeviceFront: CameraDevice? = null

    */
/**
     * A reference to the opened [CameraDevice].
     *//*

    private var cameraDeviceRear: CameraDevice? = null

    */
/**
     * The [android.util.Size] of camera preview.
     *//*

    private lateinit var previewSizeFront: Size

    */
/**
     * The [android.util.Size] of camera preview.
     *//*

    private lateinit var previewSizeRear: Size

    */
/**
     * ID of the current [CameraDevice].
     *//*

    private lateinit var cameraIdFront: String

    */
/**
     * ID of the current [CameraDevice].
     *//*

    private lateinit var cameraIdRear: String

    */
/**
     * Orientation of the camera sensor
     *//*

    private var sensorOrientationFront = 0

    */
/**
     * Orientation of the camera sensor
     *//*

    private var sensorOrientationRear = 0

    */
/**
     * A [Semaphore] to prevent the app from exiting before closing the camera.
     *//*

    private val cameraOpenCloseLockFront = Semaphore(1)

    */
/**
     * A [Semaphore] to prevent the app from exiting before closing the camera.
     *//*

    private val cameraOpenCloseLockRear = Semaphore(1)

    */
/**
     * [CaptureRequest.Builder] for the camera preview
     *//*

    private lateinit var previewRequestBuilderFront: CaptureRequest.Builder

    */
/**
     * [CaptureRequest.Builder] for the camera preview
     *//*

    private lateinit var previewRequestBuilderRear: CaptureRequest.Builder
    */
/**
     * Whether the current camera device supports Flash or not.
     *//*

    private var flashSupported = false

    */
/**
     * [CaptureRequest] generated by [.previewRequestBuilder]
     *//*

    private lateinit var previewRequestFront: CaptureRequest

    */
/**
     * [CaptureRequest] generated by [.previewRequestBuilder]
     *//*

    private lateinit var previewRequestRear: CaptureRequest


    */
/**
     * This a callback object for the [ImageReader]. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     *//*

    private val onImageAvailableListenerFront = ImageReader.OnImageAvailableListener {
//        backgroundHandler?.post(ImageSaver(it.acquireNextImage(), file))
        Log.d(TAG, "onImageAvailableListenerFront Called")
    }


    */
/**
     * This a callback object for the [ImageReader]. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     *//*

    private val onImageAvailableListenerRear = ImageReader.OnImageAvailableListener {
        //        backgroundHandler?.post(ImageSaver(it.acquireNextImage(), file))
        Log.d(TAG, "onImageAvailableListener Called")
    }


    */
/**
     * A [CameraCaptureSession.CaptureCallback] that handles events related to JPEG capture.
     *//*

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {

        private fun process(result: CaptureResult) {

        }

        private fun capturePicture(result: CaptureResult) {

        }

        override fun onCaptureProgressed(session: CameraCaptureSession,
                                         request: CaptureRequest,
                                         partialResult: CaptureResult) {
            process(partialResult)
        }

        override fun onCaptureCompleted(session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult) {
            process(result)
        }

    }


    */
/**
     * [TextureView.SurfaceTextureListener] handles several lifecycle events on the front camera's
     * [TextureView].
     *//*

    private val surfaceTextureListenerFront = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            openCameraFront(width, height)
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            configureTransformFront(width, height)
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture) = true

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) = Unit

    }

    */
/**
     * [TextureView.SurfaceTextureListener] handles several lifecycle events on the rear camera's
     * [TextureView].
     *//*

    private val surfaceTextureListenerRear = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            openCameraRear(width, height)
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            configureTransformRear(width, height)
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture) = true

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) = Unit

    }

    */
/**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     *//*

    private val stateCallbackFront = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            cameraOpenCloseLockFront.release()
            this@CameraFragment.cameraDeviceFront = cameraDevice
            createCameraPreviewSessionFront()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraOpenCloseLockFront.release()
            cameraDevice.close()
            this@CameraFragment.cameraDeviceFront = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            onDisconnected(cameraDevice)
            this@CameraFragment.activity?.finish()
        }

    }

    */
/**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     *//*

    private val stateCallbackRear = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            cameraOpenCloseLockRear.release()
            this@CameraFragment.cameraDeviceRear = cameraDevice
            createCameraPreviewSessionRear()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraOpenCloseLockRear.release()
            cameraDevice.close()
            this@CameraFragment.cameraDeviceRear = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            onDisconnected(cameraDevice)
            this@CameraFragment.activity?.finish()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textureViewFront = view.findViewById(R.id.texture1)
        textureViewRear = view.findViewById(R.id.texture2)
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (textureViewFront.isAvailable) {
            openCameraFront(textureViewFront.width, textureViewFront.height)
        } else {
            textureViewFront.surfaceTextureListener = surfaceTextureListenerFront
        }
        if (textureViewRear.isAvailable) {
            openCameraRear(textureViewRear.width, textureViewRear.height)
        } else {
            textureViewRear.surfaceTextureListener = surfaceTextureListenerRear
        }
    }

    override fun onPause() {
        closeCameraFront()
        closeCameraRear()
        stopBackgroundThread()
        super.onPause()
    }

    */
/**
     * Opens front camera specified by [Camera2BasicFragment.cameraId].
     *//*

    private fun openCameraFront(width: Int, height: Int) {
        val permission = activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
            return
        }
        setUpCameraOutputsFront(width, height)
        configureTransformFront(width, height)
        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // Wait for camera to open - 2.5 seconds is sufficient
            if (!cameraOpenCloseLockFront.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(cameraIdFront, stateCallbackFront, backgroundHandlerRear)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }

    }

    */
/**
     * Opens rear camera specified by [Camera2BasicFragment.cameraId].
     *//*

    private fun openCameraRear(width: Int, height: Int) {
        val permission = activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
            return
        }
        setUpCameraOutputsRear(width, height)
        configureTransformRear(width, height)
        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // Wait for camera to open - 2.5 seconds is sufficient
            if (!cameraOpenCloseLockRear.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(cameraIdRear, stateCallbackRear, backgroundHandlerRear)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }

    }


    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    */
/**
     * Sets up member variables related to front camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     *//*

    private fun setUpCameraOutputsFront(width: Int, height: Int) {
        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (cameraDirection != null &&
                    cameraDirection == CameraCharacteristics.LENS_FACING_BACK) {
                    continue
                }

                val map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue

                // For still image captures, we use the largest available size.
                val aspectRatio = Collections.max(
                    Arrays.asList(*map.getOutputSizes(ImageFormat.JPEG)),
                    CompareSizesByViewAspectRatio(textureViewFront.height, textureViewFront.width))
                imageReaderFront = ImageReader.newInstance(aspectRatio.width, aspectRatio.height,
                    ImageFormat.JPEG, */
/*maxImages*//*
 2).apply {
                    setOnImageAvailableListener(onImageAvailableListenerFront, backgroundHandlerFront)
                }

                Log.d(TAG, "selected aspect ratio " + aspectRatio.height  + "x" + aspectRatio.width + " : " + aspectRatio.height/aspectRatio.width)
                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                val displayRotation = activity!!.windowManager.defaultDisplay.rotation

                sensorOrientationFront = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                val swappedDimensions = areDimensionsSwappedFront(displayRotation)

                val displaySize = Point()
                activity!!.windowManager.defaultDisplay.getSize(displaySize)
                val rotatedPreviewWidth = if (swappedDimensions) height else width
                val rotatedPreviewHeight = if (swappedDimensions) width else height
                var maxPreviewWidth = if (swappedDimensions) displaySize.y else displaySize.x
                var maxPreviewHeight = if (swappedDimensions) displaySize.x else displaySize.y

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) maxPreviewWidth = MAX_PREVIEW_WIDTH
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) maxPreviewHeight = MAX_PREVIEW_HEIGHT

                // Danger, W.R.! Attempting to use too large a preview size could exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                previewSizeFront = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
                    rotatedPreviewWidth, rotatedPreviewHeight,
                    maxPreviewWidth, maxPreviewHeight,
                    aspectRatio)


                */
/*
                 * We are filling the whole view with camera preview, on a downside, this distorts
                 * the aspect ratio.
                 * To retain the aspect ratio, uncomment the below line.
                 * Another option is the crop preview into the view, for that we have to choose
                 * preview ratio such that it comes nearest to aspect ratio of view.
                 *//*

//                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    textureView.setAspectRatio(previewSize.width, previewSize.height)
//                } else {
//                    textureView.setAspectRatio(previewSize.height, previewSize.width)
//                }

                this.cameraIdFront = cameraId

                // We've found a viable camera and finished setting up member variables,
                // so we don't need to iterate through other available cameras.
                return
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                .show(childFragmentManager, FRAGMENT_DIALOG)
        }

    }

    */
/**
     * Closes the current [CameraDevice].
     *//*

    private fun closeCameraFront() {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                captureSessionFront?.stopRepeating();
                captureSessionFront?.abortCaptures();
            }

            cameraOpenCloseLockFront.acquire()
            captureSessionFront?.close()
            captureSessionFront = null
            cameraDeviceFront?.close()
            cameraDeviceFront = null
            imageReaderFront?.close()
            imageReaderFront = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock front camera closing.", e)
        } finally {
            cameraOpenCloseLockFront.release()
        }
    }

    */
/**
     * Closes the current [CameraDevice].
     *//*

    private fun closeCameraRear() {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                captureSessionRear?.stopRepeating();
                captureSessionRear?.abortCaptures();
            }
            cameraOpenCloseLockRear.acquire()
            captureSessionRear?.close()
            captureSessionRear = null
            cameraDeviceRear?.close()
            cameraDeviceRear = null
            imageReaderRear?.close()
            imageReaderRear = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock rear camera closing.", e)
        } finally {
            cameraOpenCloseLockRear.release()
        }
    }
    */
/**
     * Sets up member variables related to rear camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     *//*

    private fun setUpCameraOutputsRear(width: Int, height: Int) {
        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (cameraDirection != null &&
                    cameraDirection == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                val map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue

                // For still image captures, we use the largest available size.
                val aspectRatio = Collections.max(
                    Arrays.asList(*map.getOutputSizes(ImageFormat.JPEG)),
                    CompareSizesByViewAspectRatio(textureViewRear.height, textureViewRear.width))
                imageReaderRear = ImageReader.newInstance(aspectRatio.width, aspectRatio.height,
                    ImageFormat.JPEG, */
/*maxImages*//*
 2).apply {
                    setOnImageAvailableListener(onImageAvailableListenerRear, backgroundHandlerRear)
                }

                Log.d(TAG, "selected aspect ratio " + aspectRatio.height  + "x" + aspectRatio.width + " : " + aspectRatio.height/aspectRatio.width)
                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                val displayRotation = activity!!.windowManager.defaultDisplay.rotation

                sensorOrientationRear = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                val swappedDimensions = areDimensionsSwappedRear(displayRotation)

                val displaySize = Point()
                activity!!.windowManager.defaultDisplay.getSize(displaySize)
                val rotatedPreviewWidth = if (swappedDimensions) height else width
                val rotatedPreviewHeight = if (swappedDimensions) width else height
                var maxPreviewWidth = if (swappedDimensions) displaySize.y else displaySize.x
                var maxPreviewHeight = if (swappedDimensions) displaySize.x else displaySize.y

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) maxPreviewWidth = MAX_PREVIEW_WIDTH
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) maxPreviewHeight = MAX_PREVIEW_HEIGHT

                // Danger, W.R.! Attempting to use too large a preview size could exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                previewSizeRear = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
                    rotatedPreviewWidth, rotatedPreviewHeight,
                    maxPreviewWidth, maxPreviewHeight,
                    aspectRatio)


                */
/*
                 * We are filling the whole view with camera preview, on a downside, this distorts
                 * the aspect ratio.
                 * To retain the aspect ratio, uncomment the below line.
                 * Another option is the crop preview into the view, for that we have to choose
                 * preview ratio such that it comes nearest to aspect ratio of view.
                 *//*

//                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    textureView.setAspectRatio(previewSize.width, previewSize.height)
//                } else {
//                    textureView.setAspectRatio(previewSize.height, previewSize.width)
//                }

                this.cameraIdRear = cameraId

                // We've found a viable camera and finished setting up member variables,
                // so we don't need to iterate through other available cameras.
                return
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                .show(childFragmentManager, FRAGMENT_DIALOG)
        }

    }



    */
/**
     * Configures the necessary [android.graphics.Matrix] transformation to `textureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `textureView` is fixed.
     *
     * @param viewWidth  The width of `textureView`
     * @param viewHeight The height of `textureView`
     *//*

    private fun configureTransformFront(viewWidth: Int, viewHeight: Int) {
        activity ?: return
        val rotation = activity!!.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewSizeFront.height.toFloat(), previewSizeFront.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            val scale = Math.max(
                viewHeight.toFloat() / previewSizeFront.height,
                viewWidth.toFloat() / previewSizeFront.width)
            with(matrix) {
                setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                postScale(scale, scale, centerX, centerY)
                postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
            }
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        textureViewFront.setTransform(matrix)
    }

    */
/**
     * Configures the necessary [android.graphics.Matrix] transformation to `textureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `textureView` is fixed.
     *
     * @param viewWidth  The width of `textureView`
     * @param viewHeight The height of `textureView`
     *//*

    private fun configureTransformRear(viewWidth: Int, viewHeight: Int) {
        activity ?: return
        val rotation = activity!!.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewSizeRear.height.toFloat(), previewSizeRear.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            val scale = Math.max(
                viewHeight.toFloat() / previewSizeRear.height,
                viewWidth.toFloat() / previewSizeRear.width)
            with(matrix) {
                setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                postScale(scale, scale, centerX, centerY)
                postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
            }
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        textureViewRear.setTransform(matrix)
    }

    */
/**
     * Determines if the dimensions are swapped given the phone's current rotation.
     *
     * @param displayRotation The current rotation of the display
     *
     * @return true if the dimensions are swapped, false otherwise.
     *//*

    private fun areDimensionsSwappedFront(displayRotation: Int): Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (sensorOrientationFront == 90 || sensorOrientationFront == 270) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (sensorOrientationFront == 0 || sensorOrientationFront == 180) {
                    swappedDimensions = true
                }
            }
            else -> {
                Log.e(TAG, "Display rotation is invalid: $displayRotation")
            }
        }
        return swappedDimensions
    }


    */
/**
     * Determines if the dimensions are swapped given the phone's current rotation.
     *
     * @param displayRotation The current rotation of the display
     *
     * @return true if the dimensions are swapped, false otherwise.
     *//*

    private fun areDimensionsSwappedRear(displayRotation: Int): Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (sensorOrientationRear == 90 || sensorOrientationRear == 270) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (sensorOrientationRear == 0 || sensorOrientationRear == 180) {
                    swappedDimensions = true
                }
            }
            else -> {
                Log.e(TAG, "Display rotation is invalid: $displayRotation")
            }
        }
        return swappedDimensions
    }

    */
/**
     * Starts a background thread and its [Handler].
     *//*

    private fun startBackgroundThread() {
        backgroundThreadFront = HandlerThread("CameraBackgroundFront").also { it.start() }
        backgroundThreadRear = HandlerThread("CameraBackgroundRear").also { it.start() }
        backgroundHandlerFront = Handler(backgroundThreadFront?.looper)
        backgroundHandlerRear = Handler(backgroundThreadRear?.looper)
    }

    */
/**
     * Stops the background thread and its [Handler].
     *//*

    private fun stopBackgroundThread() {
        backgroundThreadFront?.quitSafely()
        backgroundThreadRear?.quitSafely()
        try {
            backgroundThreadFront?.join()
            backgroundThreadFront = null
            backgroundHandlerFront = null

            backgroundThreadRear?.join()
            backgroundThreadRear = null
            backgroundHandlerRear = null
        } catch (e: InterruptedException) {
            Log.e(TAG, e.toString())
        }

    }

    */
/**
     * Creates a new [CameraCaptureSession] for front camera preview.
     *//*

    private fun createCameraPreviewSessionFront() {
        try {
            val texture = textureViewFront.surfaceTexture

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSizeFront.width, previewSizeFront.height)

            // This is the output Surface we need to start preview.
            val surface = Surface(texture)

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilderFront = cameraDeviceFront!!.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )
            previewRequestBuilderFront.addTarget(surface)

            // Here, we create a CameraCaptureSession for camera preview.
            cameraDeviceFront?.createCaptureSession(Arrays.asList(surface, imageReaderFront?.surface),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        // The camera is already closed
                        if (cameraDeviceFront == null) return

                        // When the session is ready, we start displaying the preview.
                        captureSessionFront = cameraCaptureSession
                        try {
                            // Auto focus should be continuous for camera preview.
                            previewRequestBuilderFront.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)

                            // Finally, we start displaying the camera preview.
                            previewRequestFront = previewRequestBuilderFront.build()
                            captureSessionFront?.setRepeatingRequest(previewRequestFront,
                                captureCallback, backgroundHandlerFront)
                        } catch (e: CameraAccessException) {
                            Log.e(TAG, e.toString())
                        }

                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.d(TAG, "CaptureSession failed")
                    }
                }, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }

    }

    */
/**
     * Creates a new [CameraCaptureSession] for rear camera preview.
     *//*

    private fun createCameraPreviewSessionRear() {
        try {
            val texture = textureViewRear.surfaceTexture

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSizeRear.width, previewSizeRear.height)

            // This is the output Surface we need to start preview.
            val surface = Surface(texture)

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilderRear = cameraDeviceRear!!.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )
            previewRequestBuilderRear.addTarget(surface)

            // Here, we create a CameraCaptureSession for camera preview.
            cameraDeviceRear?.createCaptureSession(Arrays.asList(surface, imageReaderRear?.surface),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        // The camera is already closed
                        if (cameraDeviceRear == null) return

                        // When the session is ready, we start displaying the preview.
                        captureSessionRear = cameraCaptureSession
                        try {
                            // Auto focus should be continuous for camera preview.
                            previewRequestBuilderRear.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)

                            // Finally, we start displaying the camera preview.
                            previewRequestRear = previewRequestBuilderRear.build()
                            captureSessionRear?.setRepeatingRequest(previewRequestRear,
                                captureCallback, backgroundHandlerRear)
                        } catch (e: CameraAccessException) {
                            Log.e(TAG, e.toString())
                        }

                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
//                        activity.showToast("Failed")
                        Log.d(TAG, "CaptureSession failed")
                    }
                }, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }

    }


    companion object {

        */
/**
         * Conversion from screen rotation to JPEG orientation.
         *//*

        private val ORIENTATIONS = SparseIntArray()
        private val FRAGMENT_DIALOG = "dialog"

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        */
/**
         * Max preview width that is guaranteed by Camera2 API
         *//*

        private val MAX_PREVIEW_WIDTH = 1920

        */
/**
         * Max preview height that is guaranteed by Camera2 API
         *//*

        private val MAX_PREVIEW_HEIGHT = 1080


        */
/**
         * Given `choices` of `Size`s supported by a camera, choose the smallest one that
         * is at least as large as the respective texture view size, and that is at most as large as
         * the respective max size, and whose aspect ratio matches with the specified value. If such
         * size doesn't exist, choose the largest one that is at most as large as the respective max
         * size, and whose aspect ratio matches with the specified value.
         *
         * @param choices           The list of sizes that the camera supports for the intended
         *                          output class
         * @param textureViewWidth  The width of the texture view relative to sensor coordinate
         * @param textureViewHeight The height of the texture view relative to sensor coordinate
         * @param maxWidth          The maximum width that can be chosen
         * @param maxHeight         The maximum height that can be chosen
         * @param aspectRatio       The aspect ratio
         * @return The optimal `Size`, or an arbitrary one if none were big enough
         *//*

        @JvmStatic private fun chooseOptimalSize(
            choices: Array<Size>,
            textureViewWidth: Int,
            textureViewHeight: Int,
            maxWidth: Int,
            maxHeight: Int,
            aspectRatio: Size
        ): Size {

            // Collect the supported resolutions that are at least as big as the preview Surface
            val bigEnough = ArrayList<Size>()
            // Collect the supported resolutions that are smaller than the preview Surface
            val notBigEnough = ArrayList<Size>()
            val w = aspectRatio.width
            val h = aspectRatio.height
            for (option in choices) {
                if (option.width <= maxWidth && option.height <= maxHeight &&
                    option.height == option.width * h / w) {
                    if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                        bigEnough.add(option)
                    } else {
                        notBigEnough.add(option)
                    }
                }
            }

            // Pick the smallest of those big enough. If there is no one big enough, pick the
            // largest of those not big enough.
            if (bigEnough.size > 0) {
                return Collections.min(bigEnough, CompareSizesByViewAspectRatio(textureViewHeight, textureViewWidth))
            } else if (notBigEnough.size > 0) {
                return Collections.max(notBigEnough, CompareSizesByViewAspectRatio(textureViewHeight, textureViewWidth))
            } else {
                Log.e(TAG, "Couldn't find any suitable preview size")
                return choices[0]
            }
        }

        */
/**
         * Tag for the [Log] from this class
         *//*

        private val TAG = CameraFragment::class.java.simpleName

        @JvmStatic fun newInstance(): CameraFragment = CameraFragment()
    }

}
*/
