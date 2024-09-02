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
    data class Success(val helpList:HelpRequest?):HelpUiState()
    data class Error(val message:String?):HelpUiState()
}

class HelpList(private val apiService: ApiService) :ViewModel()
{
    private val _helpListState= MutableStateFlow<HelpUiState>(HelpUiState.Initial)
    val helpListState: StateFlow<HelpUiState> = _helpListState

    init {
        getHelp()
    }
    fun getHelp(){
        viewModelScope.launch (Dispatchers.IO) {
            _helpListState.value=HelpUiState.Loading
            try {
                val response=apiService.getRequire()
                if (response.isSuccessful){
                    val help=response.body()?.request
                    _helpListState.value=HelpUiState.Success(help)
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
}
