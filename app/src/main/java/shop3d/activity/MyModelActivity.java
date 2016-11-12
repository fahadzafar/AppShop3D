package shop3d.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.threed.jpct.Logger;

import java.util.ArrayList;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.modifiers.ColorModManager;
import shop3d.adapters.MyModelListViewAdapter;
import shop3d.beans.SMVSharedModel;
import shop3d.util.ATAGS;
import shop3d.util.Helper;
import shop3d.util.ImageCache;
import shop3d.util.SPManager;
import shop3d.parse.ParseOperation;

public class MyModelActivity extends AppCompatActivity {

    static ListView listView;
    public Button btnLoadMore;

    private MyModelListViewAdapter adapter;

    public static ArrayList<ParseObject> listOfModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);


        listView = (ListView) findViewById(R.id.favorites_listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //   ImageCache.RemoveAllExceptIcon0();
                // Starting new intent

                ParseObject child = listOfModels.get(position);
                ParseObject parentModelObj = child.getParseObject("original_model_id");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Model");
                query.whereEqualTo("objectId", parentModelObj.getObjectId());

                try {
                    parentModelObj = query.getFirst();
                } catch (Exception er) {

                }
                if (parentModelObj.isDataAvailable() == false) {
                    Helper.ShowDialogue("Sorry: ", "This model is not available, its base model might have been updated", getApplicationContext());
                    return;
                }
                // ---------------------


                SPManager.PassToSMV = new SMVSharedModel(parentModelObj, child, 1);

                Intent in = new Intent(getApplicationContext(),
                        SingleModelView.class);
                in.putExtra(ATAGS.PASSVALUE_SELECTED_INDEX, position);
                in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, ATAGS.TAG_ACTIVITY_MYMODEL);
                startActivity(in);


         /*
                Intent in = new Intent(getApplicationContext(),
                        FullScreenImageActivity.class);
                in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, ATAGS.TAG_ACTIVITY_MYMODEL);
                in.putExtra(ATAGS.PASSVALUE_SELECTED_INDEX, position);
                in.putExtra(ATAGS.PASSVALUE_ICON_NUMBER, "icon_0");
                startActivity(in);*/
            }
        });

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

        listOfModels = new ArrayList<ParseObject>();


        current_page = 0;
        RefreshModelListFirstTime(current_page);
    }

    public void RefreshModelListFirstTime(int curPage) {
        ArrayList<ParseObject> allModels = ParseOperation.GetAllMyModels(curPage);
        listOfModels.clear();
        listView.removeFooterView(btnLoadMore);

        listOfModels.addAll(allModels);

        // Adding button to listview at footer
        if (listOfModels.size() < SPManager.ListPageSize) {
            listView.removeFooterView(btnLoadMore);
        } else
            listView.addFooterView(btnLoadMore);
        adapter = new MyModelListViewAdapter(this);

        listView.setAdapter(adapter);
        listView.requestLayout();
    }

    public static void RefreshFavoriteList() {
        listView.invalidateViews();
        listView.requestLayout();

    }

    @Override
    protected void onResume() {
        current_page = 0;
        RefreshModelListFirstTime(current_page);
        super.onResume();


    }

    ProgressDialog pDialog;

    // Flag for current page
    int current_page = 0;

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(
                    MyModelActivity.this);
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    // increment current page sinnce we want next page items.
                    current_page += 1;

                    ArrayList<ParseObject> nextPageModels = ParseOperation.GetAllMyModels(current_page);
                    MyModelActivity.listOfModels.addAll(nextPageModels);


                    // Remove the load button if you have all the records.
                    if (nextPageModels.size() < SPManager.ListPageSize) {
                        listView.removeFooterView(btnLoadMore);
                    }

                    // get listview current position - used to maintain scroll position
                    int currentPosition = listView.getFirstVisiblePosition();

                    // Appending new data to menuItems ArrayList
                    adapter = new MyModelListViewAdapter(
                            MyModelActivity.this);
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
