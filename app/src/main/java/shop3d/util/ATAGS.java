package shop3d.util;

/**
 * Created by Fahad on 11/25/2015.
 */
public class ATAGS {

    // These are Java activity names embedded into the intent
    // for calling other activities
    public static String TAG_ACTIVITY_MYMODEL = "MyModel";
    public static String TAG_ACTIVITY_MAIN = "MainActivity";
    public static String TAG_ACTIVITY_FAVORITES = "FavoritesActivity";
    public static String TAG_ACTIVITY_FRAGMENT_DISPLAY = "DisplayFragment";

    // These are the differnt types of variables embedded in activity calling
    public static String PASSVALUE_SELECTED_INDEX = "selectedIndex";
    public static String PASSVALUE_CALLER_ACTIVITY = "callerActivity";
    public static String PASSVALUE_ICON_NUMBER = "iconNumber";

    // These are the Parse Table Names used for comparison.
    public static String TABLE_PARSE_FAVORITE = "Favorite";
    public static String TABLE_PARSE_MODEL_USERMADE = "Model_UserMade";
    public static String TABLE_PARSE_MODEL = "Model";


    // ---------------- ACTIVITY TOKENS
    public static int USER_PLACED_ORDER = 1;
}
