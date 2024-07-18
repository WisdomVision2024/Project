package provider

import ViewModels.BlueTooth
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BluToothFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>):T{
        if (modelClass.isAssignableFrom(BlueTooth::class.java)){
            @Suppress("UNCHECKED_CAST")
            return BlueTooth(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}