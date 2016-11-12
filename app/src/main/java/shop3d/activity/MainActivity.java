package shop3d.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.ImageDisplay.FragmentDisplayDialogue;
import shop3d.adapters.MainListViewAdapter;
import shop3d.beans.SMVSharedModel;
import shop3d.util.ATAGS;
import shop3d.parse.ParseOperation;
import shop3d.parse.ParseOperation_Dashboard;
import shop3d.parse.ParseOperation_RegisterLogin;
import shop3d.beans.CategoryType;
import shop3d.util.Helper;
import shop3d.util.ImageCache;
import shop3d.util.SPManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shop3d.activity.login.FragmentLoginDialogue;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    NavigationView navigationView;
    Dialog progressDialog;
    boolean clickedSearchButton;

    EditText SearchKeyword;
    private ListView listView;
    private MainListViewAdapter adapter;
    private LinearLayout mSearchContainerView;

    public static ArrayList<ParseObject> listOfModels;
    private Spinner spinrCategory;
    private Spinner spinrPopular;
    public Button btnLoadMore;
    public Button btnSearch;

    FloatingActionButton fab;

    //------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clickedSearchButton = false;

        if (SPManager.IsPowerUser()) {
            Helper.ShowDialogue("POWER USER: ", "SHOWING MODELS with VISIBLE = FALSE as well", getApplicationContext());
        }

        //navMyModels = (Menu) findViewById(R.id.nav_mymodels);
        //navOrders = (Menu) findViewById(R.id.nav_orders);
        //navFavorites= (Menu) findViewById(R.id.nav_favorites);

        setContentView(R.layout.activity_main);
        mSearchContainerView = (LinearLayout) findViewById(R.id.main_searchBox);
        mSearchContainerView.setVisibility(View.GONE);

        spinrCategory = (Spinner) findViewById(R.id.main_spinnerCategory);
        spinrPopular = (Spinner) findViewById(R.id.main_spinnerPopular);

        LoadCategoriesInSpinner();
        LoadPopulrSpinner();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.LaunchActivity(getApplicationContext(),
                        CartActivity.class);
                //     Snackbar.make(view, "Added item " + + " to your cart.", Snackbar.LENGTH_LONG)
                //            .setAction("Action", null).show();
            }
        });

        // fab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#mycolor")));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        PersonalNavItemsSet();


        listOfModels = new ArrayList<ParseObject>();

        // Populate the list here.

        listView = (ListView) findViewById(R.id.listView);
        //adapter = new MainListViewAdapter(getApplicationContext(),
        //      R.layout.list_item, cityList);
        //adapter = new MainListViewAdapter(this);

        //listView.setAdapter(adapter);
        // listView.requestLayout();

        SearchKeyword = (EditText) findViewById(R.id.main_et_search);

        // Search Button
        btnSearch = (Button) findViewById(R.id.main_bt_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                clickedSearchButton = true;
                int popularityIndex = spinrPopular.getSelectedItemPosition();
                int categoryIndex = spinrCategory.getSelectedItemPosition();
                current_page = 0;
                RefreshModelListFirstTime(SearchKeyword.getText().toString(), categoryIndex, popularityIndex, current_page);
            }
        });

        // ------------- Load more button
        // Creating a button - Load More
        btnLoadMore = new Button(this);
        btnLoadMore.setBackgroundResource(R.drawable.background_btn_state);
        btnLoadMore.setTextColor(getResources().getColor(R.color.white_color));
        btnLoadMore.setText("Load More");

        // Adding button to listview at footer
        // listView.addFooterView(btnLoadMore);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Starting a new async task
                new loadMoreListView().execute();
            }
        });

        /**
         * Listening to listview single row selected
         * **/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ImageCache.RemoveAllExceptIcon0();
                // Starting new intent
                SPManager.PassToSMV = new SMVSharedModel(listOfModels.get(position));
                Intent in = new Intent(getApplicationContext(),
                        SingleModelView.class);
                // in.putExtra(ATAGS.PASSVALUE_SELECTED_INDEX, position);
                // in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, ATAGS.TAG_ACTIVITY_MAIN);
                startActivity(in);
            }
        });
        // ------------
        // ---- Writing the onscoll listener
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ?
                                0 : listView.getChildAt(0).getTop();
                // swipeView.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        int popularityIndex = spinrPopular.getSelectedItemPosition();
        int categoryIndex = spinrCategory.getSelectedItemPosition();
        current_page = 0;
        RefreshModelListFirstTime("", categoryIndex, popularityIndex, current_page);
        //    Helper.LaunchActivity(getApplicationContext(),
        //       PaymentActivity.class);

        FixFabVisibility();
    }

    public void SearchAndUpdate() {

    }

    public void LoadCategoriesInSpinner() {
        List<String> cats = new ArrayList<String>();
        for (int i = 0; i < SPManager.Categories.size(); i++) {
            CategoryType obj = (CategoryType) SPManager.Categories.get(i);
            cats.add(obj.Name + " (" + obj.Count + ")");
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner, cats);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinrCategory.setAdapter(dataAdapter);
    }


    public void LoadPopulrSpinner() {

        final List<String> list = new ArrayList<String>();
        list.add("Latest First");
        list.add("Oldest First");
        list.add("Lowest Price");
        list.add("Highest Price");
        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
                R.layout.item_spinner, list);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinrPopular.setAdapter(adp1);
    }

    public void RefreshModelListFirstTime(String keyword, int categoryIndex, int popularityIndex, int curPage) {
        ArrayList<ParseObject> allModels = ParseOperation_Dashboard.GetAllModels(keyword, categoryIndex, popularityIndex, curPage);
        listOfModels.clear();
        listView.removeFooterView(btnLoadMore);

        listOfModels.addAll(allModels);

        // Adding button to listview at footer
        if (listOfModels.size() < SPManager.ListPageSize) {
            listView.removeFooterView(btnLoadMore);
        } else
            listView.addFooterView(btnLoadMore);


        adapter = new MainListViewAdapter(this);

        listView.setAdapter(adapter);
        listView.requestLayout();
        listView.invalidateViews();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            if (mSearchContainerView.getVisibility() == View.GONE) {
                mSearchContainerView.setVisibility(View.VISIBLE);

            } else {
                mSearchContainerView.setVisibility(View.GONE);
                String sKey = SearchKeyword.getText().toString();
                if (sKey.equals("") == false
                        || spinrCategory.getSelectedItemPosition() != 0
                        || spinrPopular.getSelectedItemPosition() != 0
                        || (clickedSearchButton == true)) {
                    SearchKeyword.setText("");
                    spinrCategory.setSelection(0);
                    spinrPopular.setSelection(0);


                    current_page = 0;
                    RefreshModelListFirstTime("", 0, 0, current_page);

                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void DisplayLoginDialogue() {
/*
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialogue_login);
        dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button

        Button dialogButton = (Button) dialog.findViewById(R.id.sign_in_button);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

  */
        progressDialog = ProgressDialog.show(MainActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "email");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)
/*
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    //   Log.d(IntegratingFacebookTutorialApplication.TAG, "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    //   Log.d(IntegratingFacebookTutorialApplication.TAG, "User signed up and logged in through Facebook!");
                    //  showUserDetailsActivity();
                } else {
                    SPManager.current_user = user;

                    user.saveInBackground();

                    //  Log.d(IntegratingFacebookTutorialApplication.TAG, "User logged in through Facebook!");
                    //  showUserDetailsActivity();
                }
            }
        });
*/

    }

    void FixFabVisibility() {

        if (SPManager.CartObjects.size() > 0)
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        PersonalNavItemsSet();

        FixFabVisibility();
        // Helper.ShowDialogue("on resume", " called", getApplicationContext());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            current_page = 0;
            RefreshModelListFirstTime("", 0, 0, current_page);

        } else if (id == R.id.nav_mymodels) {
            Helper.LaunchActivity(getApplicationContext(),
                    MyModelActivity.class);


        } else if (id == R.id.nav_authorsnotes) {
            Helper.LaunchActivity(getApplicationContext(),
                    AuthorNoteActivity.class);


        } else if (id == R.id.nav_favorites) {
            Helper.LaunchActivity(getApplicationContext(),
                    FavoritesActivity.class);

        } else if (id == R.id.nav_orders) {
            Helper.LaunchActivity(getApplicationContext(),
                    MyOrderActivity.class);
        } else if (id == R.id.nav_account) {
            if (SPManager.current_user == null) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentLoginDialogue overlay = new FragmentLoginDialogue();
                FragmentLoginDialogue.CallerActivity = "MainActivity";
                overlay.show(fm, "register/sign-in");
            } else {
                Helper.LaunchActivity(getApplicationContext(),
                        AccountActivity.class);
            }

        } else if (id == R.id.nav_feedback) {
            Helper.LaunchActivity(getApplicationContext(),
                    FeedbackActivity.class);


        } else if (id == R.id.nav_howitworks) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentDisplayDialogue.TapTozoom = true;
            FragmentDisplayDialogue.SELECTEDARRAY = 0;
            FragmentDisplayDialogue overlay = new FragmentDisplayDialogue();
            overlay.show(fm, "How it Works");


        } else if (id == R.id.nav_logout) {
            ParseOperation.LogOutCurrentUser(getApplicationContext());
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onStart() {
        super.onStart();

        // Check to see if you are online.
        Boolean networkState = Helper.AmIOnline(getApplicationContext());

        if (networkState == false) {
            Helper.ShowDialogue("No internet detected",
                    "Please connect to the internet and relaunch.",
                    getApplicationContext());
            finish();
            return;
        }

        // Check to see if account exists
        if (SPManager.getUserName(MainActivity.this).length() == 0) {
            // Helper.ShowDialogue("No saved used", "No saved password",
            //       getApplicationContext());
        } else if (SPManager.current_user == null) {
            // Attempt login
            String loginResponse = ParseOperation_RegisterLogin.LoginUser(
                    SPManager.getUserName(getApplicationContext()), SPManager.getUserPassword(getApplicationContext()),
                    getApplicationContext());

            if (loginResponse.equals("success")) {
                // If user successfully register AND
                // User successfully logs in, then dismiss dialogue.
                FragmentLoginDialogue.DisplayMessage("Success: ", " User logged in.", MainActivity.this);


            } else {
                FragmentLoginDialogue.DisplayMessage("Error: ", loginResponse, MainActivity.this);

            }

        }
    }

    public void PersonalNavItemsSet() {
        boolean status = false;
        if (SPManager.current_user == null) {
            navigationView.getMenu().findItem(R.id.nav_favorites).setEnabled(status);
            AddStar(R.id.nav_favorites);

            navigationView.getMenu().findItem(R.id.nav_mymodels).setEnabled(status);
            AddStar(R.id.nav_mymodels);

            navigationView.getMenu().findItem(R.id.nav_orders).setEnabled(status);
            AddStar(R.id.nav_orders);

            navigationView.getMenu().findItem(R.id.nav_logout).setEnabled(status);
            AddStar(R.id.nav_logout);

            navigationView.getMenu().findItem(R.id.nav_account).setTitle("*Register/Sign-in");


        } else {
            status = true;
            navigationView.getMenu().findItem(R.id.nav_favorites).setEnabled(status);
            RemoveStar(R.id.nav_favorites);
            navigationView.getMenu().findItem(R.id.nav_mymodels).setEnabled(status);
            RemoveStar(R.id.nav_mymodels);
            navigationView.getMenu().findItem(R.id.nav_orders).setEnabled(status);
            RemoveStar(R.id.nav_orders);
            navigationView.getMenu().findItem(R.id.nav_logout).setEnabled(status);
            RemoveStar(R.id.nav_logout);

            navigationView.getMenu().findItem(R.id.nav_account).setTitle("Account");

        }

    }

    void AddStar(int id) {
        MenuItem mi = navigationView.getMenu().findItem(id);
        if (mi.getTitle().toString().contains("*") == false)
            mi.setTitle(mi.getTitle() + "*");
    }

    void RemoveStar(int id) {
        MenuItem mi = navigationView.getMenu().findItem(id);
        if (mi.getTitle().toString().contains("*") == true)
            mi.setTitle(mi.getTitle().subSequence(0, mi.getTitle().toString().length() - 1));
    }

    /* Async Task that send a request to url
    * Gets new list view data
    * Appends to list view
    * */

    ProgressDialog pDialog;

    // Flag for current page
    int current_page = 0;

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(
                    MainActivity.this);
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    int popularityIndex = spinrPopular.getSelectedItemPosition();
                    int categoryIndex = spinrCategory.getSelectedItemPosition();

                    // increment current page sinnce we want next page items.
                    current_page += 1;


                    ArrayList<ParseObject> nextPageModels = ParseOperation_Dashboard.GetAllModels(SearchKeyword.getText().toString(), categoryIndex, popularityIndex, current_page);
                    MainActivity.listOfModels.addAll(nextPageModels);


                    // Remove the load button if you have all the records.
                    if (nextPageModels.size() < SPManager.ListPageSize) {
                        listView.removeFooterView(btnLoadMore);
                    }

                    // get listview current position - used to maintain scroll position
                    int currentPosition = listView.getFirstVisiblePosition();

                    // Appending new data to menuItems ArrayList
                    adapter = new MainListViewAdapter(
                            MainActivity.this);
                    listView.setAdapter(adapter);
                    // Setting new scroll position
                    listView.setSelectionFromTop(currentPosition + 1, 0);

                }
            });

            return (null);
        }

        protected void onPostExecute(Void unused) {
            // closing progress dialog
            pDialog.dismiss();
        }
    }
}
