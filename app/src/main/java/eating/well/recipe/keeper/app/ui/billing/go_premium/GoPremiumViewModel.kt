package eating.well.recipe.keeper.app.ui.billing.go_premium

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoPremiumViewModel : ViewModel() {
    private val _mutableGoPremiumEvent = MutableLiveData<GoPremiumEvent>()
    val goPremiumEvent: LiveData<GoPremiumEvent> get() = _mutableGoPremiumEvent

    fun handleEvent(event: GoPremiumEvent) {
        _mutableGoPremiumEvent.postValue(event)
    }

}