package ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import assets.ArduinoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ArduinoUi{
    data object Initial:ArduinoUi()
    data class Success(val message: String?):ArduinoUi()
    data class Error(val message:String?):ArduinoUi()
}
sealed class Require{
    data object Initial:Require()
    data class Success(val success: Boolean?):Require()
    data class Error(val message:String?):Require()
}
class Arduino(private val arduinoApi: ArduinoApi):ViewModel() {

    private val _arduinoState= MutableStateFlow<ArduinoUi>(ArduinoUi.Initial)
    val arduinoState: StateFlow<ArduinoUi> = _arduinoState

    private val _requireState= MutableStateFlow<Require>(Require.Initial)
    val requireState:StateFlow<Require> = _requireState
    fun getDistance(){
        viewModelScope.launch (Dispatchers.IO) {
            _arduinoState.value=ArduinoUi.Initial
            try {
                Log.d("Arduino","try")
                val response=arduinoApi.getDistance()
                _arduinoState.value=ArduinoUi.Success(response)
            }catch (e:Exception)
            {
                _arduinoState.value=ArduinoUi.Error("error:${e.message}")
                Log.d("Arduino","Error: ${e.message}")
            }
        }
    }
}