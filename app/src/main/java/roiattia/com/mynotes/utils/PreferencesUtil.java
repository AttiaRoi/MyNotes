package roiattia.com.mynotes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import static roiattia.com.mynotes.utils.Constants.PREF_SHOW_CREATION_DATE;
import static roiattia.com.mynotes.utils.Constants.PREF_SHOW_LAST_EDIT_DATE;
import static roiattia.com.mynotes.utils.Constants.PREF_SHOW_REMINDER_DATE;

public class PreferencesUtil {

    private PreferencesUtil(){ }

    public static boolean getShowCreationDate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_SHOW_CREATION_DATE, false);
    }

    public static boolean getShowLastEditDate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_SHOW_LAST_EDIT_DATE, true);
    }

    public static boolean getShowReminderDate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_SHOW_REMINDER_DATE, true);
    }

    public static void setFields(Context context, ArrayList<Integer> selectedItems) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        if(selectedItems.contains(0)){
            editor.putBoolean(PREF_SHOW_CREATION_DATE, true);
        } else {
            editor.putBoolean(PREF_SHOW_CREATION_DATE, false);
        }
        if(selectedItems.contains(1)){
            editor.putBoolean(PREF_SHOW_LAST_EDIT_DATE, true);
        } else {
            editor.putBoolean(PREF_SHOW_LAST_EDIT_DATE, false);
        }
        if(selectedItems.contains(2)){
            editor.putBoolean(PREF_SHOW_REMINDER_DATE, true);
        } else {
            editor.putBoolean(PREF_SHOW_REMINDER_DATE, false);
        }
        editor.apply();
    }
}
