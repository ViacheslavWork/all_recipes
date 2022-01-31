package com.android.app.data

import com.android.app.data.database.RecipeDao
import com.android.app.data.database.entity.Category
import com.android.app.data.database.entity.RecipeEntity
import com.android.app.utils.AssetManager
import com.android.app.utils.Constants.RECIPES_FILE_NAME
import com.android.app.utils.FileIOManager

private const val TAG = "Repository"
class Repository(
    private val recipeDao: RecipeDao,
    private val assetManager: AssetManager,
    private val fileIOManager: FileIOManager
) {
    fun setRecipes(recipes: List<RecipeEntity>) {
        recipeDao.insertAll(recipes)
    }

    fun writeRecipesToFile(recipes: List<RecipeEntity>) {
        fileIOManager.writeToFile(recipes,"recipes_new.txt")
    }

    fun writeImagesToExternalStorage() {
        recipeDao.getAll().forEach { fileIOManager.saveImage(it.detailImage) }
    }

    fun getRecipes(): List<RecipeEntity> {
        return recipeDao.getAll()
    }

    fun clearRecipes() {
        recipeDao.deleteAll()
    }

    fun getRecipesByCategory(category: Category): List<RecipeEntity> {
        if (recipeDao.getAll().isEmpty()) {
            recipeDao.insertAll(assetManager.readFromAssetFile(fileName = RECIPES_FILE_NAME))
        }
        return recipeDao.getByCategory(category)
    }
}