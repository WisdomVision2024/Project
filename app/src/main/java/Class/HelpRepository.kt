package Class

import Data.HelpResponse
import assets.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HelpRepository(private val apiService: ApiService) {
    private val _helpResponseData = MutableStateFlow<HelpResponse?>(null)
    val helpResponseData: StateFlow<HelpResponse?> = _helpResponseData.asStateFlow()

    suspend fun fetchHelpData(): HelpResponse? {
        return try {
            val response = apiService.getRequire()
            if (response.isSuccessful) {
                val helpData = response.body()
                _helpResponseData.value=helpData // 保存數據
                helpData
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateHelpData(helpResponse: HelpResponse?) {
        _helpResponseData.value=helpResponse
    }
}
