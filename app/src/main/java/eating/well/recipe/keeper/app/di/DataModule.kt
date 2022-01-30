package eating.well.recipe.keeper.app.di

import eating.well.recipe.keeper.app.data.Repository
import eating.well.recipe.keeper.app.utils.AssetManager
import eating.well.recipe.keeper.app.utils.FileIOManager
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