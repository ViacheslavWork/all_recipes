package eating.well.recipe.keeper.apps.ui.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import eating.well.recipe.keeper.apps.R


class TermsConditionsFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_conditions, container, false)
    }

    override fun getTheme() = R.style.FullScreenDialogTheme
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<ImageView>(R.id.exit_terms_conditions_iv)
            .setOnClickListener { requireActivity().onBackPressed() }
    }

}