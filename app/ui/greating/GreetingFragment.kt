package com.android.app.ui.greating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.app.R
import com.android.app.ui.home.HomeViewModel
import com.android.app.ui.home.RecipeListEvent
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [GreetingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GreetingFragment : Fragment() {
    private val homeViewModel: HomeViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_greeting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val button = view.findViewById<ImageView>(R.id.see_more_button)
        button.setOnClickListener {
            homeViewModel.handleEvent(RecipeListEvent.OnOpenHomeFragmentEvent)
        }
        super.onViewCreated(view, savedInstanceState)
    }

}