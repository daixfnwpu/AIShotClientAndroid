package com.ai.aishotclientkotlin.ui.screens.shot.screen.game

/*

@Composable
fun RajawaliInCompose(context: Context) {
    // 保持 SurfaceView 引用
    var rajawaliSurfaceView: SurfaceView? by remember { mutableStateOf(null) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            // 创建 RajawaliSurfaceView
            SurfaceView(context).apply {
                val renderer = AiShot3DRenderer(context)
                setSurfaceRenderer(renderer)
                rajawaliSurfaceView = this
            }
        },
        update = {
            // 可以在这里更新视图状态
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            // 销毁时释放资源
            rajawaliSurfaceView?.destroyDrawingCache()
        }
    }
}

class AiShot3DRenderer(context: Context) : Renderer(context) {

    private lateinit var model: Object3D
    private lateinit var cube: Cube
    init {
        setFrameRate(60)
    }

    override fun initScene() {
        val parser =LoaderOBJ(context.resources, mTextureManager, R.raw.stl150k)

        try {
            parser.parse()
            model = parser.parsedObject
            model.position = Vector3(0.0, 0.0, 0.0)
            // 创建一个材质
            val material = Material()
            // 也可以绑定纹理（如果有纹理）
            try {
                val texture = Texture("stl150k_mtl", R.drawable.stl150k) // 替换成你的纹理
                material.addTexture(texture)
            } catch (e: ATexture.TextureException) {
                e.printStackTrace()
            }
            model.material = material

            currentScene.addChild(model)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        currentCamera.position = Vector3(0.0, 0.0, 50.0)
       // currentCamera.lookAt(0.0, 0.0, 0.0)
        currentCamera.setLookAt(0.0, 0.0, 0.0)
    }

    override fun onRenderFrame(glUnused: GL10) {
        super.onRenderFrame(glUnused)
      //  model.rotate(Vector3(0.0, 0.5, 0.0), 1.0)
    }

    override fun onOffsetsChanged(
        xOffset: Float,
        yOffset: Float,
        xOffsetStep: Float,
        yOffsetStep: Float,
        xPixelOffset: Int,
        yPixelOffset: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun onTouchEvent(event: MotionEvent?) {
        if (event != null) {
            model.moveRight(event.x.toDouble())
            model.setScale( event.x.toDouble())
            model.moveForward(event.y.toDouble())
        }
        Log.e("EVENT",event.toString())
    }
}
*/

