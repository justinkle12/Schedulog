package com.example.schedulog

import android.app.Application
import timber.log.Timber

class SchedulogApplication : Application() {    // This class contains global application state for the entire app. No activities should occur in this class.

    //private lateinit var twitterInstance: Twitter

    override fun onCreate() {
        /*val consumerKey = "***********************"
        val consumerSecret = "*****************************"
        val accessToken = "******************************************************"
        val accessTokenSecret = "************************************"*/

        super.onCreate()
        Timber.plant(Timber.DebugTree())        // Initializing logger

        /*twitterInstance = Twitter.newBuilder()
            .oAuthConsumer(consumerKey, consumerSecret)
            .oAuthAccessToken(accessToken, accessTokenSecret)
            .build()*/
    }

    /*fun getTwitterInstance(): Twitter {
        // Return the pre-configured Twitter instance
        return twitterInstance
    }*/
}