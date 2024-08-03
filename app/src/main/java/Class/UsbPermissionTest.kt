import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log

class UsbPermissionTest(private val context: Context) {
    companion object {
        private const val ACTION_USB_PERMISSION = "com.example.project.USB_PERMISSION"
        private const val TARGET_VID = 6975 // 供应商ID (VID)
        private const val TARGET_PID = 8775 // 产品ID (PID)
    }

    private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device: UsbDevice? =
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    if (device != null) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            Log.d("UsbPermissionTest", "Permission granted for device: $device")
                        } else {
                            Log.d("UsbPermissionTest", "Permission denied for device: $device")
                        }
                    } else {
                        Log.d("UsbPermissionTest", "Device is null in onReceive")
                    }
                }
            }
        }
    }

    init {
        // Register the BroadcastReceiver to listen for the permission result
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        context.registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    fun requestUsbPermission() {
        val deviceList = usbManager.deviceList
        val usbDevice = deviceList.values.find {
            it.vendorId == TARGET_VID && it.productId == TARGET_PID
        }

        if (usbDevice != null) {
            val intent = Intent(ACTION_USB_PERMISSION).apply {
                putExtra(UsbManager.EXTRA_DEVICE, usbDevice)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            usbManager.requestPermission(usbDevice, pendingIntent)
            Log.d("UsbPermissionTest", "Permission request initiated for device: $usbDevice")
        } else {
            Log.d("UsbPermissionTest", "No matching USB device found")
        }
    }

    fun release() {
        context.unregisterReceiver(usbReceiver)
    }
}
