package eating.well.recipe.keeper.app.ui.home

import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.data.database.entity.RecipeEntity

sealed class RecipeListEvent {
    var hasBeenHandled = false
        protected set // Allow external read but not write

    fun setHandled() {
        hasBeenHandled = true
    }

    data class OnRecipeClick(val recipeEntity: RecipeEntity, val position: Int) : RecipeListEvent() {
        /**
         * Returns the content and prevents its use again.
         */
        fun getContentIfNotHandled(): RecipeEntity? {
            return if (hasBeenHandled) {
                null
            } else {
                hasBeenHandled = true
                recipeEntity
            }
        }

        /**
         * Returns the content, even if it's already been handled.
         */
        fun peekRecipeEntity(): RecipeEntity = recipeEntity
    }
    data class OnCategoryClick(val category: Category) : RecipeListEvent()
    class OnBackClick : RecipeListEvent()
    object OnShowMoreRecipesClick : RecipeListEvent()
    object OnRectangleClick : RecipeListEvent()
    object OnGridClick : RecipeListEvent()
    object OnOpenHomeFragmentEvent : RecipeListEvent()
}
