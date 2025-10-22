package com.zaidu.avarts.ui.save

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SaveViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SaveViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}