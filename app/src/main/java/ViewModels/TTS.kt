package ViewModels

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Locale

class TTS(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(application, this)
    }

    override fun onInit(status: Int) {
        val systemLocale = Locale.getDefault()
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = systemLocale
        }
    }

    fun speak(text: String) {
        viewModelScope.launch {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}