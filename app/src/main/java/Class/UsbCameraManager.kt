package Class

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import android.widget.Toast
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
        private const val TARGET_VID = 6975 // 供应商ID (VID)
        private const val TARGET_PID = 8775 // 产品ID (PID)
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
                    Log.d("UsbCameraManager", "Device found: $usbDevice")
                    if (device!=null){
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device.apply {
                                connectToDevice(this)
                            }
                        } else {
                            Log.d("UsbCameraManager", "Permission denied for device $device")
                        }
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
                val name=device.deviceName
                Log.d("Initialize","Success usbDevice:${name}")
                break
            }
        }

        usbDevice?.let { device ->
            val intent = Intent(ACTION_USB_PERMISSION).apply {
                putExtra(UsbManager.EXTRA_DEVICE,device)
            }
            val pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_IMMUTABLE)
            usbManager.requestPermission(device, pendingIntent)
            Log.d("UsbCameraManager", "Permission request initiated for device: $device")
        }
    }

    private fun connectToDevice(device: UsbDevice) {
        usbInterface = device.getInterface(0)
        usbEndpoint = usbInterface?.getEndpoint(0)
        if (usbInterface != null && usbEndpoint != null) {
            usbDeviceConnection = usbManager.openDevice(device)
            val claimed = usbDeviceConnection?.claimInterface(usbInterface, true) ?: false
            if (claimed) {
                Log.d("UsbCameraManager", "Device connected successfully")
            } else {
                Log.d("UsbCameraManager", "Failed to claim interface")
            }
        } else {
            Log.d("UsbCameraManager", "Failed to get interface or endpoint")
        }
    }
    fun captureImage() {
        if (usbDeviceConnection == null || usbEndpoint == null) {
            Log.d("UsbCameraManager", "usbDeviceConnection or usbEndpoint is null")
            return
        }
        usbDeviceConnection?.let { connection ->
            usbEndpoint?.let { endpoint ->
                CoroutineScope(Dispatchers.IO).launch {
                    val buffer = ByteBuffer.allocate(endpoint.maxPacketSize)
                    val received = connection.bulkTransfer(endpoint, buffer.array(), buffer.capacity(), 1000)
                    if (received > 0) {
                        val imageData = ByteArray(received)
                        buffer.get(imageData, 0, received)
                        buffer.clear()
                        _imageLiveData.postValue(imageData)
                        Log.d("UsbCameraManager", "Image captured and posted to LiveData. Data size: $received bytes")
                    } else {
                        Log.d("UsbCameraManager", "Failed to capture image. Received bytes: $received")
                    }
                }
            }
        } ?: Log.d("UsbCameraManager", "usbDeviceConnection or usbEndpoint is null")
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