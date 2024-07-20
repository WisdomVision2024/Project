package provider

import ViewModels.BlueTooth
import ViewModels.Identified
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import assets.ApiService

class IdentifiedFactory(
    private val application: Application,
    private val apiService: ApiService,
    private val navController: NavController
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Identified::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return Identified(application, blueTooth = BlueTooth(application),apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
