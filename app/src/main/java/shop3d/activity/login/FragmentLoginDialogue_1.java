package shop3d.activity.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

import shop3d.parse.ParseOperation_RegisterLogin;
import shop3d.util.Helper;
import shop3d.util.SPManager;

import activity.shop3d.org.shop3d.R;


/**
 * A simple counterpart for tab1 layout...
 */
public class FragmentLoginDialogue_1 extends Fragment {
    Button BtnRegister, BtnFB;
    EditText IvPassword1, IvPassword2, IvEmail, IvLogin;
    Context con;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_dialogue_1, container, false);

        IvLogin = (EditText) view.findViewById(R.id.frag_login_1_username);
        IvEmail = (EditText) view.findViewById(R.id.frag_login_1_email);
        IvPassword1 = (EditText) view.findViewById(R.id.frag_login_1_password_1);
        IvPassword2 = (EditText) view.findViewById(R.id.frag_login_1_password_2);


        con = view.getContext();

        BtnFB = (Button) view.findViewById(R.id.fb_login_1);
        final List<String> mPermissions = Arrays.asList("email,name");
        BtnFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(getParentFragment().getActivity(), mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            // Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            // Log.d("MyApp", "User signed up and logged in through Facebook!");
                            getUserDetailsFromFB();
                        } else {
                            //Log.d("MyApp", "User logged in through Facebook!");
                            // getUserDetailsFromParse();
                        }
                    }
                });
            }
        });


        BtnRegister = (Button) view.findViewById(R.id.frag_login_1_btn_register);
        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckRegisterAndLogin()) {
                    boolean isFB = false;

                    BeginRegistration(isFB, "");
                }
            }
        });
        return view;
    }

    public void BeginRegistration(final Boolean isFB, final String email) {
        progressDialog = ProgressDialog.show(con, "", "Loading...");

        new Thread() {
            public void run() {
                // This also registeres the stripe user
                String registerResponse = "";
                if (isFB) {
                    registerResponse = ParseOperation_RegisterLogin.RegisterUser( email,
                            "", email, con.getApplicationContext());

                }
                else {
                    registerResponse = ParseOperation_RegisterLogin.RegisterUser(IvLogin.getText().
                                    toString(), IvPassword1.getText().toString(), IvEmail.getText().toString(),
                            con.getApplicationContext());
                }
                if (registerResponse.equals("success")) {
                    FragmentLoginDialogue.DisplayMessage("Success: ", " User Registered.", con);
                    String loginResponse = ParseOperation_RegisterLogin.LoginUser(IvLogin.getText().
                            toString(), IvPassword1.getText().toString(), con.getApplicationContext());


                    if (loginResponse.equals("success")) {
                        // If user successfully register AND
                        // User successfully logs in, then dismiss dialogue.
                        FragmentLoginDialogue.DisplayMessage("Success: ", " User logged in.", con);
                        SPManager.setUserNameAndPassword(con.getApplicationContext(), IvLogin.getText().
                                        toString(),
                                IvPassword1.getText().toString());
                        FragmentLoginDialogue.dialog.dismiss();


                    } else {
                        FragmentLoginDialogue.DisplayMessage("Error: ", loginResponse, con);

                    }

                } else {
                    FragmentLoginDialogue.DisplayMessage("Error: ", registerResponse, con);

                }
                progressDialog.dismiss();
            }

        }.start();


    }
    private void getUserDetailsFromFB() {
       /* // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result
                        try {
                           String fbEmail = response.getJSONObject().getString("email");
                            if (fbEmail.equals("")) {
                                Helper.ShowDialogue("Cant use Facebook login: ", "You have to register with the app directly", getContext());
                            }
                            BeginRegistration(true, fbEmail );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();*/
    }

    // Checks sanity for everything and then tries to login.
    public boolean CheckRegisterAndLogin() {
        if (Helper.isEmpty(IvLogin.getText().toString())) {
            Helper.ShowDialogue("Incomplete: ", "Please enter a username",
                    con);
            return false;
        }

        if (IvLogin.getText().toString().length() < 5) {
            Helper.ShowDialogue("Login size: ", " Should be greater than 4 characters.",
                    con);
            return false;
        }

        if (IvPassword1.getText().toString().length() < 5) {
            Helper.ShowDialogue("Password size: ", " Should be greater than 4 characters.",
                    con);
            return false;
        }

        if (Helper.isEmpty(IvEmail.getText().toString())) {
            Helper.ShowDialogue("Incomplete: ", "Please enter an email address",
                    con);
            return false;
        }

        if (Helper.isEmpty(IvPassword1.getText().toString())) {
            Helper.ShowDialogue("Incomplete: ", "Please enter a password",
                    con);
            return false;
        }
        if (IvPassword1.getText().toString().equals(IvPassword2.getText().toString()) == false) {
            Helper.ShowDialogue("Mismatch: ", "Passwords do not match.",
                    con);
            return false;
        }

        return true;
    }


}