package com.android.app

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.data.database.entity.RecipeEntity
import eating.well.recipe.keeper.app.databinding.ActivityMainBinding
import eating.well.recipe.keeper.app.ui.details.DetailsFragment
import eating.well.recipe.keeper.app.ui.greating.GreetingFragment
import com.android.app.ui.home.HomeFragment
import com.android.app.ui.home.HomeViewModel
import com.android.app.ui.home.RecipeListEvent
import eating.well.recipe.keeper.app.R
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var greetingFragment: GreetingFragment? = null
    private var recipeListFragment: HomeFragment? = null
    private var detailsFragment: DetailsFragment? = null

    companion object {
        const val DETAILS_FRAGMENT_TAG = "DetailsFragment"
        const val RECIPES_FRAGMENT_TAG = "RecipesListFragment"
        const val GREETING_FRAGMENT_TAG = "GreetingFragment"
        private const val TAG = "MainActivity"
    }

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

//        openRecipesListFragment()

        if (savedInstanceState == null) {
            openGreetingFragment()
        } else {
            handleRecreate()
        }

        observeHomeVM()
        setUpNavigationView()
    }

    private fun handleRecreate() {
        recipeListFragment =
            (supportFragmentManager.findFragmentByTag(RECIPES_FRAGMENT_TAG) as? HomeFragment)
        detailsFragment =
            (supportFragmentManager.findFragmentByTag(DETAILS_FRAGMENT_TAG) as? DetailsFragment)
    }

    private fun setUpNavigationView() {
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_cross -> {
                    binding.drawerLayout.close()
                    Toast.makeText(
                        baseContext,
                        "Category clicked",
                        Toast.LENGTH_SHORT
                    ).show()
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
                is RecipeListEvent.OnCategoryClick -> Unit
                is RecipeListEvent.OnRecipeClick -> openDetailsFragment(it.recipeEntity)
                RecipeListEvent.OnShowMoreRecipesClick -> binding.drawerLayout.open()
                RecipeListEvent.OnOpenHomeFragmentEvent -> openRecipesListFragment()
                RecipeListEvent.OnBackClick -> {onBackPressed()}
            }
        }
    }

    private fun openDetailsFragment(recipeEntity: RecipeEntity) {
        detailsFragment = null
        detailsFragment = DetailsFragment.newInstance(recipeEntity)
        supportFragmentManager.beginTransaction()
            .apply {
                replace(
                    R.id.flContent,
                    detailsFragment!!,
                    DETAILS_FRAGMENT_TAG
                )
                addToBackStack(null)
                commit()
            }
    }

    private fun openRecipesListFragment() {
        recipeListFragment = HomeFragment.newInstance(2)
        recipeListFragment?.apply {
            supportFragmentManager.beginTransaction()
                .replace(R.id.flContent, this, RECIPES_FRAGMENT_TAG)
//                .addToBackStack(null)
                .commit()
        }
    }

    private fun openGreetingFragment() {
        greetingFragment = GreetingFragment()
        greetingFragment?.apply {
            supportFragmentManager.beginTransaction()
                .replace(R.id.flContent, this, RECIPES_FRAGMENT_TAG)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

}