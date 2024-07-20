package ViewModels

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BlueTooth(application: Application) : AndroidViewModel(application){
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var outputStream: OutputStream
    private lateinit var inputStream: InputStream

    private val _newMessageReceived = MutableLiveData<String>()
    val newMessageReceived: LiveData<String> = _newMessageReceived

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager =
            application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter }

    private val bluetoothDeviceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = intent.getParcelableExtra(
                    BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java
                )
                if (device != null && device.name == "HC-06") {
                    if (ActivityCompat.checkSelfPermission(getApplication(),
                            android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("Bluetooth", "Bluetooth permission not granted")
                        return
                    }
                    // 找到目标设备，停止扫描并连接
                    bluetoothAdapter?.cancelDiscovery()
                    connectToDevice(device)
                }
            }
        }
    }

    init {
        // 注册蓝牙设备扫描的广播接收器
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        getApplication<Application>().registerReceiver(bluetoothDeviceReceiver, filter)
    }

    fun startDeviceScan() {
        if (ActivityCompat.checkSelfPermission(getApplication(),
                android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Bluetooth", "Bluetooth permission not granted")
            return
        }
        bluetoothAdapter?.startDiscovery()
    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(getApplication(),
                android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Bluetooth", "Bluetooth permission not granted")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uuid: UUID = device.uuids[0].uuid
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket.connect()
                outputStream = bluetoothSocket.outputStream
                inputStream = bluetoothSocket.inputStream
                Log.d("Bluetooth", "Connected to device")
                listenForIncomingMessages()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Bluetooth", "Error connecting to device", e)
            }
        }
    }


    fun sendData(data: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                outputStream.write(data.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Bluetooth", "Error sending data", e)
            }
        }
    }

    private fun listenForIncomingMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val buffer = ByteArray(1024)
                while (true) {
                    val bytes = inputStream.read(buffer)
                    val message = String(buffer, 0, bytes)
                    _newMessageReceived.postValue(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Bluetooth", "Error receiving data", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::bluetoothSocket.isInitialized) {
            bluetoothSocket.close()
        }
    }

    fun playAlertSound(context: Context) {
        val mediaPlayer = MediaPlayer().apply {
            val alertSoundUri = Uri.parse("android.resource://${context.packageName}/raw/shrillwhistle3")
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
            setDataSource(context, alertSoundUri)
            prepare()
            start()
        }
    }
}