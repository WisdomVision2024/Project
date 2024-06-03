package ViewModels


import Data.IdentifiedData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import assets.ApiService

class Identified(application: Application,
                 private val apiService: ApiService,
                 private val isPreview: Boolean = false) : AndroidViewModel(application) {
    private val voiceToTextParse = VoiceToTextParse(application)
    private val _state = MutableStateFlow(VoiceToTextParseState())
    val state: StateFlow<VoiceToTextParseState> = _state
    private val _permissionState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val permissionState: StateFlow<Map<String, Boolean>> = _permissionState
    private val _uploadState = MutableStateFlow<List<String?>>(emptyList())
    val uploadState: StateFlow<List<String?>> = _uploadState
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        if (!isPreview) {
            viewModelScope.launch {
                voiceToTextParse.state.collectLatest { newState ->
                    _state.value = newState
                }
                permissionState.collectLatest { permissions ->
                    // 处理权限状态
                    handlePermissions(permissions)
                }
            }
        }
    }
    fun checkPermissions(granted: Map<String, Boolean>, denied: Map<String, Boolean>) {
        // 更新权限状态
        _permissionState.value = granted + denied
    }

    private fun handlePermissions(permissions: Map<String, Boolean>) {
        // 进一步处理权限，例如根据权限状态启用/禁用功能
        val deniedPermissions = permissions.filterValues { !it }
        if (deniedPermissions.isNotEmpty()) {
            // 有被拒绝的权限
            // 执行相应的操作，例如提示用户需要授予权限
            deniedPermissions.forEach { (permission, _) ->
                Log.d("IdentifiedViewModel", "$permission is denied")
            }
        } else {
            // 所有权限都被授予
            Log.d("IdentifiedViewModel", "All permissions are granted")
        }
    }

    fun startListening(languageCode: String) {
        if (!isPreview) {
            voiceToTextParse.startListening(languageCode)
        }
    }
    fun stopListening() {
        if (!isPreview) {
            voiceToTextParse.stopListening()
        }
    }

    fun upLoad(text: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch {
                val response = apiService.identify(IdentifiedData(text))
                try {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _uploadState.value = it.ans
                            _errorMessage.value = null
                        }
                    }
                }catch (e:Exception)
                {
                    _errorMessage.value = "Response body is null"
                    Log.e("HomeViewModel", "Response body is null")
                    }
                }
            }
        }

    fun processRecognizedText(text: String, navController: NavHostController) {
        val settingPageRoute = "SettingPage"
        when {
            text.contains("change name", ignoreCase = true) -> {
                navController.navigate("$settingPageRoute?screen=name")
            }
            text.contains("change password", ignoreCase = true) -> {
                navController.navigate("$settingPageRoute?screen=password")
            }
            text.contains("change email", ignoreCase = true) -> {
                navController.navigate("$settingPageRoute?screen=email")
            }
            text.contains("",ignoreCase = true)->{
                navController.navigate("$settingPageRoute?screen=language")
            }
            text.contains("send request", ignoreCase = true) -> {
                navController.navigate("RequestPage")
            }
            else -> {
               null
            }
        }
    }
                 }

