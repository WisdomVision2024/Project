package ViewModels


import Data.UpdateResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import assets.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import Data.User
import DataStore.LoginDataStore
import DataStore.LoginState
import android.util.Log

sealed class UpdateUiState {
    data object Initial : UpdateUiState()
    data class Success(val updateResponse:UpdateResponse?) : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
}
class Setting(private val apiService: ApiService,private val loginDataStore: LoginDataStore
) : ViewModel()
{
    private val _updateUiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Initial)
    val updateState: StateFlow<UpdateUiState> = _updateUiState


    fun changeName(account:String,
                   name:String?,
                   email:String?,
                   newPassword:String?,
                   oldPassword:String
                   ){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = apiService.update(User(account,name,email,oldPassword,newPassword))
                if (response.isSuccessful){
                    if (response.body()?.success == true){
                        val body=response.body()
                        Log.d("update","$body")
                        _updateUiState.value=UpdateUiState.Success(response.body())
                    }
                    else{
                        _updateUiState.value=UpdateUiState.Error(
                            response.body()?.errorMessage ?:"Update failed"
                        )
                    }
                }
                else{_updateUiState.value=UpdateUiState.Error(response.message())}
            }
            catch (e: Exception){
                _updateUiState.value=UpdateUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun logOut(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                loginDataStore.saveLoginState(LoginState(isLoggedIn = false))
                Log.d("Log Out","Log Out Success")
                _updateUiState.value=UpdateUiState.Success(null)
            }catch (e:Exception){
                Log.d("Log Out","Log Out Failed")
                _updateUiState.value=UpdateUiState.Error("Unknown Error")
            }
        }
    }
}