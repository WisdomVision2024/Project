package ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import assets.ArduinoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed class ArduinoUi{
    data object Initial:ArduinoUi()
    data object Loading:ArduinoUi()
    data class Success(val message: String?):ArduinoUi()
    data class Error(val message:String?):ArduinoUi()
}
sealed class Require{
    data object Initial:Require()
    data class Success(val success: Boolean?):Require()
    data class Error(val message:String?):Require()
}

class Arduino(private val arduinoApi: ArduinoApi,
              private val tts: TTS):ViewModel() {

    private val _arduinoState= MutableStateFlow<ArduinoUi>(ArduinoUi.Initial)
    val arduinoState: StateFlow<ArduinoUi> = _arduinoState

    private var timerJob: Job? = null

    fun continuedGet(interval: Long = 1000L){
        timerJob =viewModelScope.launch (Dispatchers.IO) {
            while (isActive){
                try {
                    Log.d("Arduino","try")
                    val response=arduinoApi.getDistance()
                    Log.d("Arduino", "Response: $response")
                    handleResult(response)
                }catch (e:Exception)
                {
                    _arduinoState.value=ArduinoUi.Error("error:${e.message}")
                    e.printStackTrace()
                    Log.d("Arduino","Error: ${e.message}")
                    stop()
                }
                delay(interval)
            }
        }
    }

    private fun handleResult(text:String){
        val regex = Regex("""\d+(\.\d+)?""")
        val result=regex.find(text)?.value?.toFloatOrNull()
        if (result != null) {
            if (result<80.0){
                tts.speak("小心")
            }
        }
        else {
            Log.d("Arduino", "Invalid distance format in response: $text")
        }
    }

    fun stop(){
        if (timerJob?.isActive == true) {  // 检查是否在活动状态
            timerJob?.cancel()
            Log.d("Arduino", "Timer job cancelled.")
        }
    }

    fun getDistance(){
        viewModelScope.launch (Dispatchers.IO) {
            _arduinoState.value=ArduinoUi.Initial
            try {
                Log.d("Arduino","try")
                val response=arduinoApi.getDistance()
                Log.d("Arduino", "Response: $response")
                _arduinoState.value=ArduinoUi.Success(response)
            }catch (e:Exception)
            {
                _arduinoState.value=ArduinoUi.Error("error:${e.message}")
                Log.d("Arduino","Error: ${e.message}")
            }
        }
    }
}