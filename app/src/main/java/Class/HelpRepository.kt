package Class

import Data.HelpResponse
import assets.ApiService

class HelpRepository(private val apiService: ApiService) {
    suspend fun fetchHelpData(): HelpResponse? {
        return try {
            val response = apiService.getRequire()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}