package com.android.app.ui.home

import com.android.app.data.database.entity.RecipeEntity


data class RecipesState(
    val isLoading: Boolean = false,
    val recipes: List<RecipeEntity> = emptyList(),
    val error: String = ""
)