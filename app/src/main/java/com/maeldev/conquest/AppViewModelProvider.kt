package com.maeldev.conquest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras

object AppViewModelProvider {
    val Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            val application = checkNotNull(extras[APPLICATION_KEY]) as ConQuestApplication
            val db = application.database
            
            if (modelClass.isAssignableFrom(CosplayViewModel::class.java)) {
                return CosplayViewModel(
                    application = application,
                    dao = db.cosplayDao(),
                    photoDao = db.cosplayPhotoDao(),
                    elementDao = db.cosplayElementDao(),
                    taskDao = db.cosplayTaskDao(),
                    eventDao = db.eventDao(),
                    progressPhotoDao = db.progressPhotoDao()
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
