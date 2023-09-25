package com.example.schedulog

import android.app.Application
import timber.log.Timber

class SchedulogApplication : Application() {    // This class contains global application state for the entire app. No activities should occur in this class.
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())        // Initializing logger
    }
}