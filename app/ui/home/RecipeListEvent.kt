package eating.well.recipe.keeper.app.ui.home

import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.data.database.entity.RecipeEntity

sealed class RecipeListEvent {
    data class OnRecipeClick(val recipeEntity: RecipeEntity) : RecipeListEvent()
    data class OnCategoryClick(val category: Category) : RecipeListEvent()
    object OnShowMoreRecipesClick : RecipeListEvent()
    object OnRectangleClick : RecipeListEvent()
    object OnGridClick : RecipeListEvent()
    object OnBackClick : RecipeListEvent()
    object OnOpenHomeFragmentEvent : RecipeListEvent()
}
