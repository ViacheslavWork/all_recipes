package eating.well.recipe.keeper.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eating.well.recipe.keeper.app.data.Repository
import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.data.database.entity.RecipeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeViewModel(private val repository: Repository) : ViewModel() {
    private val _recipes = MutableLiveData<RecipesState>()
    val recipes: LiveData<RecipesState> = _recipes

    private val _isLayoutGrid = MutableLiveData<Boolean>(true)
    val isLayoutGrid: LiveData<Boolean> = _isLayoutGrid

    private val mutableRecipeListEvent = MutableLiveData<RecipeListEvent>()
    val recipeListEvent: LiveData<RecipeListEvent> get() = mutableRecipeListEvent

    init {
        getRecipesByCategory(Category.PASTA)
//        writeImagesToFile()
    }

    private fun writeImagesToFile() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.writeImagesToExternalStorage()
        }
    }

    private fun getRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            _recipes.postValue(RecipesState(recipes = repository.getRecipes()))
        }
    }

    fun writeRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.writeRecipesToFile(repository.getRecipes())
        }
    }

    private fun getRecipesByCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            _recipes.postValue(RecipesState(recipes = repository.getRecipesByCategory(category)))
        }
    }

    fun deleteAllRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearRecipes()
        }
    }

    fun putRecipes(recipes: List<RecipeEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setRecipes(recipes)
        }
    }

    fun handleEvent(event: RecipeListEvent) {
        when (event) {
            is RecipeListEvent.OnCategoryClick -> getRecipesByCategory(event.category)
            is RecipeListEvent.OnRecipeClick -> mutableRecipeListEvent.value = event
            is RecipeListEvent.OnBackClick -> mutableRecipeListEvent.value = event
            RecipeListEvent.OnShowMoreRecipesClick -> mutableRecipeListEvent.value = event
            RecipeListEvent.OnGridClick -> _isLayoutGrid.value = true
            RecipeListEvent.OnRectangleClick -> _isLayoutGrid.value = false
            RecipeListEvent.OnOpenHomeFragmentEvent -> mutableRecipeListEvent.value = event
        }
    }
}