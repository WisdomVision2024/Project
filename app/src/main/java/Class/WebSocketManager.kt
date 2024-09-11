package Class

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.project.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.Locale

class WebSocketManager (private val context: Context) {
    private var client: OkHttpClient? = null
    private var webSocket: WebSocket? = null

    private val _messageFlow = MutableStateFlow<String>("")
    private val messageFlow: StateFlow<String> get() = _messageFlow

    fun start() {
        client = OkHttpClient()

        val request = Request.Builder()
            .url("http://163.13.201.104:8080/getunity") // 替換為你的 WebSocket URL
            .build()

        webSocket = client?.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("webSocket","open")
                // WebSocket 連接成功
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // 當接收到伺服器的新消息時，處理並發送通知
                Log.d("webSocket","get")
                _messageFlow.value=text
                handleIncomingMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
            }
        })

        client?.dispatcher?.executorService?.shutdown()
    }

    private fun handleIncomingMessage(message: String) {
        val systemLocale: String = Locale.getDefault().toLanguageTag()
        // 這裡可以根據接收到的消息內容來決定通知的內容
        when (systemLocale) {
            "en-rUS" -> sendNotification(
                "Visually impaired people need assistance",
                "Click to view details"
            )
            "zh-rTW" -> sendNotification(
                "有視障者需要協助",
                "點擊查看詳細資訊"
            )
            "fr" -> sendNotification(
                "Les personnes malvoyantes ont besoin d'aide",
                "Cliquez pour voir les détails"
            )
            "ja" -> sendNotification(
                "視覚障害者は支援が必要です",
                "クリックして詳細を表示"
            )
            "ko-rKR" -> sendNotification(
                "시각 장애인은 도움이 필요합니다",
                "세부정보를 보려면 클릭하세요."
            )
            else -> sendNotification(
                "Visually impaired people need assistance",
                "Click to view details"
            )
        }
    }


    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel("data_channel", "有視障者需要協助", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, "websocket_channel")
            .setSmallIcon(R.drawable.notifiction_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }

    fun stop() {
        webSocket?.close(1000, null)
        client = null
    }

    fun receiveMessageFlow(): StateFlow<String> = messageFlow
}