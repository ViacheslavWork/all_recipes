package eating.well.recipe.keeper.app

import android.os.Bundle
import android.telephony.SubscriptionInfo
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.drawerlayout.widget.DrawerLayout
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IPurchasesResponseListener
import com.anjlab.android.iab.v3.BillingProcessor.ISkuDetailsResponseListener
import com.anjlab.android.iab.v3.PurchaseInfo
import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.databinding.ActivityMainBinding
import eating.well.recipe.keeper.app.ui.billing.go_premium.GoPremiumEvent
import eating.well.recipe.keeper.app.ui.billing.go_premium.GoPremiumViewModel
import eating.well.recipe.keeper.app.ui.billing.go_premium.Subscription
import eating.well.recipe.keeper.app.ui.home.HomeViewModel
import eating.well.recipe.keeper.app.ui.home.RecipeListEvent
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.anjlab.android.iab.v3.SkuDetails


class MainActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {
    companion object {
        private const val TAG = "MainActivity"
        private const val SUBSCRIPTION_ID = "com.anjlab.test.iab.subs1"
        private const val SUBSCRIPTION_ID_MONTH = "android.test.purchased"
        private const val SUBSCRIPTION_ID_YEAR = "android.test.purchased"
    }

    private val homeViewModel: HomeViewModel by viewModel()
    private val goPremiumViewModel: GoPremiumViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    private var bp: BillingProcessor? = null

    var subscriptionMonthInfo: PurchaseInfo? = null
    var subscriptionYearInfo: PurchaseInfo? = null

    var isMonthSubscribed: Boolean? = false
    var isYearSubscribed: Boolean? = false

    var isMonthPurchased: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        bp = BillingProcessor.newBillingProcessor(
            this, getString(R.string.licence_key), this
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
        homeViewModel.isPremiumLiveData.observe(this){
            homeViewModel.handleEvent(RecipeListEvent.OnCategoryClick(Category.PASTA))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun updateSubscriptionStatus() {
        Log.i(TAG, "updateSubscriptionStatus: ")
        bp?.loadOwnedPurchasesFromGoogleAsync(object : IPurchasesResponseListener {
            override fun onPurchasesSuccess() {
                showToast("Subscriptions updated.")
            }

            override fun onPurchasesError() {
                showToast("Subscriptions update error.")
            }
        })
        isMonthSubscribed = bp?.isSubscribed(SUBSCRIPTION_ID_MONTH)
        isYearSubscribed = bp?.isSubscribed(SUBSCRIPTION_ID_YEAR)

        isMonthPurchased = bp?.isPurchased(SUBSCRIPTION_ID_MONTH)

        isMonthSubscribed?.let { homeViewModel.makePremium(it) }



        Log.i(TAG, "isYearSubscribed: $isYearSubscribed")
        Log.i(TAG, "isMonthSubscribed: $isMonthSubscribed")

        Log.i(TAG, "isMonthPurchased: $isMonthPurchased")
        bp?.getPurchaseListingDetailsAsync(SUBSCRIPTION_ID_MONTH,object :ISkuDetailsResponseListener{
            override fun onSkuDetailsResponse(products: MutableList<SkuDetails>?) {
                Log.i(TAG, "Month purchase details: $products")
            }

            override fun onSkuDetailsError(error: String?) {
                Log.i(TAG, "Month purchase details: error")
            }
        })



    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        //call after purchase
        showToast("Subscription COMPLETED")
        Log.i(TAG, "onProductPurchased: ")
        updateSubscriptionStatus()
    }

    override fun onPurchaseHistoryRestored() {
        //called when owned products restored
        Log.i(TAG, "onPurchaseHistoryRestored: ")
        showToast("Subscription history RESTORED")
        updateSubscriptionStatus()
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        showToast("BillingError:${error?.message.toString()}")
        Log.e(TAG, "onBillingError:$errorCode  $error")
    }

    override fun onBillingInitialized() {
        Log.i(TAG, "onBillingInitialized: ")
        updateSubscriptionStatus()
        observeGoPremiumEvents()
    }

    private fun observeGoPremiumEvents() {
        goPremiumViewModel.goPremiumEvent.observe(this) {
            it.getContentIfNotHandled()?.let { subscription ->
                if (bp?.isSubscriptionUpdateSupported == false) {
                    showToast("Subscriptions are not supported")
                } else {
                    when (subscription) {
                        Subscription.MONTH -> {
                            showToast("Month subscription")
                            bp?.purchase(this, SUBSCRIPTION_ID_MONTH)
                        }
                        Subscription.YEAR -> {
                            showToast("Year subscription")
                            bp?.subscribe(this, SUBSCRIPTION_ID_YEAR)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        bp?.release()
        super.onDestroy()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}