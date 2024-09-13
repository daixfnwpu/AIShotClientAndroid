package com.ai.aishotclientkotlin.ui.screens.shot.screen
import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.GLES20

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun OpenGLComposeView(context: Context) {
    AndroidView(
        factory = {
            val glView = GLSurfaceView(context)
            glView.setEGLContextClientVersion(2)
            glView.setRenderer(OpenGLRenderer())
            glView
        },
        modifier = Modifier.fillMaxSize()
    )
}

class OpenGLRenderer : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f)  // Set the background color to blue
    }

    override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceChanged(gl: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }
}