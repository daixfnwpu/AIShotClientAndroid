package com.ai.aishotclientkotlin.data.ble

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.speech.tts.TextToSpeech
import com.ai.aishotclientkotlin.R
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import java.util.Locale

// TODO : 用于提供audio播放和client数据更新；（接收和发送数据的service）；,被BleManger回调，和被BleViewModel 调用；
//  在蓝牙连接成功后启动该服务，该服务在app配置文件种注册；
class  BleService : Service(), OnInitListener {
    var useTextToSpeech : Boolean = false
    init {
        BLEManager.onNotificationReceived.add{ characteristic: Characteristic, bytes: ByteArray ->
                when (bytes.toString(Charsets.UTF_8))
                {
                    "audio_1" -> {
                        if(useTextToSpeech) {
                            speakText("连接成功")
                        }else
                        playAudioBasedOnData(1)

                    }
                    "audio_2" -> {
                        if(useTextToSpeech) {
                            speakText("连接成功")
                        }else
                        playAudioBasedOnData(2)
                    }
                    "audio_3" -> {
                        if(useTextToSpeech) {
                            speakText("连接成功")
                        }else
                        playAudioBasedOnData(3)
                    }
                    "audio_4" -> {
                        if(useTextToSpeech) {
                            speakText("连接成功")
                        }else
                        playAudioBasedOnData(4)
                    }
                    "audio_5" -> {
                        if(useTextToSpeech) {
                            speakText("连接成功")
                        }else
                        playAudioBasedOnData(5)
                    }
                    "audio_6" -> {
                        if(useTextToSpeech) {
                            speakText("连接成功")
                        }else
                        playAudioBasedOnData(6)
                    }
                    else      -> {}
                }
        }
    }

    private var currentMediaPlayer: MediaPlayer? = null

    private fun playAudioBasedOnData(dataType: Int) {
        // 如果已经有音频在播放，先停止并释放
        currentMediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }

        val audioResId : Int = when (dataType) {
            1 -> R.raw.audio_file_1
            2 -> R.raw.audio_file_2
            3 -> R.raw.audio_file_3
            else -> R.raw.default_audio
        }

        // 播放新音频
        currentMediaPlayer = MediaPlayer.create(this, audioResId).apply {
            start()
            setOnCompletionListener {
                it.release()
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate() {
        super.onCreate()
        // 初始化 TextToSpeech
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val langResult = textToSpeech.setLanguage(Locale.CHINA)
            useTextToSpeech = true
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 语言数据缺失或不支持
                Log.e("TextToSpeech","TTS语言不支持或缺失")
                useTextToSpeech = false
            }
        } else {
            Log.e("TextToSpeech","TextToSpeech初始化失败")
            useTextToSpeech = false
        }
    }


    // 使用 TTS 朗读文字
    private fun speakText(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

}