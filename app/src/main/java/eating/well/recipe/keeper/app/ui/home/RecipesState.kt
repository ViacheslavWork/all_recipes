package eating.well.recipe.keeper.app.ui.home

import eating.well.recipe.keeper.app.model.Recipe


data class RecipesState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val error: String = ""
)