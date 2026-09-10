package com.maeldev.conquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.maeldev.conquest.components.deleteStoredImageByPath

/**
 * Deletes the internal-storage image at [path], if there is one.
 *
 * Photo paths are nullable throughout the entities, and "no path" is not an error, so a null or
 * blank path is a no-op. Four view models each carried a private copy of this.
 */
fun AndroidViewModel.deleteManagedImageFile(path: String?) {
    deleteStoredImageByPath(getApplication<Application>(), path.orEmpty())
}
