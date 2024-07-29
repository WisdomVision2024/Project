package ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class Help(): ViewModel() {
    private val _showFinishScreen = MutableLiveData(false)
    val showFinishScreen: LiveData<Boolean> get() = _showFinishScreen
    private val client = OkHttpClient()
    val request = Request.Builder().url("http://163.13.201.104:8080/").build()
    private lateinit var webSocket: WebSocket

    init {
        initWebSocket()
    }
    private fun initWebSocket() {
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                if (text == "finish") {
                    _showFinishScreen.postValue(true)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                // Handle WebSocket failure if needed
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                webSocket.close(code, reason)
                // Optionally, reinitialize WebSocket connection
                initWebSocket()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                // Optionally, reinitialize WebSocket connection
                initWebSocket()
            }
        })
    }

    fun closeScreen(){
        _showFinishScreen.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        webSocket.close(1000, "ViewModel cleared")
    }
}