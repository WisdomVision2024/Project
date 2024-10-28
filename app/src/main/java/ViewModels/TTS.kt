package ViewModels

import DataStore.Speed
import DataStore.SpeedStore
import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class TTS(application: Application,
          private val speedStore: SpeedStore) :
    AndroidViewModel(application), TextToSpeech.OnInitListener {

    var tts: TextToSpeech? = null
    private var onInitListener: (() -> Unit)? = null



    init {
        tts = TextToSpeech(application, this)
    }


    override fun onInit(status: Int) {
        val systemLocale = Locale.getDefault()
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = systemLocale
            onInitListener?.invoke()
            observeSpeed()
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

    private fun observeSpeed() {
        viewModelScope.launch {
            speedStore.loadSpeedState().collect { speed ->
                tts?.setSpeechRate(speed.ttsSpeed ?: 1.0f) // 根据 speedStore 设置语速
            }
        }
    }

    fun saveSpeed(speed:Float){
        viewModelScope.launch (Dispatchers.IO){
            try {
                speedStore.saveSpeedState(Speed(speed))
            }catch (e:Exception){
                Log.e("Speed",e.message.toString())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
