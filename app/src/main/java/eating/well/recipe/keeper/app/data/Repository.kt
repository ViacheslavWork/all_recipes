package eating.well.recipe.keeper.app.data

import eating.well.recipe.keeper.app.data.database.RecipeDao
import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.data.database.entity.RecipeEntity
import eating.well.recipe.keeper.app.data.database.entity.toRecipe
import eating.well.recipe.keeper.app.model.Recipe
import eating.well.recipe.keeper.app.utils.AssetManager
import eating.well.recipe.keeper.app.utils.Constants.RECIPES_FILE_NAME
import eating.well.recipe.keeper.app.utils.FileIOManager

private const val TAG = "Repository"

class Repository(
    private val recipeDao: RecipeDao,
    private val assetManager: AssetManager,
    private val fileIOManager: FileIOManager
) {
    fun setRecipes(recipes: List<Recipe>) {
        recipeDao.insertAll(recipes.map { RecipeEntity.fromRecipe(it) })
    }

    fun writeRecipesToFile(recipes: List<Recipe>) {
        fileIOManager.writeToFile(recipes.map { RecipeEntity.fromRecipe(it) }, "recipes_new.txt")
    }

    fun writeImagesToExternalStorage() {
        recipeDao.getAll().forEach { fileIOManager.saveImage(it.detailImage) }
    }

    fun getRecipes(): List<Recipe> {
        return recipeDao.getAll().map { it.toRecipe() }
    }

    fun clearRecipes() {
        recipeDao.deleteAll()
    }

    fun getRecipesByCategory(category: Category): List<Recipe> {
        if (recipeDao.getAll().isEmpty()) {
            recipeDao.insertAll(assetManager.readFromAssetFile(fileName = RECIPES_FILE_NAME))
        }
        val recipes = recipeDao.getByCategory(category).map { it.toRecipe() }
        return recipes.apply {
            forEachIndexed { index, recipe ->
                if (index > 2) recipe.isPremium = true
            }
        }
    }
}