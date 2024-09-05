package ViewModels

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Locale

class TTS(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var onInitListener: (() -> Unit)? = null

    init {
        tts = TextToSpeech(application, this)
    }


    override fun onInit(status: Int) {
        val systemLocale = Locale.getDefault()
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = systemLocale
            onInitListener?.invoke()
        }
    }

    fun setOnInitListener(listener: () -> Unit) {
        onInitListener = listener  // 设置自定义的初始化监听器
    }

    fun speak(text: String) {
        viewModelScope.launch {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stop() {
        tts?.stop()
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}