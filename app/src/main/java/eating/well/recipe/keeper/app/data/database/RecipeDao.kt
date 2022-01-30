package eating.well.recipe.keeper.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.data.database.entity.RecipeEntity

//mntl-card-list-items_2-0
//mntl-card-list-items_2-0
//https://www.simplyrecipes.com/thmb/6vTlaYA-5wI0K7ocHQHfUvkPSEU=/300x225/filters:no_upscale():max_bytes(150000):strip_icc()/__opt__aboutcom__coeus__resources__content_migration__simply_recipes__uploads__2020__03__Skillet-Chicken-Thighs-LEAD-1-702f6ae0933d421f94b6a46c365216a0.jpg

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    fun getAll(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE category LIKE :recipeCategory")
    fun getByCategory(recipeCategory: Category): List<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(recipes: List<RecipeEntity>)

    @Query("DELETE FROM recipes")
    fun deleteAll()

}