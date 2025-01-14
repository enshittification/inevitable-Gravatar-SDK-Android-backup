package com.gravatar.quickeditor

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.gravatar.quickeditor.data.FileUtils
import com.gravatar.quickeditor.data.datastore.createEncryptedFileWithFallbackReset
import com.gravatar.quickeditor.data.repository.AvatarRepository
import com.gravatar.quickeditor.data.service.WordPressOAuthApi
import com.gravatar.quickeditor.data.service.WordPressOAuthService
import com.gravatar.quickeditor.data.storage.DataStoreTokenStorage
import com.gravatar.quickeditor.data.storage.InMemoryTokenStorage
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.services.AvatarService
import com.gravatar.services.ProfileService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.osipxd.security.crypto.createEncrypted
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal class QuickEditorContainer private constructor(
    private val context: Context,
) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: QuickEditorContainer

        fun init(context: Context): QuickEditorContainer {
            instance = QuickEditorContainer(context)
            return instance
        }

        fun getInstance(): QuickEditorContainer {
            check(::instance.isInitialized) {
                "QuickEditorContainer is not initialized. Call init() first."
            }
            return instance
        }
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createEncrypted(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    ) {
        context.createEncryptedFileWithFallbackReset(name = "quick-editor-preferences")
    }

    val dataStoreTokenStorage: DataStoreTokenStorage by lazy {
        DataStoreTokenStorage(dataStore = dataStore, dispatcher = Dispatchers.IO)
    }

    private var useInMemoryTokenStorage = false

    public val inMemoryTokenStorage: InMemoryTokenStorage by lazy {
        InMemoryTokenStorage()
    }

    val tokenStorage: TokenStorage
        get() = if (useInMemoryTokenStorage) inMemoryTokenStorage else dataStoreTokenStorage

    val wordPressOAuthService: WordPressOAuthService by lazy {
        WordPressOAuthService(
            wordPressOAuthApi = getWordpressOAuthApi(),
            dispatcher = Dispatchers.IO,
        )
    }

    private val avatarService: AvatarService by lazy {
        AvatarService()
    }

    val profileService: ProfileService by lazy {
        ProfileService()
    }

    val fileUtils: FileUtils by lazy {
        FileUtils(context)
    }

    val avatarRepository: AvatarRepository
        get() = AvatarRepository(
            avatarService = avatarService,
            tokenStorage = tokenStorage,
            dispatcher = Dispatchers.IO,
        )

    fun useInMemoryTokenStorage() {
        useInMemoryTokenStorage = true
    }

    fun resetUseInMemoryTokenStorage() {
        useInMemoryTokenStorage = false
    }

    private fun getWordpressOAuthApi(): WordPressOAuthApi {
        return Retrofit.Builder().baseUrl("https://public-api.wordpress.com")
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build().create(WordPressOAuthApi::class.java)
    }
}
