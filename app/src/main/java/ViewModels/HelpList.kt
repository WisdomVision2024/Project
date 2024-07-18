package ViewModels

import Data.HelpRequest
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import assets.ApiService
import assets.RetrofitInstance
import com.example.project.HelpListPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

sealed class HelpUiState{
    data object Initial:HelpUiState()
    data object Loading:HelpUiState()
    data class Success(val helpList:List<HelpRequest>?):HelpUiState()
    data class Error(val message:String?):HelpUiState()
}
sealed class AcceptUiState{
    data object Initial:AcceptUiState()
    data class Success(val message: String?):AcceptUiState()
    data class Error(val message:String?):AcceptUiState()
}
class HelpList(private val apiService: ApiService) :ViewModel()
{
    private val _helpListState= MutableStateFlow<HelpUiState>(HelpUiState.Initial)
    val helpListState: StateFlow<HelpUiState> = _helpListState

    private val _acceptState= MutableStateFlow<AcceptUiState>(AcceptUiState.Initial)
    val acceptUiState:StateFlow<AcceptUiState> = _acceptState
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

    fun AcceptCommission(id:String){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response=apiService.acceptCommission(id)
                if (response.isSuccessful){
                    val message=response.body()?.message
                    _acceptState.value=AcceptUiState.Success(message)
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
}
