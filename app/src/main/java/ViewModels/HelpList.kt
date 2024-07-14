package ViewModels

import Data.HelpRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
sealed class HelpUiState{
    data object Initial:HelpUiState()
    data object Loading:HelpUiState()
    data class Success(val helpList:List<HelpRequest>?):HelpUiState()
    data class Error(val message:String):HelpUiState()
}
class HelpList:ViewModel() {
    fun getHelpList(){
        viewModelScope.launch {  }
    }
}