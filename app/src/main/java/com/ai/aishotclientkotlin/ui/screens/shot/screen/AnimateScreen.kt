package com.ai.aishotclientkotlin.ui.screens.shot.screen
import android.content.Context
import android.opengl.EGLConfig
import android.opengl.GLSurfaceView
import android.opengl.GLES20
import android.opengl.Matrix
import android.view.MotionEvent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ai.aishotclientkotlin.R
import org.rajawali3d.Object3D
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.loader.LoaderOBJ
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.renderer.Renderer
import org.rajawali3d.view.SurfaceView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


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


@Composable
fun OpenGLInCompose() {
    // 保持 GLSurfaceView 的引用
    var glSurfaceView: GLSurfaceView? by remember { mutableStateOf(null) }

    // 使用 AndroidView 嵌入 GLSurfaceView
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                setRenderer(OpenGLRenderer(context))
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

                // 保持引用，便于在生命周期中管理
                glSurfaceView = this
            }
        },
        update = {
            // 这里可以在需要的时候更新视图状态
        }
    )

    // 管理生命周期
    DisposableEffect(Unit) {
        onDispose {
            // 当 Compose 组件销毁时，释放 GLSurfaceView 的资源
            glSurfaceView?.onPause()
            glSurfaceView = null
        }
    }
}

class OBJLoader {

    fun loadObj(context: Context, fileId: Int): FloatArray {
        val inputStream = context.resources.openRawResource(fileId)
        val reader = BufferedReader(InputStreamReader(inputStream))

        val vertices = mutableListOf<Float>()

        reader.forEachLine { line ->
            if (line.startsWith("v ")) {
                val parts = line.split(" ")
                vertices.add(parts[1].toFloat())  // x
                vertices.add(parts[2].toFloat())  // y
                vertices.add(parts[3].toFloat())  // z
            }
        }

        return vertices.toFloatArray()
    }
}
class AiShot3DRenderer(context: Context) : Renderer(context) {

    private lateinit var model: Object3D

    init {
        setFrameRate(60)
    }

    override fun initScene() {
        val parser = LoaderOBJ(context.resources, mTextureManager, R.raw.slingshot_v1)
        parser.parse()
        model = parser.parsedObject
        model.position = Vector3(0.0, 0.0, 0.0)
        currentScene.addChild(model)

        val light = DirectionalLight(1.0, 0.2, -1.0)
        light.setColor(1.0F, 1.0F, 1.0F)
        light.power = 2.0F
        currentScene.addLight(light)

        currentCamera.position = Vector3(0.0, 0.0, 4.0)
       // currentCamera.lookAt(0.0, 0.0, 0.0)
        currentCamera.setLookAt(0.0, 0.0, 0.0)
    }

    override fun onRenderFrame(glUnused: GL10) {
        super.onRenderFrame(glUnused)
        model.rotate(Vector3(0.0, 1.0, 0.0), 1.0)
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
        TODO("Not yet implemented")
    }
}



class OpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var vertexBuffer: FloatBuffer
    private var program: Int = 0

    // 顶点着色器代码
    private val vertexShaderCode = """
        attribute vec4 vPosition;
        uniform mat4 uMVPMatrix;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """

    // 片段着色器代码
    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """

    // 投影和视图矩阵
    private val mvpMatrix = FloatArray(16)


    override fun onDrawFrame(gl: GL10?) {
        // 清除颜色和深度缓冲区
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // 使用 OpenGL 程序
        GLES20.glUseProgram(program)

        // 获取并设置顶点属性
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, 3, GLES20.GL_FLOAT, false,
            12, vertexBuffer
        )

        // 获取并设置颜色
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(0.6f, 0.2f, 0.8f, 1.0f), 0)

        // 获取并设置模型视图投影矩阵
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexBuffer.capacity() / 3)

        // 禁用顶点属性
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        // 加载 .OBJ 模型
        val objLoader = OBJLoader()
        val vertices = objLoader.loadObj(context, R.raw.aislingshot001)

        // 创建顶点缓冲区
        val byteBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuffer.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        // 编译着色器
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // 创建 OpenGL 程序并链接着色器
        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 设置视口尺寸
        GLES20.glViewport(0, 0, width, height)

        // 设置投影矩阵
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(mvpMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}
