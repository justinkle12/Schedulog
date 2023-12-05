package com.example.schedulog

import android.app.Application
import timber.log.Timber
import twitter4j.Twitter

class SchedulogApplication : Application() {    // This class contains global application state for the entire app. No activities should occur in this class.

    // Depreciated version of posting tweets using twitter4j and twitter APIv1.1
    private lateinit var twitterInstance: Twitter

    override fun onCreate() {

        // Depreciated version of posting tweets using twitter4j and twitter APIv1.1
        val consumerKey = resources.getString(R.string.consumerKey)
        val consumerSecret = resources.getString(R.string.consumerSecret)
        val accessToken = resources.getString(R.string.accessToken)
        val accessTokenSecret = resources.getString(R.string.accessTokenSecret)

        super.onCreate()
        Timber.plant(Timber.DebugTree())        // Initializing logger



        // Depreciated version of posting tweets using twitter4j and twitter APIv1.1
        twitterInstance = Twitter.newBuilder()
           .oAuthConsumer(consumerKey, consumerSecret)
            .oAuthAccessToken(accessToken, accessTokenSecret)
            .build()
    }


    // Depreciated version of posting tweets using twitter4j and twitter APIv1.1
    fun getTwitterInstance(): Twitter {
         //Return the pre-configured Twitter instance
        return twitterInstance
    }
}