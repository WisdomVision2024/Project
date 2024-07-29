package ViewModels

import Data.HelpRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import assets.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HelpUiState{
    data object Initial:HelpUiState()
    data object Loading:HelpUiState()
    data class Success(val helpList:List<HelpRequest>?):HelpUiState()
    data class Error(val message:String?):HelpUiState()
}
sealed class AcceptUiState{
    data object Initial:AcceptUiState()
    data class Success(val helpRequest: HelpRequest?,val message: String?):AcceptUiState()
    data class Error(val message:String?):AcceptUiState()
}
sealed class CancelUiState{
    data object Initial:CancelUiState()
    data class Success(val message: String?):CancelUiState()
    data class Error(val message:String?):CancelUiState()
}

class HelpList(private val apiService: ApiService) :ViewModel()
{
    private val _helpListState= MutableStateFlow<HelpUiState>(HelpUiState.Initial)
    val helpListState: StateFlow<HelpUiState> = _helpListState

    private val _acceptState= MutableStateFlow<AcceptUiState>(AcceptUiState.Initial)
    val acceptUiState:StateFlow<AcceptUiState> = _acceptState

    private val _cancelState= MutableStateFlow<CancelUiState>(CancelUiState.Initial)
    val cancelUiState:StateFlow<CancelUiState> = _cancelState
    init {
        getHelpList()
    }
    fun getHelpList(){
        viewModelScope.launch (Dispatchers.IO) {
            _helpListState.value=HelpUiState.Loading
            try {
                val response=apiService.getRequire()
                if (response.isSuccessful){
                    val helpList=response.body()?.request
                    _helpListState.value=HelpUiState.Success(helpList)
                }
                else{
                    val errorMessage=response.body()?.errorMessage
                    _helpListState.value=HelpUiState.Error(errorMessage)
                }
            }catch (e:Exception)
            {
                _helpListState.value=HelpUiState.Error("error:${e.message}")
            }
        }
    }

    fun acceptCommission(id:String,account:String){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response=apiService.acceptCommission(id,account)
                if (response.isSuccessful){
                    val success=response.body()?.success
                    val message=response.body()?.message
                    if (success == true){
                        val helpRequest=response.body()?.helpRequest
                        _acceptState.value=AcceptUiState.Success(helpRequest,message)
                    }
                    else{
                        _acceptState.value=AcceptUiState.Error(message)
                    }
                }
                else{
                    val errorMessage=response.body()?.message
                    _acceptState.value=AcceptUiState.Error(errorMessage)
                }
            }catch (e:Exception){
                _acceptState.value=AcceptUiState.Error("error:${e}")
            }
        }
    }

    fun cancelCommission(id:String,account:String){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response=apiService.cancelCommission(id,account)
                if (response.isSuccessful){
                    val success=response.body()?.success
                    val message=response.body()?.message
                    if (success == true){
                        _cancelState.value=CancelUiState.Success(message)
                    }
                    else{
                        _cancelState.value=CancelUiState.Error(message)
                    }
                }
                else{
                    val errorMessage=response.body()?.message
                    _cancelState.value=CancelUiState.Error(errorMessage)
                }
            }catch (e:Exception){
                _cancelState.value=CancelUiState.Error("error:${e}")
            }
        }
    }
}
