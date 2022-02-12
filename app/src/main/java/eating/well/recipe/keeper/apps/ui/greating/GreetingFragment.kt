package eating.well.recipe.keeper.apps.ui.greating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import eating.well.recipe.keeper.apps.R


class GreetingFragment : Fragment() {
        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_greeting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val button = view.findViewById<ImageView>(R.id.see_more_button)
        button.setOnClickListener {
            findNavController().navigate(GreetingFragmentDirections.actionGreetingFragmentToHomeFragment())
        }
        super.onViewCreated(view, savedInstanceState)
    }

}