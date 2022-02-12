package eating.well.recipe.keeper.apps.ui.home

import eating.well.recipe.keeper.apps.model.Recipe


data class RecipesState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val error: String = ""
)