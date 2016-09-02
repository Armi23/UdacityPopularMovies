package com.armi.popularmovies;


import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

/**
 * Handles displaying app settings to the user
 */
public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs, rootKey);
        Preference preference = findPreference(getString(R.string.ranking_pref_key));
        preference.setOnPreferenceChangeListener(this);
        String currentSetting = PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), "");
        onPreferenceChange(preference, currentSetting);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ListPreference listPreference = (ListPreference) preference;
        String value = newValue.toString();
        int prefIndex = listPreference.findIndexOfValue(value);
        preference.setSummary(listPreference.getEntries()[prefIndex]);
        return true;
    }

}
