package Class

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class UsbCameraManager(private val context: Context) {

    companion object {
        private const val ACTION_USB_PERMISSION = "com.example.project.USB_PERMISSION"
        private const val TARGET_VID = 0x1B3F // 供应商ID (VID)
        private const val TARGET_PID = 0x2247 // 产品ID (PID)
    }

    private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var usbDevice: UsbDevice? = null
    private var usbInterface: UsbInterface? = null
    private var usbEndpoint: UsbEndpoint? = null
    private var usbDeviceConnection: UsbDeviceConnection? = null

    private val _imageLiveData = MutableLiveData<ByteArray>()
    val imageLiveData: LiveData<ByteArray> get() = _imageLiveData

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device: UsbDevice? =
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.apply {
                            connectToDevice(this)
                        }
                    } else {
                        Log.d("UsbCameraManager", "Permission denied for device $device")
                    }
                }
            }
        }
    }

    init {
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        context.registerReceiver(usbReceiver, filter,Context.RECEIVER_NOT_EXPORTED)
    }

    fun initialize() {
        val deviceList = usbManager.deviceList
        for (device in deviceList.values) {
            if (device.vendorId == TARGET_VID && device.productId == TARGET_PID) {
                usbDevice = device
                break
            }
        }

        usbDevice?.let { device ->
            val intent = Intent(ACTION_USB_PERMISSION).apply {
                setPackage(context.packageName)
            }
            val pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_IMMUTABLE)
            usbManager.requestPermission(device, pendingIntent)
        }
    }

    private fun connectToDevice(device: UsbDevice) {
        usbInterface = device.getInterface(0)
        usbEndpoint = usbInterface?.getEndpoint(0)
        if (usbInterface != null && usbEndpoint != null) {
            usbDeviceConnection = usbManager.openDevice(device)
            usbDeviceConnection?.claimInterface(usbInterface, true)
            startCapture()
        }
    }

    private fun startCapture() {
        usbDeviceConnection?.let { connection ->
            usbEndpoint?.let { endpoint ->
                scope.launch {
                    val buffer = ByteBuffer.allocate(endpoint.maxPacketSize)
                    while (isActive) {
                        val received = connection.bulkTransfer(endpoint, buffer.array(), buffer.capacity(), 1000)
                        if (received > 0) {
                            val imageData = ByteArray(received)
                            buffer.get(imageData, 0, received)
                            buffer.clear()
                            _imageLiveData.postValue(imageData)
                            delay(1000) // 每秒拍摄一张图片
                        }
                    }
                }
            }
        }
    }

    fun stopCapture() {
        job.cancel()
    }

    fun release() {
        context.unregisterReceiver(usbReceiver)
        stopCapture()
        usbDeviceConnection?.releaseInterface(usbInterface)
        usbDeviceConnection?.close()
    }
}