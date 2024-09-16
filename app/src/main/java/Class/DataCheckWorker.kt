package Class

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import assets.RetrofitInstance
import com.example.project.MainActivity
import com.example.project.R
import kotlinx.coroutines.runBlocking
import java.util.Locale

class DataCheckWorker(context: Context,
                      params: WorkerParameters) : Worker(context, params) {
    private val systemLocale: String = Locale.getDefault().toLanguageTag()
    private val repository = HelpRepository(RetrofitInstance.apiService)

    override fun doWork(): Result {
        // 這裡進行伺服器數據檢查邏輯
        return runBlocking {
            val hasNewData = repository.fetchHelpData()
            // 如果有新數據，發送通知
            if (hasNewData!=null) {
                Log.d("systemLocale",systemLocale)
                when (systemLocale) {
                    "en-rUS" -> sendNotification(
                        "Visually impaired people need assistance"
                    )
                    "zh-rTW" -> sendNotification(
                        "有視障者需要協助"
                    )
                    "fr" -> sendNotification(
                        "Les personnes malvoyantes ont besoin d'aide"
                    )
                    "ja" -> sendNotification(
                        "視覚障害者は支援が必要です"
                    )
                    "ko-rKR" -> sendNotification(
                        "시각 장애인은 도움이 필요합니다"
                    )
                    else -> sendNotification(
                        "有視障者需要協助"
                    )
                }
            }
            Result.success()
        }
    }

    private fun sendNotification(title: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_help_list", true) // 传递导航标志
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 及以上需要創建 NotificationChannel

        val channel = NotificationChannel("data_channel", "有視障者需要協助", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)


        // 創建通知
        val notification = NotificationCompat.Builder(applicationContext, "data_channel")
            .setSmallIcon(R.drawable.notifiction_foreground)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)  // 设置点击通知后启动的意图
            .setAutoCancel(true)
            .build()

        // 發送通知
        notificationManager.notify(1, notification)
    }
}
