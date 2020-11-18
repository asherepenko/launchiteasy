package com.sherepenko.android.launchiteasy.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.databinding.FragmentSettingsBinding
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSettingsBinding.bind(view)

        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }

    private fun setupToolbar() {
        if (requireActivity() is AppCompatActivity) {
            (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarView)
        }

        binding.toolbarView.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setCurrentScreen() {
        Timber.d("Current fragment: ${javaClass.simpleName}")
        Firebase.analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to javaClass.simpleName,
                FirebaseAnalytics.Param.SCREEN_CLASS to javaClass.canonicalName
            )
        )
    }
}
