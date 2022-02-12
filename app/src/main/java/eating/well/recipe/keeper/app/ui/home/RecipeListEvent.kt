package eating.well.recipe.keeper.app.ui.home

import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.model.Recipe

sealed class RecipeListEvent {
    var hasBeenHandled = false
        protected set // Allow external read but not write

    fun setHandled() {
        hasBeenHandled = true
    }

    abstract class OnRecipeClick(val recipe: Recipe, val position: Int) : RecipeListEvent() {
        /**
         * Returns the content and prevents its use again.
         */
        fun getContentIfNotHandled(): Recipe? {
            return if (hasBeenHandled) {
                null
            } else {
                hasBeenHandled = true
                recipe
            }
        }

        /**
         * Returns the content, even if it's already been handled.
         */
        fun peekRecipeEntity(): Recipe = recipe
    }

    class OnOpenedRecipeClick(recipe: Recipe, position: Int) : OnRecipeClick(recipe, position)
    class OnClosedRecipeClick(recipe: Recipe, position: Int) : OnRecipeClick(recipe, position)
    class OnAdClick : RecipeListEvent()

    data class OnCategoryClick(val category: Category) : RecipeListEvent()
    class OnBackClick : RecipeListEvent()
    object OnShowMoreRecipesClick : RecipeListEvent()
    object OnRectangleClick : RecipeListEvent()
    object OnGridClick : RecipeListEvent()
    object OnOpenHomeFragmentEvent : RecipeListEvent()
}
