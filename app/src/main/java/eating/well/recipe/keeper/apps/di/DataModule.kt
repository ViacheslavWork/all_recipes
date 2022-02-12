package eating.well.recipe.keeper.apps.di

import eating.well.recipe.keeper.apps.data.Repository
import eating.well.recipe.keeper.apps.utils.AssetManager
import eating.well.recipe.keeper.apps.utils.FileIOManager
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