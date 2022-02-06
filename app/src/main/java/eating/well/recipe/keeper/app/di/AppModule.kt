package eating.well.recipe.keeper.app.di

import eating.well.recipe.keeper.app.ui.billing.go_premium.GoPremiumViewModel
import eating.well.recipe.keeper.app.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { HomeViewModel(repository = get()) }
    viewModel { GoPremiumViewModel() }
}