package shop3d.parse;

import android.content.Context;

import com.parse.ParseUser;

import shop3d.extras.StripeOperation;
import shop3d.util.Helper;
import shop3d.util.SPManager;

/**
 * Created by Fahad on 11/13/2015.
 */
public class ParseOperation_RegisterLogin {

    public static String RegisterUser(String username, String password, String email, final Context con) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        try {
            user.setEmail(email);

            user.signUp();

            // Create the stipe user
            String stripeId = StripeOperation.CreateCustomer(user.getUsername() + ":" + user.getObjectId());
            user.put("stripe_id", stripeId);
            user.save();

            // Hooray! Let them use the app now.
            Helper.ShowDialogue("Success", "User registered ^_^", con);
            return "success";

        } catch (Exception e1) {
            return e1.getMessage();
        }
    }

    // Login a user after he enters a login and a password.
    public static String LoginUser(String username, String password, final Context con) {
        ParseUser user;
        try {
            user = ParseUser.logIn(username, password);
            if (user != null) {
                // Update new user in the shared objects.
                SPManager.current_user = user;

                // Add to shared preferences
                SPManager.setUserNameAndPassword(con, username,
                        password);
                return "success";


            } else {
                return "Could not login.";
                // Helper.ShowDialogue("Login", "Could not login", con);
            }

        } catch (Exception e1) {
            // FragmentLoginDialogue.DisplayMessage("Error: ", e1.getMessage(), con);
            return e1.getMessage();
        }
    }

}
