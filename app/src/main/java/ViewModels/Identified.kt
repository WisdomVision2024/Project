package ViewModels


import Data.IdentifiedData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import assets.ApiService

sealed class UploadState {
    data object Initial : UploadState()
    data class Success(val result: String?) : UploadState()
    data class Error(val message: String) : UploadState()
}
sealed class HandleResult{
    data object Initial : HandleResult()
    data object NavigateRequest:HandleResult()
    data object NavigateSetting:HandleResult()
    data object LanguageChange:HandleResult()
    data object NameChange:HandleResult()
    data object PasswordChange:HandleResult()
    data object EmailChange:HandleResult()
    data object Upload:HandleResult()
}
class Identified(application: Application,
                 private val apiService: ApiService,
                 private val isPreview: Boolean = false,
                ) : AndroidViewModel(application) {

    private val voiceToTextParse = VoiceToTextParse(application)
    private val _state = MutableStateFlow(VoiceToTextParseState())
    val state: StateFlow<VoiceToTextParseState> = _state

    private val _permissionState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val permissionState: StateFlow<Map<String, Boolean>> = _permissionState

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Initial)
    val uploadState: StateFlow<UploadState> = _uploadState

    private val _handleResult= MutableStateFlow<HandleResult>(HandleResult.Initial)
    val handleResult: StateFlow<HandleResult> = _handleResult
    fun handle(){
        if (!isPreview) {
            viewModelScope.launch {
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
                Log.d("IdentifiedViewModel", "$permission is denied")
            }
        } else {
            // 所有权限都被授予
            Log.d("IdentifiedViewModel", "All permissions are granted")
        }
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
            try {
                val response = apiService.identify(IdentifiedData(text))
                if (response.isSuccessful) {
                    val ans= response.body()?.ans
                    _uploadState.value=UploadState.Success(ans)
                }
                }catch (e:Exception)
                {
                _uploadState.value=UploadState.Error( "Response body is null")
                Log.e("HomeViewModel", "Response body is null")
                }
        }
    }

    private fun processRecognizedText(text: String) {
        when {
            text.contains("change name", ignoreCase = true) ||
                    text.contains("更改名稱", ignoreCase = true) ||
                    text.contains("Changer le nom d'utilisateur", ignoreCase = true) ||
                    text.contains("ユーザー名の変更", ignoreCase = true) ||
                    text.contains("사용자 이름 변경", ignoreCase = true)
            ->{
                _handleResult.value = HandleResult.NameChange
                Log.d("HandleResult","NameChange")
            }
            text.contains("change password", ignoreCase = true) ||
                    text.contains("更改密碼", ignoreCase = true) ||
                    text.contains("changer le mot de passe", ignoreCase = true) ||
                    text.contains("パスワードを変更する", ignoreCase = true) ||
                    text.contains("비밀번호 변경", ignoreCase = true)
            ->
                {
                    _handleResult.value = HandleResult.PasswordChange
                    Log.d("HandleResult","PasswordChange")
                }
            text.contains("change email", ignoreCase = true) ||
                    text.contains("更改電子郵件", ignoreCase = true) ||
                    text.contains("更改電子郵件", ignoreCase = true) ||
                    text.contains("更改電子郵件", ignoreCase = true) ||
                    text.contains("更改電子郵件", ignoreCase = true)
            ->
                {
                    _handleResult.value = HandleResult.EmailChange
                    Log.d("HandleResult","EmailChange")
                }
            text.contains("change language",ignoreCase = true)||
                    text.contains("更改語言", ignoreCase = true)||
                    text.contains("Changer de boîte aux lettres ", ignoreCase = true)||
                    text.contains("アカウントのメールアドレスを変更する", ignoreCase = true)||
                    text.contains("사서함 변경", ignoreCase = true)
            ->
                {
                    _handleResult.value=HandleResult.LanguageChange
                    Log.d("HandleResult","LanguageChange")
                }
            text.contains("send request", ignoreCase = true) ||
                    text.contains("need help", ignoreCase = true)||
                    text.contains("發送請求", ignoreCase = true)||
                    text.contains("需要幫助", ignoreCase = true)||
                    text.contains("Besoin d'aide", ignoreCase = true)||
                    text.contains("助けが必要", ignoreCase = true)||
                    text.contains("도움이 필요하다", ignoreCase = true)
            ->{
                _handleResult.value=HandleResult.NavigateRequest
                Log.d("HandleResult","NavigateRequest")
            }
            text.contains("setting", ignoreCase = true)||
                    text.contains("設定", ignoreCase = true)||
                    text.contains("paramètre", ignoreCase = true)||
                    text.contains("설정", ignoreCase = true)
            -> {
                _handleResult.value=HandleResult.NavigateSetting
                Log.d("HandleResult","NavigateSetting")
            }
            else -> {
               upLoad(text)
                _handleResult.value=HandleResult.Upload
                Log.d("HandleResult","Upload")
            }
        }
    } }

