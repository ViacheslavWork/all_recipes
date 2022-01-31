package eating.well.recipe.keeper.app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eating.well.recipe.keeper.app.R
import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.databinding.FragmentHomeBinding
import eating.well.recipe.keeper.app.utils.RecipeParser.putRecipesFun
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    companion object {
        private const val ARG_COLUMN_COUNT = "column-count"
        private const val LIST_STATE_KEY = "recycler_state"
        private const val CLICKED_POSITION = "clicked_position"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }

    }


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipesAdapter

    private val homeViewModel: HomeViewModel by sharedViewModel()
    private var _binding: FragmentHomeBinding? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var rectangleMenuItem: MenuItem
    private lateinit var gridMenuItem: MenuItem

    private var clickedPosition = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        savedInstanceState?.getInt(CLICKED_POSITION)?.let { clickedPosition = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        downloadRecipes()
//        writeToFile()

        return root
    }

    private fun downloadRecipes() {
        GlobalScope.launch {
            homeViewModel.deleteAllRecipes()
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/pasta-recipes-5090999",
                    Category.PASTA
                )
            )
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/recipes-by-diet-5091259",
                    Category.DIET
                )
            )
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/recipes-by-method-5091235",
                    Category.METHOD
                )
            )
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/recipes-by-ingredients-5091192",
                    Category.INGREDIENTS
                )
            )
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/recipes-by-time-and-ease-5090817",
                    Category.TIME_EASE
                )
            )
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/world-cuisine-recipes-5090811",
                    Category.CUISINE
                )
            )
        }
    }

    private fun writeToFile() {
        GlobalScope.launch {
            homeViewModel.writeRecipes()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = GridLayoutManager(context, 2)
        setUpRecyclerView(view, layoutManager)
        setUpAdapter()
        setUpToolbar(view)

        observeLayoutState(view)
        observeState()
        observeEvent()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        layoutManager.onSaveInstanceState()?.let { outState.putParcelable(LIST_STATE_KEY, it) }
        outState.putInt(CLICKED_POSITION, clickedPosition)
    }

    private fun observeLayoutState(view: View) {
        homeViewModel.isLayoutGrid.observe(viewLifecycleOwner) {
            if (it) {
                layoutManager = GridLayoutManager(context, 2)
                setUpRecyclerView(view, layoutManager)
            } else {
                layoutManager = LinearLayoutManager(context)
                setUpRecyclerView(view, layoutManager)
            }
        }
    }

    private fun setUpRecyclerView(view: View, layoutManager: RecyclerView.LayoutManager) {
        recyclerView = view.findViewById(R.id.rv_recipe_cards)
        recyclerView.layoutManager = layoutManager
        recyclerView.layoutManager?.scrollToPosition(clickedPosition)
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.recipes_toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        val moreRecipesButton = view.findViewById<Button>(R.id.show_more_recipes_btn)
        moreRecipesButton.setOnClickListener { homeViewModel.handleEvent(RecipeListEvent.OnShowMoreRecipesClick) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_list_menu, menu)
        rectangleMenuItem = menu.findItem(R.id.rectangle_mi)
        gridMenuItem = menu.findItem(R.id.grid_mi)
        homeViewModel.isLayoutGrid.observe(viewLifecycleOwner) {
            if (it) {
                menu.findItem(R.id.grid_mi).setIcon(R.drawable.ic_grid_clicked)
                menu.findItem(R.id.rectangle_mi).setIcon(R.drawable.ic_rectangle)
            } else {
                menu.findItem(R.id.grid_mi).setIcon(R.drawable.ic_grid)
                menu.findItem(R.id.rectangle_mi).setIcon(R.drawable.ic_rectangle_clicked)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rectangle_mi -> homeViewModel.handleEvent(RecipeListEvent.OnRectangleClick)
            R.id.grid_mi -> homeViewModel.handleEvent(RecipeListEvent.OnGridClick)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeEvent() {
        homeViewModel.recipeListEvent.observe(this, {
            when (it) {
                is RecipeListEvent.OnRecipeClick -> {
                    clickedPosition = it.position
                    it.getContentIfNotHandled()?.let {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToDetailsFragment()
                        )
                    }
                }
            }
        })
    }


    private fun observeState() {
        homeViewModel.recipes.observe(viewLifecycleOwner, {
            when {
                it.error.isNotBlank() -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_LONG).show()
                }
                it.isLoading -> {
                    Toast.makeText(requireContext(), "Loading!!!", Toast.LENGTH_LONG).show()
                }
                else -> {
                    adapter.submitList(it.recipes.toMutableList())
                    Log.i(TAG, "lenght: ${it.recipes.size}");
                }
            }
        })
    }

    private fun setUpAdapter() {
        adapter = RecipesAdapter()
        adapter.event.observe(viewLifecycleOwner, {
            homeViewModel.handleEvent(it)
        })
        recyclerView.adapter = adapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
