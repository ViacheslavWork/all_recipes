package eating.well.recipe.keeper.app.ui.home

import eating.well.recipe.keeper.app.data.database.entity.RecipeEntity


data class RecipesState(
    val isLoading: Boolean = false,
    val recipes: List<RecipeEntity> = emptyList(),
    val error: String = ""
)