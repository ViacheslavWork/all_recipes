package eating.well.recipe.keeper.apps.di

import android.content.Context
import androidx.room.Room
import eating.well.recipe.keeper.apps.data.database.RecipeDao
import eating.well.recipe.keeper.apps.data.database.RecipeDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val DATABASE_NAME = "Recipes.db"

val roomModule = module {
    fun provideDatabase(context: Context): RecipeDatabase {
        return Room.databaseBuilder(context, RecipeDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()//dangerous thing!!!
            .build()
    }

    fun provideMoviesDao(database: RecipeDatabase): RecipeDao {
        return database.recipeDao
    }

    single { provideDatabase(context = androidContext()) }
    single { provideMoviesDao(get()) }
}