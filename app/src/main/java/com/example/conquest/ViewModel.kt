package com.example.conquest

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ViewModel(application: Application) : AndroidViewModel(application) {
    internal val database = (application as ConQuestApplication).database
}