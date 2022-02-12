package eating.well.recipe.keeper.apps.ui.billing

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import eating.well.recipe.keeper.apps.R
import eating.well.recipe.keeper.apps.databinding.FragmentUnlockFreeBinding
import eating.well.recipe.keeper.apps.ui.home.HomeViewModel
import eating.well.recipe.keeper.apps.ui.home.RecipeListEvent
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class UnlockFreeFragment : DialogFragment() {
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private var _binding: FragmentUnlockFreeBinding? = null
    private val binding get() = _binding!!

    //Interstitial
    private var interAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnlockFreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpViews()
        observeViewModel()
        loadInterAd()
    }

    private fun observeViewModel() {
        homeViewModel.recipeListEvent.observe(viewLifecycleOwner) {
            when (it) {
                is RecipeListEvent.OnRecipeClick -> {
                    val imageOption = RequestOptions()
                        .placeholder(R.drawable.unlock_placeholder)
                        .fallback(R.drawable.unlock_placeholder)
                    val bigImage = it.recipe.detailImage.split("/")
                    val smallImage = it.recipe.image.split("/")
                    Glide.with(requireContext())
                        .load(Uri.parse("file:///android_asset/small_images/${smallImage[smallImage.size - 1]}"))
                        .centerCrop()
                        .error("file:///android_asset/big_images/${bigImage[bigImage.size - 1]}")
                        .apply(imageOption)
                        .into(binding.unlockFreeIv)
                }
            }
        }
    }

    override fun onResume() {
        loadInterAd()
        super.onResume()
    }

    private fun setUpViews() {
        binding.closeUnlockFreeBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.watchVideoBtn.setOnClickListener {
            showInterAd {
                findNavController().navigate(
                    UnlockFreeFragmentDirections.actionUnlockFreeFragmentToDetailsFragment()
                )
            }
        }
        binding.openAllRecipesBtn.setOnClickListener {
            findNavController().navigate(
                UnlockFreeFragmentDirections.actionUnlockFreeFragmentToGoPremiumFragment()
            )
        }
    }

    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            resources.getString(R.string.rewarded_id),
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
    }


    override fun getTheme() = R.style.RoundedCornersDialog

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}