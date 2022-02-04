package eating.well.recipe.keeper.app.ui.billing

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import eating.well.recipe.keeper.app.R
import eating.well.recipe.keeper.app.databinding.FragmentUnlockFreeBinding
import eating.well.recipe.keeper.app.ui.home.HomeViewModel
import eating.well.recipe.keeper.app.ui.home.RecipeListEvent
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class UnlockFreeFragment : DialogFragment() {
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private var _binding: FragmentUnlockFreeBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun observeViewModel() {
        homeViewModel.recipeListEvent.observe(viewLifecycleOwner){
            when (it) {
                is RecipeListEvent.OnRecipeClick-> {
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

    private fun setUpViews() {
        binding.closeUnlockFreeBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.watchVideoBtn.setOnClickListener { }
        binding.openAllRecipesBtn.setOnClickListener {
            findNavController().navigate(
                UnlockFreeFragmentDirections.actionUnlockFreeFragmentToGoPremiumFragment()
            )
        }
    }

    override fun getTheme() = R.style.RoundedCornersDialog

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}