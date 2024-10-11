package com.ai.aishotclientkotlin.ui.screens.shot.screen.game

/*

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(modifier: Modifier) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraIdList = cameraManager.cameraIdList

    cameraIdList.forEach { cameraId ->
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
        if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
            Log.e("AR", "Back camera is available")
        } else if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            Log.e("AR", "Front camera is available")
        }
        val cameraCapabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
        if (cameraCapabilities?.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) == true) {
            Log.e("AR", "Camera supports backward compatibility")
        } else {
            Log.e("AR", "Camera does not support running multiple streams simultaneously")
        }
    }
    // 创建不同的 LifecycleOwner


    //LaunchedEffect(Unit) {
        if (permissionState.status != PermissionStatus.Granted) {
            permissionState.launchPermissionRequest()
        } else {
            cameraProvider = cameraProviderFuture.get()
            Log.e("AR","Granted")
        }
  //  }

   // CameraSelector.Builder().requireLensFacing()requireLensFacing

    if (permissionState.status == PermissionStatus.Granted && cameraProvider != null) {
        Column(modifier = modifier) {

//            FrontCameraImageAnalysis(cameraProvider!!,frontCameraLifecycleOwner)
//            CameraPreview(Modifier.weight(1f), CameraSelector.DEFAULT_BACK_CAMERA, cameraProvider!!,backCameraLifecycleOwner)
        //    CameraPreview(Modifier.weight(1f), CameraSelector.DEFAULT_FRONT_CAMERA, cameraProvider!!,frontCameraLifecycleOwner)

            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current


           //     val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

               // cameraProviderFuture.addListener({
                    //   val cameraProvider = cameraProviderFuture.get()

                    // 创建 ImageAnalysis 对象
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    // 设置图像分析器
                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        // 在这里处理图像分析逻辑
                        Log.e("ImageAnalysis", "分析到图像: ${imageProxy.imageInfo.timestamp}")
                        imageProxy.close() // 处理完图像后关闭
                    }



               //     val cameraProvider = cameraProviderFuture.get()
                    val previewView = PreviewView(context)
                    // 创建 Preview 对象
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    try {
                        val primary: SingleCameraConfig = SingleCameraConfig(
                            CameraSelector.DEFAULT_FRONT_CAMERA, UseCaseGroup.Builder()
                                .addUseCase(imageAnalysis)
                                .build(),lifecycleOwner
                        )
                        val secondary: SingleCameraConfig = SingleCameraConfig(
                            CameraSelector.DEFAULT_BACK_CAMERA, UseCaseGroup.Builder()
                                .addUseCase(preview)
                                .build(),lifecycleOwner
                        )

                        cameraProvider!!.bindToLifecycle(
                            com.google.common.collect.ImmutableList.of(
                                primary,
                                secondary
                            )
                        )
                    } catch (exc: Exception) {
                        Log.e("CameraX", "摄像头绑定失败: ${exc.message}")
                    }

           //     }, ContextCompat.getMainExecutor(context))



        }
    }
}


private fun startCameraPreview(previewView: PreviewView, context: Context, cameraSelector: CameraSelector) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        // 创建 Preview 对象
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        try {
            // 解绑之前的所有用例
            cameraProvider.unbindAll()

            // 绑定预览到后置摄像头
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (exc: Exception) {
            Log.e("CameraX", "摄像头绑定失败: ${exc.message}")
        }
    }, ContextCompat.getMainExecutor(context))
}

@Composable
fun FrontCameraImageAnalysis( cameraProvider: ProcessCameraProvider,lifecycleOwner: LifecycleOwner) {
    val context = LocalContext.current
    //val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
         //   val cameraProvider = cameraProviderFuture.get()

            // 创建 ImageAnalysis 对象
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // 设置图像分析器
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), { imageProxy ->
                // 在这里处理图像分析逻辑
                Log.e("ImageAnalysis", "分析到图像: ${imageProxy.imageInfo.timestamp}")
                imageProxy.close() // 处理完图像后关闭
            })

            try {
                // 解绑之前的所有用例
                cameraProvider.unbindAll()

                // 绑定图像分析到前置摄像头
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("CameraX", "摄像头绑定失败: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraProviderFuture.cancel(true)
        }
    }
}



@Composable
fun CameraPreview(modifier: Modifier, cameraSelector: CameraSelector, cameraProvider: ProcessCameraProvider,lifecycleOwner: LifecycleOwner) {
    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            startCamera(previewView, context, cameraSelector, cameraProvider,lifecycleOwner)
            Log.e("AR","CameraPreview")
            previewView
        },
        modifier = Modifier
    )
}

private fun startCamera(  previewView: PreviewView,
                          context: Context,
                          cameraSelector: CameraSelector,
                          cameraProvider: ProcessCameraProvider, lifecycleOwner: LifecycleOwner) {



    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }

    try {
        // 仅绑定所需的摄像头，不再解绑所有摄像头
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview
        )
        Log.e("AR", "Camera bound successfully for $cameraSelector")
    } catch (exc: Exception) {
        Log.e("AR", "Failed to bind camera: ${exc.message}")
    }
}


class CustomLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        // 设置初始状态为 CREATED，生命周期开始
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }



    // 启动生命周期，将状态变为 STARTED
    fun start() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    // 停止生命周期，将状态变为 DESTROYED
    fun stop() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}
*/
/*
@SuppressLint("PermissionLaunchedDuringComposition")
@Composable
fun ConcurrentCameraScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    // Camera states
    var frontCameraDevice by remember { mutableStateOf<CameraDevice?>(null) }
    var backCameraDevice by remember { mutableStateOf<CameraDevice?>(null) }
    // Function to open both cameras
    fun openCameras(cameraManager: CameraManager) {
        openCamera(cameraManager, CameraCharacteristics.LENS_FACING_FRONT) { camera ->
            frontCameraDevice = camera
        }
//        openCamera(cameraManager, CameraCharacteristics.LENS_FACING_BACK) { camera ->
//            backCameraDevice = camera
//        }
    }
    // Handle permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                openCameras(cameraManager)
            } else {
                Log.e("Camera", "Camera permission not granted")
            }
        }
    )

    // Check permissions and open cameras
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCameras(cameraManager)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }



    // UI with both camera previews
    Column(modifier = modifier.fillMaxSize()) {
        // Front Camera Preview
        CameraPreview(frontCameraDevice, "Front Camera")

        // Back Camera Preview
      //  CameraPreview(backCameraDevice, "Back Camera")
    }
}

@Composable
fun CameraPreview(cameraDevice: CameraDevice?, label: String) {
    var surfaceTexture by remember { mutableStateOf<SurfaceTexture?>(null) }
    var previewSurface by remember { mutableStateOf<Surface?>(null) }

    AndroidView(
        factory = { context ->
            TextureView(context).apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        surfaceTexture = surface
                        previewSurface = Surface(surface)
                        cameraDevice?.let {
                            Log.e(label, "Starting camera preview")
                            startCameraPreview(it, previewSurface!!)
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        Log.e(label, "Surface destroyed")
                        previewSurface?.release()
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
    )
}

@SuppressLint("MissingPermission")
fun openCamera(cameraManager: CameraManager, lensFacing: Int, onOpened: (CameraDevice) -> Unit) {
    val cameraId = cameraManager.cameraIdList.find { id ->
        cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING) == lensFacing
    } ?: return

    cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Log.e("Camera", "Camera $lensFacing opened")
            onOpened(camera)
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.e("Camera", "Camera $lensFacing disconnected")
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e("Camera", "Error opening camera $lensFacing: $error")
            camera.close()
        }
    }, Handler(Looper.getMainLooper()))
}

fun startCameraPreview(cameraDevice: CameraDevice, surface: Surface) {
    val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
    captureRequestBuilder.addTarget(surface)

    cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            Log.e("Camera", "Camera preview configured")
            session.setRepeatingRequest(captureRequestBuilder.build(), null, Handler(Looper.getMainLooper()))
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.e("Camera", "Failed to configure camera preview")
        }
    }, Handler(Looper.getMainLooper()))
}
*/

