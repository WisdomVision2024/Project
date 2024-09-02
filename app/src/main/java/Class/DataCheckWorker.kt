package Class

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import assets.RetrofitInstance
import com.example.project.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Locale

class DataCheckWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private val systemLocale: String = Locale.getDefault().toLanguageTag()
    override fun doWork(): Result {
        // 這裡進行伺服器數據檢查邏輯
        return runBlocking {
            val hasNewData = checkForNewDataFromServer()

            // 如果有新數據，發送通知
            if (hasNewData) {
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

            Result.success()
        }
    }
    private suspend fun checkForNewDataFromServer(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.apiService.getRequire()
                response.isSuccessful
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 及以上需要創建 NotificationChannel

        val channel = NotificationChannel("data_channel", "有視障者需要協助", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)


        // 創建通知
        val notification = NotificationCompat.Builder(applicationContext, "data_channel")
            .setSmallIcon(R.drawable.notifiction_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // 發送通知
        notificationManager.notify(1, notification)
    }

}
