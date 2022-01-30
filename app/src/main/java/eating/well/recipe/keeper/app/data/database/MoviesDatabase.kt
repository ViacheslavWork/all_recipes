package eating.well.recipe.keeper.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eating.well.recipe.keeper.app.data.database.entity.MethodPointEntityListConverter
import eating.well.recipe.keeper.app.data.database.entity.RecipeEntity
import eating.well.recipe.keeper.app.data.database.entity.StringListConverter

@Database(
    entities = [RecipeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class, MethodPointEntityListConverter::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract val recipeDao: RecipeDao
}