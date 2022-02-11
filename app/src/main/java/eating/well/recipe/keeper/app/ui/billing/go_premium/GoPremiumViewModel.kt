package eating.well.recipe.keeper.app.ui.billing.go_premium

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoPremiumViewModel : ViewModel() {
    private val _mutableGoPremiumEvent = MutableLiveData<GoPremiumEvent>()
    val goPremiumEvent: LiveData<GoPremiumEvent> get() = _mutableGoPremiumEvent
    private val _mutableSubscriptionsInfo = MutableLiveData<HashMap<Subscription,Pair<String, String>>>()
    val subscriptionInfoLD: LiveData<HashMap<Subscription,Pair<String, String>>> get() = _mutableSubscriptionsInfo

    fun handleEvent(event: GoPremiumEvent) {
        _mutableGoPremiumEvent.postValue(event)
    }

    fun putSubscriptionsInfo(subscriptionsInfo: HashMap<Subscription,Pair<String, String>>) {
        _mutableSubscriptionsInfo.postValue(subscriptionsInfo)
    }

}