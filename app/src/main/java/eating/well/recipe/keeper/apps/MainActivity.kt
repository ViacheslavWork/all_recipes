package eating.well.recipe.keeper.apps

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IPurchasesResponseListener
import com.anjlab.android.iab.v3.PurchaseInfo
import com.anjlab.android.iab.v3.SkuDetails
import eating.well.recipe.keeper.apps.data.database.entity.Category
import eating.well.recipe.keeper.apps.databinding.ActivityMainBinding
import eating.well.recipe.keeper.apps.ui.billing.go_premium.GoPremiumViewModel
import eating.well.recipe.keeper.apps.ui.billing.go_premium.Subscription
import eating.well.recipe.keeper.apps.ui.home.HomeViewModel
import eating.well.recipe.keeper.apps.ui.home.RecipeListEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {
    companion object {
        private const val TAG = "MainActivityLog"
        private const val LICENCE_KEY = R.string.licence_key
        private const val SUBSCRIPTION_ID_TEST = R.string.purchase_id_test
        private const val SUBSCRIPTION_ID_MONTH = "month_subscription"
        private const val SUBSCRIPTION_ID_YEAR = "year_subscription"
    }

    private val homeViewModel: HomeViewModel by viewModel()
    private val goPremiumViewModel: GoPremiumViewModel by viewModel()

    private lateinit var binding: ActivityMainBinding

    private var bp: BillingProcessor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        bp = BillingProcessor.newBillingProcessor(
            this, getString(LICENCE_KEY), this
        )
        bp?.initialize()

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
                    if (!it.hasBeenHandled) {
                        onBackPressed()
                        it.setHandled()
                    }
                }
            }
        }
        homeViewModel.isPremiumLiveData.observe(this) {
            homeViewModel.handleEvent(RecipeListEvent.OnCategoryClick(Category.DIET))
        }
    }

    private fun observeGoPremiumEvents() {
        goPremiumViewModel.goPremiumEvent.observe(this) {
            it.getContentIfNotHandled()?.let { subscription ->
                if (bp?.isSubscriptionUpdateSupported == true) {
                    when (subscription) {
                        Subscription.MONTH -> {
                            bp?.subscribe(this, SUBSCRIPTION_ID_MONTH)
                        }
                        Subscription.YEAR -> {
                            bp?.subscribe(this, SUBSCRIPTION_ID_YEAR)
                        }
                    }
                }
            }
        }
    }

    //billing
    private fun hasSubscription(): Boolean {
        val isMonthSubscribed = bp?.isSubscribed(SUBSCRIPTION_ID_MONTH)
        val isYearSubscribed = bp?.isSubscribed(SUBSCRIPTION_ID_YEAR)
        return isMonthSubscribed == true || isYearSubscribed == true
//        return true
    }

    private fun updateSubscriptionStatus() {
        bp?.loadOwnedPurchasesFromGoogleAsync(object : IPurchasesResponseListener {
            override fun onPurchasesSuccess() {
            }

            override fun onPurchasesError() {
            }
        })
        homeViewModel.makePremium(isPremium = hasSubscription())
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        //call after purchase
        updateSubscriptionStatus()
    }

    override fun onPurchaseHistoryRestored() {
        //called when owned products restored
        lifecycleScope.launch(Dispatchers.IO) {
            for (i in 1..2) {
                delay(1000)
                updateSubscriptionStatus()
                homeViewModel.updateList()
            }
        }
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        getSubscriptionsPrice()
    }

    override fun onBillingInitialized() {
        observeGoPremiumEvents()
        getSubscriptionsPrice()
        updateSubscriptionStatus()
    }

    private fun getSubscriptionsPrice() {
        var monthPrice: Pair<String, String> = Pair("$", "--")
        var yearPrice: Pair<String, String> = Pair("$", "--")
        goPremiumViewModel.putSubscriptionsInfo(Subscription.MONTH, monthPrice)
        goPremiumViewModel.putSubscriptionsInfo(Subscription.YEAR, yearPrice)

        bp?.getSubscriptionListingDetailsAsync(SUBSCRIPTION_ID_MONTH,
            object : BillingProcessor.ISkuDetailsResponseListener {
                override fun onSkuDetailsResponse(products: MutableList<SkuDetails>?) {
                    if (products?.size != 0) {
                        val product = products?.get(0)
                        monthPrice = Pair(product?.currency!!, product.priceText)
                        goPremiumViewModel.putSubscriptionsInfo(Subscription.MONTH, monthPrice)
                    }
                }

                override fun onSkuDetailsError(error: String?) {
                }
            })
        bp?.getSubscriptionListingDetailsAsync(SUBSCRIPTION_ID_YEAR,
            object : BillingProcessor.ISkuDetailsResponseListener {
                override fun onSkuDetailsResponse(products: MutableList<SkuDetails>?) {
                    if (products?.size != 0) {
                        val product = products?.get(0)
                        yearPrice = Pair(product?.currency!!, product.priceText)
                        goPremiumViewModel.putSubscriptionsInfo(Subscription.YEAR, yearPrice)
                    }
                }

                override fun onSkuDetailsError(error: String?) {
                }
            })
    }

    override fun onDestroy() {
        bp?.release()
        super.onDestroy()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showLog(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }
}