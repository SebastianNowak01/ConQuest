package com.example.conquest

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class CosplayViewModel(application: Application) : AndroidViewModel(application) {
    internal val database = (application as ConQuestApplication).database
}