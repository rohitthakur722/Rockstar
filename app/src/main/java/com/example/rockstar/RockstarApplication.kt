package com.example.rockstar

import android.app.Application
import com.example.rockstar.data.local.RockstarDatabase
import com.example.rockstar.data.preferences.PreferencesRepository
import com.example.rockstar.repo.MediaStoreMusicRepository
import com.example.rockstar.repo.PersonalLibraryRepository
import com.example.rockstar.repo.PersonalLibraryRepositoryImpl

class RockstarApplication : Application() {
    lateinit var container: RockstarContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = RockstarContainer(this)
    }
}

class RockstarContainer(application: Application) {
    val database: RockstarDatabase = RockstarDatabase.getInstance(application)
    val personalLibraryRepository: PersonalLibraryRepository = PersonalLibraryRepositoryImpl(database)
    val preferencesRepository: PreferencesRepository = PreferencesRepository(application)
    val musicRepository = MediaStoreMusicRepository(application.contentResolver)
}
