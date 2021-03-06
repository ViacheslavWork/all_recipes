package com.android.app.ui.details

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.android.app.R
import com.android.app.data.database.entity.RecipeEntity
import com.android.app.databinding.FragmentDetailsBinding
import com.android.app.databinding.FragmentHomeBinding
import com.android.app.ui.home.HomeViewModel
import com.android.app.ui.home.RecipeListEvent
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val TAG = "DetailsFragment"

class DetailsFragment : Fragment() {
    private var recipeEntity: RecipeEntity? = null
    private var _binding: FragmentDetailsBinding? = null

    private val homeViewModel: HomeViewModel by sharedViewModel()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeEntity = it.getSerializable(ARG_RECIPE) as RecipeEntity
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpToolbar(view)
        setUpViews()

        /*val textView = view.findViewById<TextView>(R.id.details_title_tv)
        val bigText: StringBuilder = StringBuilder("Big text")
        for (i: Int in 1..100) {
            bigText.append("Text\n")
        }
        textView.text = bigText*/

    }

    private fun setUpViews() {
        binding.detailsTitleTv.text = recipeEntity?.title

        binding.timeTv.text = recipeEntity?.prepareTime

        setServing()

        setIngredients()

        setMethod()

        setImage()


    }

    private fun setServing() {
        if (recipeEntity?.serving?.isEmpty() == true) {
            binding.servingTv.isGone = true
        } else {
            binding.servingTv.text = recipeEntity?.serving
        }
    }

    private fun setImage() {
        val smallImage = recipeEntity?.image?.split("/")
        val bigImage = recipeEntity?.detailImage?.split("/")
        Glide.with(requireContext())
            .asBitmap()
            .override(800, 400)
            .fitCenter()
            .error("file:///android_asset/small_images/${smallImage!![smallImage.size - 1]}")
            .load(Uri.parse("file:///android_asset/big_images/${bigImage!![bigImage.size - 1]}"))
            .into(binding.detailsIv)
    }

    private fun setMethod() {
        val methodPoints = StringBuilder()
        recipeEntity?.method?.forEachIndexed { index, methodPointEntity ->
            methodPoints.append("${index + 1}. ${methodPointEntity.title}\n")
            methodPoints.append("${methodPointEntity.content}\n")
        }
        binding.methodTv.text = methodPoints
    }

    private fun setIngredients() {
        val ingredients = StringBuilder()
        recipeEntity?.ingredients?.forEach { ingredients.append("$it\n") }
        ingredients.trim()
        if (ingredients.isNotBlank()) {
            binding.ingredientsTv.text = ingredients.substring(0, ingredients.length - 1)
        }
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.details_toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toolbar.setNavigationOnClickListener {
//            requireActivity().onBackPressed()
            homeViewModel.handleEvent(RecipeListEvent.OnOpenHomeFragmentEvent)
        }
        toolbar.title = getString(R.string.recipe)
    }

    companion object {
        private const val ARG_RECIPE = "recipe"

        @JvmStatic
        fun newInstance(recipeEntity: RecipeEntity) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_RECIPE, recipeEntity)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}