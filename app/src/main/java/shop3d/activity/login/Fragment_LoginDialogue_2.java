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

import java.util.Arrays;
import java.util.List;

import shop3d.parse.ParseOperation_RegisterLogin;
import shop3d.util.Helper;
import shop3d.util.SPManager;

import activity.shop3d.org.shop3d.R;


/**
 * A simple counterpart for tab1 layout...
 */
public class Fragment_LoginDialogue_2 extends Fragment {

    Button BtnLogin, BtnFB;

    EditText IvPassword1, IvLogin;
    Context con;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__login_dialogue_2, container, false);


        IvLogin = (EditText) view.findViewById(R.id.frag_login_2_username);
        IvPassword1 = (EditText) view.findViewById(R.id.frag_login_2_password);


        con = view.getContext();

        BtnFB = (Button) view.findViewById(R.id.fb_login_2);
        final List<String> mPermissions = Arrays.asList("public_profile", "email");
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
                            // getUserDetailsFromFB();
                        } else {
                            //Log.d("MyApp", "User logged in through Facebook!");
                            // getUserDetailsFromParse();
                        }
                    }
                });
            }
        });

        BtnLogin = (Button) view.findViewById(R.id.frag_login_2_btn_sign_in);
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckRegisterAndLogin()) {
                    progressDialog = ProgressDialog.show(con, "", "Loading...");

                    new Thread() {
                        public void run() {
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


                            progressDialog.dismiss();
                        }

                    }.start();

                }
            }
        });
        return view;
    }

    // Checks sanity for everything and then tries to login.
    public boolean CheckRegisterAndLogin() {
        if (Helper.isEmpty(IvLogin.getText().toString())) {
            Helper.ShowDialogue("Incomplete: ", "Please enter a username",
                    con);
            return false;
        }

        if (Helper.isEmpty(IvPassword1.getText().toString())) {
            Helper.ShowDialogue("Incomplete: ", "Please enter a password",
                    con);
            return false;
        }


        return true;
    }


}