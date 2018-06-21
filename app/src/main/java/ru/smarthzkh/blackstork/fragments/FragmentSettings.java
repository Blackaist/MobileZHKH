package ru.smarthzkh.blackstork.fragments;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.ColorPickerPreference;
import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

import java.util.Objects;

import ru.smarthzkh.blackstork.MainActivity;
import ru.smarthzkh.blackstork.R;
import ru.smarthzkh.blackstork.other.SaveLoadFile;

public class FragmentSettings extends PreferenceFragmentCompatDividers {
    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        testDynamicPrefs();

        EditTextPreference etPref = (EditTextPreference) findPreference("edit_text_test");
        if (etPref != null) {
            int inputType = etPref.getEditText().getInputType();
        }

        Preference pref = (Preference) findPreference("checkbox_preference");
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences settings = getActivity().getSharedPreferences("ru.smarthzkh.blackstork", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor =  settings.edit();
                int Theme;
                boolean checked = Boolean.valueOf(newValue.toString());
                if (checked) {
                    Theme = R.style.AppTheme_NoActionBar_Big;
                    editor.putString("FONT_SIZE", "BIG");
                }
                else {
                    Theme = R.style.AppTheme_NoActionBar_Small;
                    editor.putString("FONT_SIZE", "SMALL");
                }
                getActivity().setTheme(Theme);
                editor.apply();

                Intent intent = getActivity().getIntent();//TODO: Кнопку сохранить которая перезагрузит активити.
                getActivity().finish();
                startActivity(intent);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new FragmentSettings())
                        .commit();

                return true;
            }
        });

        Preference pref1 = (Preference) findPreference("pref_color");
        pref1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()  {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences settings =
                        getActivity().getSharedPreferences("ru.smarthzkh.blackstork", Context.MODE_PRIVATE);
                String fontSizePref = settings.getString("PANEL_COLOR", "-12759625");
                SharedPreferences.Editor editor =  settings.edit();
                System.out.println(newValue.toString());
                ColorPickerPreference s;
                //View d = getActivity().findViewById(R.id.);


                Intent intent = getActivity().getIntent();
                //getActivity().finish();
                getActivity().startActivity(intent);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new FragmentSettings())
                        .commit();
                return true;
            }
        });

        Preference prefEmptyCheck = findPreference("pref_empty_check");
        if(prefEmptyCheck != null) {
            prefEmptyCheck.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!(Boolean) newValue) {
                        findPreference("pref_empty_categ").setTitle(null);
                    } else {
                        findPreference("pref_empty_categ").setTitle("Now you see me");
                    }

                    return true;
                }
            });
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Настройки");
        try {
            return super.onCreateView(inflater, container, savedInstanceState);
        } finally {
            // Uncomment this if you want to change the divider style
            // setDividerPreferences(DIVIDER_OFFICIAL);
        }
    }

    @Override
    public void onDetach() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Мобильный ЖКХ");
        super.onDetach();
    }

    private void testDynamicPrefs() {
        final Context ctx = getPreferenceManager().getContext(); // this is the material styled context

        final PreferenceCategory dynamicCategory = (PreferenceCategory) findPreference("pref_categ");

        Preference prefAdd = findPreference("pref_add");
        if (prefAdd != null) {
            prefAdd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                private int n = 0;

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Preference newPreference = new Preference(ctx);

                    newPreference.setTitle("New preference " + n++);
                    newPreference.setSummary(Long.toString(System.currentTimeMillis()));

                    if (dynamicCategory != null) {
                        dynamicCategory.addPreference(newPreference);
                    }
                    return true;
                }
            });
        }
    }
}