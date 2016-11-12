package shop3d.activity;

/**
 * Created by Fahad on 11/6/2015.
 */

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import shop3d.parse.ParseOperation;
import shop3d.beans.CategoryType;
import shop3d.beans.SaleItem;
import shop3d.util.Helper;
import shop3d.util.SPManager;


/**
 * Created by Fahad on 11/6/2015.
 */

import java.util.ArrayList;

public class Shop3DApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        // Check to see if you are online.
        Boolean networkState = Helper.AmIOnline(getApplicationContext());

        if (networkState == false) {
            Helper.ShowDialogue("No internet detected",
                    "Please connect to the internet and relaunch.",
                    getApplicationContext());
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        try {
            // This policy change is needed for Stripe.
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            if (SPManager.PRODUCTION_API_KEYS) {
                SPManager.STRIPE_SECRET_KEY = "sk_live_7PTRpxCosKwb54IdNC1dbo4f";
            }

            com.stripe.Stripe.apiKey = SPManager.STRIPE_SECRET_KEY; // stripe public

            // Add your initialization code here
           // FacebookSdk.sdkInitialize(getApplicationContext());
            Parse.initialize(this, SPManager.Parse_ApplicationId_,
                    SPManager.Parse_ClientId_);
         //   ParseFacebookUtils.initialize(context);

           // ParseUser.enableAutomaticUser();
            ParseACL defaultACL = new ParseACL();


            // If you would like all objects to be private by default, remove
            // this line.
            defaultACL.setPublicReadAccess(true);
            defaultACL.setPublicWriteAccess(true);
            ParseACL.setDefaultACL(defaultACL, true);

            SPManager.current_user = ParseUser.getCurrentUser();

           // boolean test = SPManager.current_user.isNew();
           // boolean test2 = SPManager.current_user.isAuthenticated();


            // Setup the categories with "All" generated at runtime.
            SPManager.CartObjects = new ArrayList<SaleItem>();
            SetUpCategoriesArray();

            SPManager.ShippingRates = new ArrayList<ParseObject>();

        } catch (Exception er) {
            Helper.ShowDialogue("Error", er.getMessage(),
                    getApplicationContext());
        }

        long maxHeap = Runtime.getRuntime().maxMemory() / 1024;
        int y = 0;
    }

    public static void SetUpCategoriesArray() {
        SPManager.Categories = new ArrayList<CategoryType>();
        ArrayList<ParseObject> cats = ParseOperation.GetAllCategories();
        int count = 0;
        for (int i = 0; i < cats.size(); i++) {
            SPManager.Categories.add(new CategoryType(cats.get(i)));
            count += cats.get(i).getInt("model_count");
        }
        SPManager.Categories.add(0, new CategoryType("All", "All Categories", count));
    }

    public static Context getContext() {
        return context;
    }

    public static String getMessage(int msgId) {
        return context.getResources().getString(msgId);
    }

    public static String getFormattedMessage(int msgId, Object... args) {
        String message = context.getResources().getString(msgId);
        return String.format(message, args);
    }

}
