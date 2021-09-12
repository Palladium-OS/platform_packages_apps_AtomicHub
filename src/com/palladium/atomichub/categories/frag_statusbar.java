package com.palladium.atomichub.categories;

import android.os.Bundle;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.*;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import com.android.settings.R;
import android.app.ActionBar;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;
import android.os.ServiceManager;
import com.palladium.atomichub.*;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.provider.SearchIndexableResource;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import android.text.TextUtils;
import com.android.internal.util.palladium.PalladiumUtils;
import com.palladium.support.preferences.SystemSettingListPreference;
import android.provider.Settings;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)

public class frag_statusbar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private IOverlayManager mOverlayService;
    private static final String LAYOUT_SETTINGS = "navbar_layout_views";
    private static final String NAVIGATION_BAR_INVERSE = "navbar_inverse_layout";
    private static final String PREF_KEY_CUTOUT = "cutout_settings";
    private static final String VOLTE_ICON_STYLE = "volte_icon_style";
    private static final String VOWIFI_ICON_STYLE = "vowifi_icon_style";

    private SystemSettingListPreference mVolteIconStyle;
    private SystemSettingListPreference mVowifiIconStyle;

    private Preference mLayoutSettings;
    private SwitchPreference mSwapNavButtons;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ps_statusbar);
        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
        //Feature Additon!
        final PreferenceScreen prefScreen = getPreferenceScreen();
        mLayoutSettings = findPreference(LAYOUT_SETTINGS);
        mSwapNavButtons = findPreference(NAVIGATION_BAR_INVERSE);

        Preference mCutoutPref = (Preference) findPreference(PREF_KEY_CUTOUT);
        String hasDisplayCutout = getResources().getString(com.android.internal.R.string.config_mainBuiltInDisplayCutout);

        mVolteIconStyle = (SystemSettingListPreference) findPreference(VOLTE_ICON_STYLE);
        int volteIconStyle = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.VOLTE_ICON_STYLE, 0);
        mVolteIconStyle.setValue(String.valueOf(volteIconStyle));
        mVolteIconStyle.setOnPreferenceChangeListener(this);

        mVowifiIconStyle = (SystemSettingListPreference) findPreference(VOWIFI_ICON_STYLE);
        int vowifiIconStyle = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.VOWIFI_ICON_STYLE, 0);
        mVowifiIconStyle.setValue(String.valueOf(vowifiIconStyle));
        mVowifiIconStyle.setOnPreferenceChangeListener(this);

        if (TextUtils.isEmpty(hasDisplayCutout)) {
            getPreferenceScreen().removePreference(mCutoutPref);
        }
        if (!PalladiumUtils.isThemeEnabled("com.android.internal.systemui.navbar.threebutton")) {
            prefScreen.removePreference(mLayoutSettings);
        }
            prefScreen.removePreference(mSwapNavButtons);

    }
    @Override
    public int getMetricsCategory() {
        return MetricsEvent.PALLADIUM;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
         if (preference == mVolteIconStyle) {
            int volteIconStyle = Integer.parseInt(((String) newValue).toString());
            Settings.System.putInt(resolver,
                  Settings.System.VOLTE_ICON_STYLE, volteIconStyle);
            mVolteIconStyle.setValue(String.valueOf(volteIconStyle));
            PalladiumUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mVowifiIconStyle) {
            int vowifiIconStyle = Integer.parseInt(((String) newValue).toString());
            Settings.System.putInt(resolver,
                  Settings.System.VOWIFI_ICON_STYLE, vowifiIconStyle);
            mVowifiIconStyle.setValue(String.valueOf(vowifiIconStyle));
            PalladiumUtils.showSystemUiRestartDialog(getContext());
            return true;
        }
        return false;
    }

    /**
     * For Search
     */

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.ps_statusbar;
                    result.add(sir);
                    return result;
                }

           @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}