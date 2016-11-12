package shop3d.util;

/**
 * Created by Fahad on 11/6/2015.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.parse.ParseObject;
import com.parse.ParseUser;

import shop3d.beans.CategoryType;
import shop3d.beans.SMVSharedModel;
import shop3d.beans.SaleItem;

import java.util.ArrayList;

public class SPManager {

    public static boolean HOT_TESTING = false;

    // Information.
    public static String S_PrintMaterial = "color_plastic";
    public static String S_Units = "in";
    public static String S_Currency = "USD";

    public static boolean PRODUCTION_API_KEYS = true;
    public static String Parse_ApplicationId_ = "90mOHlvDWXIclskinrzj2G1DTMN3C9LebhjakTxA";
    public static String Parse_ClientId_ = "C3R3RXnqqmfW9woHXAlGZOKBJ3ZJ7wzzMdNQg9zt";

    public static String STRIPE_SECRET_KEY = "sk_test_efyKXtwF5NZ4fvmthudsKKpI";
    public static final String STRIPE_PUBLISHABLE_KEY = "pk_live_zbixJivv8pvB3q1IZTYpizU6";

    public static String Fb_App_id = "182072602133670";


    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_PASSWORD = "password";

    public static ParseUser current_user;

    public static ArrayList<SaleItem> CartObjects;
    public static ArrayList<CategoryType> Categories;


    // Stores the pass through object for anything going to SMV;
    public static SMVSharedModel PassToSMV = null;

    // Stores the shipping rates for each country.
    public static ArrayList<ParseObject> ShippingRates;

    // UI Shared Settings
    public static int ListPageSize = 10;
    public static int ShippingAddressLimit = 100;
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static boolean IsPowerUser(){
        if (SPManager.current_user == null)
            return false;
        else
        return SPManager.current_user.getUsername().equals("zoalord12");

    }
    // User name functions
    public static void setUserNameAndPassword(Context ctx, String userName,
                                              String password) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.putString(PREF_USER_PASSWORD, password);
        editor.commit();
    }

    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getUserPassword(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_PASSWORD, "");
    }

    public static void clear(Context ctx) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); // clear all stored data
        editor.commit();
    }
}