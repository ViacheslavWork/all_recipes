package eating.well.recipe.keeper.apps.di

import eating.well.recipe.keeper.apps.ui.billing.go_premium.GoPremiumViewModel
import eating.well.recipe.keeper.apps.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { HomeViewModel(repository = get()) }
    viewModel { GoPremiumViewModel() }
}