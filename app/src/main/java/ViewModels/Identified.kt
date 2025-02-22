package ViewModels


import Class.VoiceToTextParse
import Class.VoiceToTextParseState
import Data.IdentifiedData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import assets.ApiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

sealed class HandleResult{
    data object Initial : HandleResult()
    data object Loading:HandleResult()
    data object NameChange:HandleResult()
    data object PasswordChange:HandleResult()
    data object EmailChange:HandleResult()
    data object NeedHelp:HandleResult()
    data object Focus:HandleResult()
    data object Upload:HandleResult()
    data object Arduino:HandleResult()
}
sealed class UploadState {
    data object Initial : UploadState()
    data class Success(val result: String?) : UploadState()
    data class Error(val message: String) : UploadState()
    data object Loading:UploadState()
}
sealed class SendState {
    data object Initial : SendState()
    data class Success(val result: String?) : SendState()
    data class Error(val message: String) : SendState()
}
sealed class PermissionState{
    data object Initial : PermissionState()
    data object RequestPermissionsAgain:PermissionState()
}
class Identified(application: Application,
                 private val apiService: ApiService,
                 private val isPreview: Boolean = false
                ) : AndroidViewModel(application)
{
    private var timerJob: Job? = null

    private val voiceToTextParse = VoiceToTextParse(application)
    private val _state = MutableStateFlow(VoiceToTextParseState())
    val state: StateFlow<VoiceToTextParseState> = _state

    private val _permissionState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val permissionState: StateFlow<Map<String, Boolean>> = _permissionState

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Initial)
    val uploadState: StateFlow<UploadState> = _uploadState

    private val _sendState = MutableStateFlow<SendState>(SendState.Initial)
    val sendState: StateFlow<SendState> = _sendState

    private val _handleResult= MutableStateFlow<HandleResult>(HandleResult.Initial)
    val handleResult: StateFlow<HandleResult> = _handleResult

    private val _permissions= MutableStateFlow<PermissionState>(PermissionState.Initial)
    val permissions:StateFlow<PermissionState> = _permissions

    private val _showPermissionRationale = MutableLiveData<Boolean>()
    val showPermissionRationale: LiveData<Boolean> = _showPermissionRationale
    init {
        if (!isPreview) {
            viewModelScope.launch {
                Log.d("Identified", "Identified ViewModel initialized with application: $application")
                voiceToTextParse.state.collectLatest { newState ->
                    _state.value = newState
                    processRecognizedText(newState.spokenText)
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
                _showPermissionRationale.value = true
                Log.d("IdentifiedViewModel", "$permission is denied")
            }
        } else {
            // 所有权限都被授予
            Log.d("IdentifiedViewModel", "All permissions are granted")
        }
    }

    fun onPermissionRationaleShown() {
        _showPermissionRationale.value = false
    }

    fun requestPermissionsAgain() {
        _permissions.value=PermissionState.RequestPermissionsAgain
    }

    fun startListening() {
        if (!isPreview) {
            voiceToTextParse.startListening()
        }
    }
    fun stopListening() {
        if (!isPreview) {
            voiceToTextParse.stopListening()
        }
    }

    private fun upLoad(text: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _uploadState.value=UploadState.Loading
            try {
                val response = apiService.identify(IdentifiedData(text))
                if (response.isSuccessful) {
                    val ans= response.body()?.ans
                    Log.d("identified response","$ans")
                    _uploadState.value=UploadState.Success(ans)
                }
                }catch (e:Exception)
                {
                _uploadState.value=UploadState.Error(e.message.toString())
                Log.e("identified response", "$e")
                }
        }
    }

    private fun focusUpLoad(text: String?) {
        _uploadState.value=UploadState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.focusIdentify(IdentifiedData(text))
                if (response.isSuccessful) {
                    val ans= response.body()?.ans
                    Log.d("identified response","$ans")
                    _uploadState.value=UploadState.Success(ans)
                }
            }catch (e:Exception)
            {
                _uploadState.value=UploadState.Error( "Response body is null")
                Log.e("identified response", "Response body is null")
            }
        }
    }

    fun getName(){
        viewModelScope.launch {
            val response=apiService.sendRequire()
            try {
                if (response.isSuccessful){
                    val name=response.body()?.message?.name.toString()
                    _uploadState.value=UploadState.Success("協助者${name}即將前往協助您")
                    Log.d("help", name)
                }
            }catch (e:Exception){
                _uploadState.value=UploadState.Error(e.message.toString())
                e.printStackTrace()
            }
        }
    }

    fun cancel(){
        timerJob?.cancel()
    }

    private fun processRecognizedText(text: String) {
        _handleResult.value=HandleResult.Initial
        if (text!=""){
            when {
                (text.contains("change", ignoreCase = true) &&text.contains("name", ignoreCase = true))||(
                        (text.contains("更改", ignoreCase = true)||text.contains("更換", ignoreCase = true))&&
                                (text.contains("名字", ignoreCase = true)||text.contains("名稱", ignoreCase = true))
                        )
                        ||
                        text.contains("Changer le nom d'utilisateur", ignoreCase = true) ||
                        text.contains("ユーザー名の変更", ignoreCase = true) ||
                        text.contains("사용자 이름 변경", ignoreCase = true)
                ->{
                    _handleResult.value = HandleResult.NameChange
                    Log.d("HandleResult","NameChange")
                }
                text.contains("change password", ignoreCase = true) ||(
                        (text.contains("更改", ignoreCase = true)||text.contains("更換", ignoreCase = true))&&
                                text.contains("密碼", ignoreCase = true)) ||
                        text.contains("changer le mot de passe", ignoreCase = true) ||
                        text.contains("パスワードを変更する", ignoreCase = true) ||
                        text.contains("비밀번호 변경", ignoreCase = true)
                ->
                {
                    _handleResult.value = HandleResult.PasswordChange
                    Log.d("HandleResult","PasswordChange")
                }
                text.contains("change email", ignoreCase = true) ||(
                        (text.contains("更改", ignoreCase = true)||text.contains("更換", ignoreCase = true))&&
                                text.contains("郵件", ignoreCase = true)||text.contains("信箱", ignoreCase = true) )||
                        text.contains("Changer de boîte aux lettres ", ignoreCase = true) ||
                        text.contains("アカウントのメールアドレスを変更する", ignoreCase = true) ||
                        text.contains("사서함 변경", ignoreCase = true)
                ->
                {
                    _handleResult.value = HandleResult.EmailChange
                    Log.d("HandleResult","EmailChange")
                }
                text.contains("send request", ignoreCase = true) ||
                        text.contains("need help", ignoreCase = true)||
                        (text.contains("需要", ignoreCase = true)&&
                                (text.contains("幫助", ignoreCase = true)||
                                        text.contains("協助", ignoreCase = true) )
                                )||
                        text.contains("Besoin d'aide", ignoreCase = true)||
                        text.contains("助けが必要", ignoreCase = true)||
                        text.contains("도움이 필요하다", ignoreCase = true)
                ->{
                    _handleResult.value=HandleResult.NeedHelp
                    Log.d("HandleResult","NeedHelp")
                }
                text.contains("focus on", ignoreCase = true)||
                        text.contains("關注", ignoreCase = true)||
                        text.contains("paramètre", ignoreCase = true)||
                        text.contains("焦点を当てる", ignoreCase = true)||
                        text.contains("에 집중하다", ignoreCase = true)
                -> {
                    _handleResult.value=HandleResult.Focus
                    focusUpLoad(text)
                    Log.d("HandleResult","focus")
                }
                text.contains("distance", ignoreCase = true)||
                        text.contains("距離", ignoreCase = true)
                ->{
                    _handleResult.value=HandleResult.Arduino
                }
                else -> {
                    _handleResult.value=HandleResult.Upload
                    upLoad(text)
                    Log.d("HandleResult","Upload")
                }
            }
        }
        else{
            _handleResult.value=HandleResult.Loading
            Log.d("HandleResult","Loading")
        }
    }
    fun resetHandleState(){
        _handleResult.value=HandleResult.Initial
    }
                }

