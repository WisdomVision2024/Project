package acitivity

import android.content.Context
import java.net.NetworkInterface
import java.util.Collections

fun getMacAddress(context: Context): String {
    try {
        val all: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (nif in all) {
            if (!nif.name.equals("wlan0", ignoreCase = true)) continue

            val macBytes = nif.hardwareAddress ?: return ""

            val res1 = StringBuilder()
            for (b in macBytes) {
                res1.append(String.format("%02X:", b))
            }

            if (res1.isNotEmpty()) {
                res1.deleteCharAt(res1.length - 1)
            }
            return res1.toString()
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return "02:00:00:00:00:00"
}