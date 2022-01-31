package com.android.app.di

import com.android.app.data.Repository
import com.android.app.utils.AssetManager
import com.android.app.utils.FileIOManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<Repository> {
        Repository(
            recipeDao = get(),
            assetManager = get(),
            fileIOManager = get()
        )
    }
    single<AssetManager> { AssetManager(androidContext()) }
    single<FileIOManager> { FileIOManager(androidContext()) }
}