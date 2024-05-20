package ViewModels

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KProperty


class Identified : ViewModel(){
    var isUploadSuccess by mutableStateOf(false)
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    fun setupPermissionLauncher(context: Context) {
        permissionLauncher = (context as MainActivity).registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            setupPermissionLauncher(permissions, context)
        }
    }

    fun setupPermissionLauncher(permissions: Map<String, Boolean>, context: Context) {
        val isRecordAudioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (!isRecordAudioGranted) {
            showPermissionDeniedDialog(context)
        }
    }

    private fun showPermissionDeniedDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Permission Required")
            .setMessage("This app needs audio recording permission to function properly. Please grant the permission.")
            .setPositiveButton("Grant Permission") { _, _ ->
                requestRecordAudioPermission(context)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun requestRecordAudioPermission(context: Context) {
        permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
    }

    private fun isPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startRecording(context: Context, onRecordingStarted: (String) -> Unit) {
        val recordingFile = createRecordingFile(context)
        if (!isPermissionGranted(context)) {
            requestRecordAudioPermission(context)
            throw SecurityException("RECORD_AUDIO permission not granted")
        }
        var recorder: MediaRecorder? = null
        try {
            // Check if the RECORD_AUDIO permission is granted
            if (isPermissionGranted(context)) {
                recorder = MediaRecorder(context)
                recorder.run {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setOutputFile(recordingFile.absolutePath)
                    prepare()
                    start()
                }
                // Recording started successfully
                onRecordingStarted(recordingFile.absolutePath)
            } else {
                requestRecordAudioPermission(context)
                // Permission not granted
                throw SecurityException("RECORD_AUDIO permission not granted")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle exceptions
        } finally {
            // Release resources
            // If recording fails or an exception occurs, make sure to release MediaRecorder
            recorder?.release()
        }
    }

    fun stopRecording(context: Context, recordingFilePath: String) {
        val recorder = MediaRecorder(context)

        try {
            recorder.stop()
            recorder.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!isUploadSuccess) {
            uploadRecordingFile(context, recordingFilePath) { success ->
                if (success) {
                    isUploadSuccess = true
                }
            }
        }
    }

    fun uploadRecordingFile(context: Context, filePath: String, onComplete: (Boolean) -> Unit) {
        // Placeholder for uploading logic
        // Simulate uploading success after a delay
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000) // Simulate network delay
            onComplete(true) // Simulate upload success
        }
    }

    fun deleteRecordingFile(context: Context, filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createRecordingFile(context: Context): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val recordingFileName = "Recording_$timestamp.mp4"
        val filesDir = context.filesDir
        return File(filesDir, recordingFileName)
    }

    operator fun getValue(mainActivity: MainActivity, property: KProperty<*>): Identified {
        return ViewModelProvider(mainActivity)[Identified::class.java]
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 101
    }

}