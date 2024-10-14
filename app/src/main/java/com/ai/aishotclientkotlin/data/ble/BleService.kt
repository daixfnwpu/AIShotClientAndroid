package com.ai.aishotclientkotlin.data.ble

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import com.ai.aishotclientkotlin.R
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.widget.Toast
import java.util.Locale

// TODO : 用于提供audio播放和client数据更新；（接收和发送数据的service）；,被BleManger回调，和被BleViewModel 调用；
//  在蓝牙连接成功后启动该服务，该服务在app配置文件种注册；
class  BleService : Service(), OnInitListener, RecognitionListener {
    var useTextToSpeech : Boolean = false
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
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




    override fun onCreate() {
        super.onCreate()
        // 初始化 TextToSpeech
        textToSpeech = TextToSpeech(this, this)
        // 初始化 SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(this)
    }

    // 开始语音识别
    private fun startListening() {
        val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer.startListening(intent)
    }

    // SpeechRecognizer 的回调处理
    override fun onReadyForSpeech(params: Bundle?) {
        Toast.makeText(this, "准备开始语音识别...", Toast.LENGTH_SHORT).show()
    }

    override fun onBeginningOfSpeech() {
        Toast.makeText(this, "开始说话...", Toast.LENGTH_SHORT).show()
    }

    override fun onRmsChanged(rmsdB: Float) {
        // 处理语音音量变化
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // 处理音频数据
    }

    override fun onEndOfSpeech() {
        Toast.makeText(this, "说话结束", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: Int) {
        Toast.makeText(this, "识别出错: $error", Toast.LENGTH_SHORT).show()
    }

    override fun onResults(results: Bundle?) {
        // 获取语音识别的结果
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
            val recognizedText = matches[0]
            Toast.makeText(this, "识别结果: $recognizedText", Toast.LENGTH_SHORT).show()

            // 可选：使用文字转语音朗读识别结果
            textToSpeech.speak(recognizedText, TextToSpeech.QUEUE_FLUSH, null, null)

            // 根据语音识别的结果，执行 BLE 操作
            if (recognizedText.contains("open")) {
                // 例如：如果识别结果包含 "open"，执行打开操作
                sendCommandToBleDevice("OPEN_COMMAND")
            } else if (recognizedText.contains("close")) {
                // 如果识别结果包含 "close"，执行关闭操作
                sendCommandToBleDevice("CLOSE_COMMAND")
            }
        }
    }

    // 假设这里是发送命令给 BLE 设备的函数 TODO
    private fun sendCommandToBleDevice(command: String) {
        // 通过 BLE 发送命令
        // ... 你的 BLE 发送逻辑
        TODO("need to be implement the send the command to the ble device!!!")
        Toast.makeText(this, "发送命令: $command", Toast.LENGTH_SHORT).show()
    }


    override fun onPartialResults(partialResults: Bundle?) {
        // 处理部分识别结果
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        // 处理其他事件
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
        speechRecognizer.stopListening()
        speechRecognizer.destroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

}