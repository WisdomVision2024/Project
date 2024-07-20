package acitivity

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket

class NetworkManager {
    private var socket: Socket? = null
    private var outputStream: OutputStream? = null
    private var bufferedReader: BufferedReader? = null

    suspend fun connectToServer(ipAddress: String, port: Int) {
        withContext(Dispatchers.IO) {
            try {
                socket = Socket(ipAddress, port)
                outputStream = socket?.getOutputStream()
                bufferedReader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                Log.d("NetworkManager", "Connected to server")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("NetworkManager", "Error connecting to server", e)
            }
        }
    }

    suspend fun sendData(data: String) {
        withContext(Dispatchers.IO) {
            try {
                outputStream?.write((data + "\n").toByteArray())
                outputStream?.flush()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("NetworkManager", "Error sending data", e)
            }
        }
    }

    suspend fun receiveData(): String? {
        return withContext(Dispatchers.IO) {
            try {
                bufferedReader?.readLine()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("NetworkManager", "Error receiving data", e)
                null
            }
        }
    }

    fun closeConnection() {
        try {
            socket?.close()
            outputStream?.close()
            bufferedReader?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("NetworkManager", "Error closing connection", e)
        }
    }
}