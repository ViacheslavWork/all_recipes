package eating.well.recipe.keeper.app.ui.billing

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import eating.well.recipe.keeper.app.R


class UnlockFreeFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unlock_free, container, false)
    }

    override fun getTheme() = R.style.RoundedCornersDialog

}