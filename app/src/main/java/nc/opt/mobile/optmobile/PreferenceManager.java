package nc.opt.mobile.optmobile;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by orlanth23 on 24/01/2018.
 */

public class PreferenceManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    // Shared preferences file name
    private static final String PREF_NAME = "COLIS-NC";
    private static final String FIRST_TIME = "FIRST_TIME";
    private static final String PREF_ORIGINE = "PREF_ORIGINE";

    public static final String OPT = "OPT";
    public static final String AFTERSHIP = "AFTERSHIP";

    public PreferenceManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(FIRST_TIME, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(FIRST_TIME, true);
    }

    public void setPrefOrigine(String preferenceOrigine) {
        editor.putString(PREF_ORIGINE, preferenceOrigine);
        editor.commit();
    }

    public String getPrefOrigine() {
        return pref.getString(PREF_ORIGINE, OPT);
    }
}