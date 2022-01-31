package com.android.app.utils

import android.content.Context
import com.android.app.data.database.entity.RecipeEntity
import com.android.app.data.database.entity.Recipes
import java.io.ObjectInputStream

class AssetManager(private val context: Context) {
    fun readFromAssetFile(fileName: String): List<RecipeEntity> {
        val inputStream = context.assets.open(fileName)
        val ois = ObjectInputStream(inputStream)
        val recipes = ois.readObject() as Recipes
        return recipes.recipesList
    }
}