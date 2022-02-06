package eating.well.recipe.keeper.app.ui.billing.go_premium

import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import eating.well.recipe.keeper.app.R
import eating.well.recipe.keeper.app.databinding.FragmentGoPremiumBinding
import eating.well.recipe.keeper.app.model.Recipe
import eating.well.recipe.keeper.app.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val TAG = "GoPremiumFragment"


class GoPremiumFragment : DialogFragment() {
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private val goPremiumViewModel: GoPremiumViewModel by sharedViewModel()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImagesAdapter

    private var _binding: FragmentGoPremiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGoPremiumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        setUpRecyclerView(view, layoutManager)
        setUpAdapter()
        observeState()

        setUpViews()

        if (!BillingProcessor.isIabServiceAvailable(context)) {
            Toast.makeText(
                context,
                "In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16",
                Toast.LENGTH_LONG
            ).show();
        }
    }

    private fun setUpViews() {
        binding.crossGoPremiumIv.setOnClickListener { requireActivity().onBackPressed() }

        binding.tryForFreeBtn.setOnClickListener { goPremiumViewModel.handleEvent(GoPremiumEvent(Subscription.YEAR)) }
        binding.perMonthBtn.setOnClickListener { goPremiumViewModel.handleEvent(GoPremiumEvent(Subscription.MONTH)) }

        val cs = SpannableStringBuilder(resources.getString(R.string.try_for_free_7_days))
        cs.setSpan(RelativeSizeSpan(0.625f), 20, cs.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tryForFreeBtn.text = cs

        binding.subscriptionDetailsTv.setOnClickListener {
            findNavController().navigate(
                GoPremiumFragmentDirections.actionGoPremiumFragmentToTermsConditionsFragment()
            )
        }
    }
    private fun observeState() {
        homeViewModel.recipes.observe(viewLifecycleOwner) {
            when {
                it.error.isNotBlank() -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_LONG).show()
                }
                it.isLoading -> {
                    Toast.makeText(requireContext(), "Loading!!!", Toast.LENGTH_LONG).show()
                }
                else -> {
                    adapter.submitList(it.recipes.toMutableList())
                    recyclerView.layoutManager?.scrollToPosition(it.recipes.size / 2)
                }
            }
        }
    }

    private fun setUpRecyclerView(view: View, layoutManager: RecyclerView.LayoutManager) {
        recyclerView = view.findViewById(R.id.go_premium_rv)
        recyclerView.layoutManager = layoutManager
    }

    private fun setUpAdapter() {
        adapter = ImagesAdapter()
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recyclerView.adapter = adapter
    }


    override fun getTheme() = R.style.FullScreenDialogTheme


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //billing
/*    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        TODO("Not yet implemented")
    }

    override fun onPurchaseHistoryRestored() {
        TODO("Not yet implemented")
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Toast.makeText(context, error?.message.toString(), Toast.LENGTH_LONG).show()
        Log.e(TAG, "onBillingError: $error")
    }

    override fun onBillingInitialized() {
        Toast.makeText(context, "Billing initialized", Toast.LENGTH_LONG).show()
        binding.tryForFreeBtn.setOnClickListener {
            Toast.makeText(
                context,
                "What price?",
                Toast.LENGTH_LONG
            ).show()

        }
        binding.perMonthBtn.setOnClickListener {
            Toast.makeText(
                context,
                "What price?",
                Toast.LENGTH_LONG
            ).show()
        }
    }*/
}

class ImagesAdapter : ListAdapter<Recipe, RecipeImageViewHolder>(RecipeDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeImageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_image, parent, false)
        return RecipeImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeImageViewHolder, position: Int) {
        getItem(position).let { recipe -> holder.onBind(recipe) }
    }
}

class RecipeImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val image: ImageView = itemView.findViewById(R.id.go_premium_iv)

    companion object {
        private val imageOption = RequestOptions()
            .placeholder(R.drawable.unlock_placeholder)
            .fallback(R.drawable.unlock_placeholder)
    }

    fun onBind(recipe: Recipe) {
        val bigImage = recipe.detailImage.split("/")
        val smallImage = recipe.image.split("/")
        Glide.with(itemView.context)
            .load(Uri.parse("file:///android_asset/small_images/${smallImage[smallImage.size - 1]}"))
            .centerCrop()
            .error("file:///android_asset/big_images/${bigImage[bigImage.size - 1]}")
            .apply(imageOption)
            .into(image)
    }
}

private class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
        (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
        (oldItem == newItem)
}


