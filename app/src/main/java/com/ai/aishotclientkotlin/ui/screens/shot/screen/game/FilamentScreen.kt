package com.ai.aishotclientkotlin.ui.screens.shot.screen.game

/*
class MaterialProvider(private val engine: Engine, private val context: Context) {
    private val materialInstances = mutableMapOf<String, MaterialInstance>()

    // 加载材质的方法
    fun loadMaterial(materialPath: String): MaterialInstance? {
        if (materialInstances.containsKey(materialPath)) {
            return materialInstances[materialPath]
        }

        // 假设材质以 KTX 纹理格式存在 assets 目录中
        val inputStream = context.assets.open(materialPath)
        val materialInstance = KTXLoader.createMaterialInstance(engine, inputStream)
        materialInstances[materialPath] = materialInstance

        return materialInstance
    }
}*/

/*

class MyMaterialProvider(private val engine: Engine) : MaterialProvider {

    override fun createMaterialInstance(
        config: MaterialProvider.MaterialKey?,
        uvmap: IntArray,
        label: String?,
        extras: String?
    ): MaterialInstance? {
        TODO("Not yet implemented")
    }

    override fun getMaterial(
        config: MaterialProvider.MaterialKey?,
        uvmap: IntArray,
        label: String?
    ): Material? {
        TODO("Not yet implemented")
    }

    override fun getMaterials(): Array<Material> {
        TODO("Not yet implemented")
    }

    override fun needsDummyData(attrib: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun destroyMaterials() {
        // 释放材质资源
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }
}
@Composable
fun FilamentView(context: Context) {

    val engine = remember { Engine.create() }
    val renderer = remember { engine.createRenderer() }
    val scene = remember { engine.createScene() }
    val cameraEntity = EntityManager.get().create()
    val camera = remember { engine.createCamera(cameraEntity) }
    val view = remember { engine.createView() }

    val modelLoader = remember { AssetLoader(engine, MyMaterialProvider(engine), EntityManager.get()) }

    var glSurfaceView: GLSurfaceView? by remember { mutableStateOf(null) }
    var modelAsset: FilamentAsset? by remember { mutableStateOf(null) }

    // Load glTF model from assets
    DisposableEffect(Unit) {

        // 从 assets 目录加载 glTF 文件
        val inputStream = context.assets.open("path/to/model.gltf")
        val buffer = ByteBuffer.wrap(inputStream.readBytes())

        modelAsset = modelLoader.createAsset(buffer)
        scene.addEntities(modelAsset!!.entities)
        onDispose {
            modelAsset?.releaseSourceData()
        }
    }

    // AndroidView to integrate GLSurfaceView into Compose
    AndroidView(
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(3)
                setRenderer(object : GLSurfaceView.Renderer {
                    override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
                        renderer.beginFrame(engine.createSwapChain(this@apply.holder.surface))
                        renderer.render(view)
                        renderer.endFrame()
                    }

                    override fun onSurfaceChanged(gl: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
                        view.viewport = Viewport(0, 0, width, height)
                        camera.setProjection(45.0, width.toDouble() / height, 0.1, 100.0f)
                    }

                    override fun onSurfaceCreated(gl: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {}
                })
            }.also { glSurfaceView = it }
        },
        modifier = Modifier.fillMaxSize()
    )

    // Gesture handling (moving, scaling)
    var scaleFactor by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    glSurfaceView?.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                offsetX += event.x
                offsetY += event.y
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    scaleFactor *= event.getPointerCoords(0).x / event.getPointerCoords(1).x
                }
            }
        }
        true
    }

    // Update camera based on gestures
    LaunchedEffect(offsetX, offsetY, scaleFactor) {
        camera.setModelMatrix(
            translation(offsetX.toDouble(), offsetY.toDouble(), -10.0) * scale(scaleFactor.toDouble())
        )

    }
}
*/
