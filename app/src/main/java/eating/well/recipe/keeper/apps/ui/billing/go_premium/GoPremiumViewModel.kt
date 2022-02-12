package eating.well.recipe.keeper.apps.ui.billing.go_premium

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoPremiumViewModel : ViewModel() {
    private val subscriptionsInfoMap = mutableMapOf<Subscription,Pair<String, String>>()
    private val _mutableGoPremiumEvent = MutableLiveData<GoPremiumEvent>()
    val goPremiumEvent: LiveData<GoPremiumEvent> get() = _mutableGoPremiumEvent
    private val _mutableSubscriptionsInfo = MutableLiveData<Map<Subscription,Pair<String, String>>>()
    val subscriptionInfoLD: LiveData<Map<Subscription,Pair<String, String>>> get() = _mutableSubscriptionsInfo

    fun handleEvent(event: GoPremiumEvent) {
        _mutableGoPremiumEvent.postValue(event)
    }

    fun putSubscriptionsInfo(subscription: Subscription,info: Pair<String, String>) {
        subscriptionsInfoMap[subscription] = info
        _mutableSubscriptionsInfo.postValue(subscriptionsInfoMap.toMap())
    }

}