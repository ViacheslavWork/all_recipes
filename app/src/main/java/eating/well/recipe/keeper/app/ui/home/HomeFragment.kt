package eating.well.recipe.keeper.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import eating.well.recipe.keeper.app.R
import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.data.database.entity.toRecipe
import eating.well.recipe.keeper.app.databinding.FragmentHomeBinding
import eating.well.recipe.keeper.app.utils.RecipeParser.putRecipesFun
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipesAdapter

    private val homeViewModel: HomeViewModel by sharedViewModel()
    private var _binding: FragmentHomeBinding? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var rectangleMenuItem: MenuItem
    private lateinit var gridMenuItem: MenuItem

    //Interstitial
    private var interAd: InterstitialAd? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

//        downloadRecipes()
//        writeToFile()

        return binding.root
    }

    override fun onResume() {
        loadInterAd()
        startAdAnimation()
        super.onResume()
    }

    private fun startAdAnimation() {
        binding.adToolbarIv.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.ad_animation
            )
        )
    }

    override fun onPause() {
        binding.adToolbarIv.clearAnimation()
        super.onPause()
    }

    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            resources.getString(R.string.interstitial_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interAd = interstitialAd
                }
            })
    }

    private fun showInterAd(function: () -> Unit) {
        if (interAd != null) {
            interAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    function()
                    interAd = null
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    function()
                    interAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    function()
                    interAd = null
                }
            }
            interAd?.show(requireActivity())
        } else {
            function()
        }
    }

    private fun downloadRecipes() {
        GlobalScope.launch {
            homeViewModel.deleteAllRecipes()
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/pasta-recipes-5090999",
                    Category.PASTA
                ).map { it.toRecipe() }
            )
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/recipes-by-diet-5091259",
                    Category.DIET
                ).map { it.toRecipe() }
            )
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/recipes-by-method-5091235",
                    Category.METHOD
                ).map { it.toRecipe() }
            )
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/recipes-by-ingredients-5091192",
                    Category.INGREDIENTS
                ).map { it.toRecipe() }
            )
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/recipes-by-time-and-ease-5090817",
                    Category.TIME_EASE
                ).map { it.toRecipe() }
            )
            homeViewModel.putRecipes(
                putRecipesFun(
                    "https://www.simplyrecipes.com/world-cuisine-recipes-5090811",
                    Category.CUISINE
                ).map { it.toRecipe() }
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
        setUpRecyclerView(layoutManager)
        setUpAdapter()
        setUpToolbar()

        observeLayoutState()
        observeState()
        observeEvent()
        observePremium()
    }

    private fun observePremium() {
        homeViewModel.isPremiumLiveData.observe(viewLifecycleOwner){
            if (it) {
                binding.adToolbarIv.clearAnimation()
                binding.adToolbarIv.visibility = View.GONE
            } else {
                binding.adToolbarIv.visibility = View.VISIBLE
                startAdAnimation()
            }
        }
    }


    private fun observeLayoutState() {
        homeViewModel.isLayoutGrid.observe(viewLifecycleOwner) {
            if (it) {
                val currentPosition = layoutManager.onSaveInstanceState()
                layoutManager = GridLayoutManager(context, 2)
                layoutManager.onRestoreInstanceState(currentPosition)
                setUpRecyclerView(layoutManager)

                binding.rectangleToolbarIv.setImageResource(R.drawable.ic_rectangle)
                binding.gridToolbarIv.setImageResource(R.drawable.ic_grid_clicked)
            } else {
                val currentPosition = layoutManager.onSaveInstanceState()
                layoutManager = LinearLayoutManager(context)
                layoutManager.onRestoreInstanceState(currentPosition)
                setUpRecyclerView(layoutManager)

                binding.rectangleToolbarIv.setImageResource(R.drawable.ic_rectangle_clicked)
                binding.gridToolbarIv.setImageResource(R.drawable.ic_grid)
            }
        }
    }

    private fun setUpRecyclerView(layoutManager: RecyclerView.LayoutManager) {
        recyclerView = binding.rvRecipeCards
        recyclerView.layoutManager = layoutManager
    }

    private fun setUpToolbar() {
        binding.showMoreRecipesBtn.setOnClickListener { homeViewModel.handleEvent(RecipeListEvent.OnShowMoreRecipesClick) }
        binding.gridToolbarIv.setOnClickListener { homeViewModel.handleEvent(RecipeListEvent.OnGridClick) }
        binding.rectangleToolbarIv.setOnClickListener { homeViewModel.handleEvent(RecipeListEvent.OnRectangleClick) }
        binding.adToolbarIv.setOnClickListener { homeViewModel.handleEvent(RecipeListEvent.OnAdClick()) }
    }


    private fun observeEvent() {
        homeViewModel.recipeListEvent.observe(this, {
            when (it) {
                is RecipeListEvent.OnOpenedRecipeClick -> {
                    it.getContentIfNotHandled()?.let {
                        showInterAd {
                            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment())
//                            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUnlockFreeFragment())
//                            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGoPremiumFragment())
//                            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTermsConditionsFragment())
                        }
                    }
                }
                is RecipeListEvent.OnClosedRecipeClick -> {
                    it.getContentIfNotHandled()?.let {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUnlockFreeFragment())
                    }
                }
                is RecipeListEvent.OnAdClick -> {
                    if (!it.hasBeenHandled) {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGoPremiumFragment())
                        it.setHandled()
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
                }
            }
        })
    }

    private fun setUpAdapter() {
        adapter = RecipesAdapter()
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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
