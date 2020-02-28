package com.sherepenko.android.launchiteasy.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.sherepenko.android.launchiteasy.R
import kotlinx.android.synthetic.main.fragment_settings.toolbarView

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }

    private fun setupToolbar() {
        if (requireActivity() is AppCompatActivity) {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarView)
        }

        toolbarView.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setCurrentScreen() {
        FirebaseAnalytics.getInstance(requireActivity())
            .setCurrentScreen(requireActivity(), javaClass.simpleName, null)
    }
}
