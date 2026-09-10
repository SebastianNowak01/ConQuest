package com.maeldev.conquest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.maeldev.conquest.data.dao.CosplayDaos
import com.maeldev.conquest.data.database.CosplayDatabase
import com.maeldev.conquest.viewmodel.CosplayViewModel
import com.maeldev.conquest.viewmodel.ElementViewModel
import com.maeldev.conquest.viewmodel.EventViewModel
import com.maeldev.conquest.viewmodel.ExportImportViewModel
import com.maeldev.conquest.viewmodel.PhotoViewModel
import com.maeldev.conquest.viewmodel.ProgressPhotoViewModel
import com.maeldev.conquest.viewmodel.TaskViewModel

/** Every DAO an export or import touches, gathered from [db]. */
private fun cosplayDaos(db: CosplayDatabase) =
    CosplayDaos(
        cosplayDao = db.cosplayDao(),
        elementDao = db.cosplayElementDao(),
        taskDao = db.cosplayTaskDao(),
        photoDao = db.cosplayPhotoDao(),
        progressPhotoDao = db.progressPhotoDao(),
        eventDao = db.eventDao(),
    )

object AppViewModelProvider {
    val Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as ConQuestApplication
                val db = application.database

                return when {
                    modelClass.isAssignableFrom(CosplayViewModel::class.java) -> {
                        CosplayViewModel(
                            application = application,
                            dao = db.cosplayDao(),
                            photoDao = db.cosplayPhotoDao(),
                            progressPhotoDao = db.progressPhotoDao(),
                        ) as T
                    }
                    modelClass.isAssignableFrom(EventViewModel::class.java) -> {
                        EventViewModel(
                            application = application,
                            eventDao = db.eventDao(),
                            cosplayDao = db.cosplayDao(),
                        ) as T
                    }
                    modelClass.isAssignableFrom(ElementViewModel::class.java) -> {
                        ElementViewModel(
                            application = application,
                            elementDao = db.cosplayElementDao(),
                            cosplayDao = db.cosplayDao(),
                        ) as T
                    }
                    modelClass.isAssignableFrom(TaskViewModel::class.java) -> {
                        TaskViewModel(
                            application = application,
                            taskDao = db.cosplayTaskDao(),
                            cosplayDao = db.cosplayDao(),
                        ) as T
                    }
                    modelClass.isAssignableFrom(PhotoViewModel::class.java) -> {
                        PhotoViewModel(
                            application = application,
                            photoDao = db.cosplayPhotoDao(),
                        ) as T
                    }
                    modelClass.isAssignableFrom(ProgressPhotoViewModel::class.java) -> {
                        ProgressPhotoViewModel(
                            application = application,
                            progressPhotoDao = db.progressPhotoDao(),
                        ) as T
                    }
                    modelClass.isAssignableFrom(ExportImportViewModel::class.java) -> {
                        ExportImportViewModel(
                            application = application,
                            daos = cosplayDaos(db),
                        ) as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
}
