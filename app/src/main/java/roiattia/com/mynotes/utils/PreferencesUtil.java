package roiattia.com.mynotes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import static roiattia.com.mynotes.utils.Constants.PREF_FIRST_NOTE;
import static roiattia.com.mynotes.utils.Constants.PREF_SHOW_CREATION_DATE;
import static roiattia.com.mynotes.utils.Constants.PREF_SHOW_LAST_EDIT_DATE;
import static roiattia.com.mynotes.utils.Constants.PREF_SHOW_REMINDER_DATE;
import static roiattia.com.mynotes.utils.Constants.PREF_SORT_FOLDER_BY_OPTION;
import static roiattia.com.mynotes.utils.Constants.PREF_SORT_NOTE_BY_OPTION;

public class PreferencesUtil {


    private PreferencesUtil(){ }

    public static boolean getFirstNoteInserted(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_FIRST_NOTE, false);
    }

    public static int getSortNotesByOption(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(PREF_SORT_NOTE_BY_OPTION, 0);
    }

    public static int getSortFoldersByOption(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(PREF_SORT_FOLDER_BY_OPTION, 0);
    }

    public static boolean[] getFields(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean[] selectedFields = new boolean[3];
        selectedFields[0] = prefs.getBoolean(PREF_SHOW_CREATION_DATE, false);
        selectedFields[1] = prefs.getBoolean(PREF_SHOW_LAST_EDIT_DATE, true);
        selectedFields[2] = prefs.getBoolean(PREF_SHOW_REMINDER_DATE, true);
        return selectedFields;
    }

    public static void setFirstNoteInserted(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_FIRST_NOTE, true);
        editor.apply();
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

    public static void setSortNotesByOption(Context context, int option) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_SORT_NOTE_BY_OPTION, option);
        editor.apply();
    }

    public static void setSortFoldersByOption(Context context, int option) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_SORT_NOTE_BY_OPTION, option);
        editor.apply();
    }
}
