package eating.well.recipe.keeper.app

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.databinding.ActivityMainBinding
import eating.well.recipe.keeper.app.ui.details.DetailsFragment
import eating.well.recipe.keeper.app.ui.greating.GreetingFragment
import eating.well.recipe.keeper.app.ui.home.HomeFragment
import eating.well.recipe.keeper.app.ui.home.HomeViewModel
import eating.well.recipe.keeper.app.ui.home.RecipeListEvent
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {


    companion object {
        const val DETAILS_FRAGMENT_TAG = "DetailsFragment"
        const val RECIPES_FRAGMENT_TAG = "RecipesListFragment"
        const val GREETING_FRAGMENT_TAG = "GreetingFragment"
        private const val TAG = "MainActivity"
    }

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        observeHomeVM()
        setUpNavigationView()
    }

    private fun setUpNavigationView() {
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_cross -> {
                    binding.drawerLayout.close()
                }
                R.id.nav_time_ease -> homeViewModel.handleEvent(
                    RecipeListEvent.OnCategoryClick(
                        Category.TIME_EASE
                    )
                )
                R.id.nav_pasta -> homeViewModel.handleEvent(RecipeListEvent.OnCategoryClick(Category.PASTA))
                R.id.nav_method -> homeViewModel.handleEvent(
                    RecipeListEvent.OnCategoryClick(
                        Category.METHOD
                    )
                )
                R.id.nav_ingredients -> homeViewModel.handleEvent(
                    RecipeListEvent.OnCategoryClick(
                        Category.INGREDIENTS
                    )
                )
                R.id.nav_diet -> homeViewModel.handleEvent(RecipeListEvent.OnCategoryClick(Category.DIET))
                R.id.nav_cuisine -> homeViewModel.handleEvent(
                    RecipeListEvent.OnCategoryClick(
                        Category.CUISINE
                    )
                )
            }
            binding.drawerLayout.close()
            true
        }
    }

    private fun observeHomeVM() {
        homeViewModel.recipeListEvent.observe(this) {
            when (it) {
                RecipeListEvent.OnShowMoreRecipesClick -> binding.drawerLayout.open()
                is RecipeListEvent.OnBackClick -> {
                    Log.i(TAG, ": ${it.hasBeenHandled}");
                    if (!it.hasBeenHandled) {
                        onBackPressed()
                        it.setHandled()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

}