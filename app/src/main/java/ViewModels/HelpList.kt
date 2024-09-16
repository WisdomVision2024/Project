package ViewModels

import Class.HelpRepository
import Data.HelpRequest
import Data.HelpResponse
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import assets.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration

sealed class HelpUiState{
    data object Initial:HelpUiState()
    data object Loading:HelpUiState()
    data class Success(val helpResponse: HelpResponse?):HelpUiState()
    data class Error(val message:String?):HelpUiState()
}

class HelpList(private val helpRepository: HelpRepository) :ViewModel()
{
    private val _helpListState= MutableStateFlow<HelpUiState>(HelpUiState.Initial)
    val helpListState: StateFlow<HelpUiState> = _helpListState

    fun fetchHelpData() {
        viewModelScope.launch {
            _helpListState.value = HelpUiState.Loading
            try {
                val helpData = helpRepository.fetchHelpData()
                if (helpData != null) {
                    _helpListState.value = HelpUiState.Success(helpData)
                    val position=(helpData as HelpResponse).position?.toString()
                    Log.d("HelpList","$position")
                } else {
                    _helpListState.value = HelpUiState.Error("No data")
                }
            } catch (e: Exception) {
                _helpListState.value = HelpUiState.Error("Error: ${e.message}")
            }
        }
    }
}
