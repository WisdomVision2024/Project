package ViewModels

import Class.WebSocketManager
import Data.HelpRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import assets.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HelpUiState{
    data object Initial:HelpUiState()
    data object Loading:HelpUiState()
    data class Success(val helpList:HelpRequest?):HelpUiState()
    data class Error(val message:String?):HelpUiState()
}

class HelpList(private val apiService: ApiService,
               private val webSocketManager: WebSocketManager) :ViewModel()
{
    private val _helpListState= MutableStateFlow<HelpUiState>(HelpUiState.Initial)
    val helpListState: StateFlow<HelpUiState> = _helpListState

    fun getHelp(){
        viewModelScope.launch (Dispatchers.IO) {
            _helpListState.value=HelpUiState.Loading
            try {
                val response=apiService.getRequire()
                if (response.isSuccessful){
                    val help=response.body()?.request
                    _helpListState.value=HelpUiState.Success(help)
                }
                else{
                    val errorMessage=response.body()?.errorMessage
                    _helpListState.value=HelpUiState.Error(errorMessage)
                }
            }catch (e:Exception)
            {
                _helpListState.value=HelpUiState.Error("error:${e.message}")
            }
        }
    }
    fun startWebSocket() {
        webSocketManager.start()
        listenToWebSocketMessages()
    }

    private fun listenToWebSocketMessages() {
        viewModelScope.launch {
            webSocketManager.receiveMessageFlow().collect { message ->
                handleWebSocketMessage(message)
            }
        }
    }

    private fun handleWebSocketMessage(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            // 處理 WebSocket 消息並更新 UI 狀態
            _helpListState.value = HelpUiState.Loading
            try {
                val help = parseMessageToHelpRequest(message)
                _helpListState.value = HelpUiState.Success(help)
            } catch (e: Exception) {
                _helpListState.value = HelpUiState.Error("Error parsing message: ${e.message}")
            }
        }
    }

    private fun parseMessageToHelpRequest(message: String): HelpRequest? {
        // 假設消息是 JSON 格式，這裡進行解析
        // 這裡需要根據你的實際消息格式進行解析
        return Gson().fromJson(message, HelpRequest::class.java)
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.stop()
    }
}
