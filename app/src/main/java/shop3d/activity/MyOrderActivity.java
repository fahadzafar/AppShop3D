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
import com.stripe.model.Charge;

import java.util.ArrayList;
import java.util.HashMap;

import activity.shop3d.org.shop3d.R;
import shop3d.adapters.MyOrderListViewAdapter;
import shop3d.parse.ParseOperation;
import shop3d.util.Helper;
import shop3d.util.OrderParser;
import shop3d.util.SPManager;

public class MyOrderActivity extends AppCompatActivity {

    static ListView listView;
    public Button btnLoadMore;

    private MyOrderListViewAdapter adapter;

    public static ArrayList<ParseObject> listOfModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorder);

        listView = (ListView) findViewById(R.id.myorder_listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
/*
                ImageCache.RemoveAllExceptIcon0();
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SingleModelView.class);
                in.putExtra(ATAGS.PASSVALUE_SELECTED_INDEX, position);
                in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, ATAGS.TAG_ACTIVITY_FAVORITES);
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
        ArrayList<ParseObject> allModels = ParseOperation.GetAllOrders(curPage);
        listOfModels.clear();
        listView.removeFooterView(btnLoadMore);

        listOfModels.addAll(allModels);

        // Adding button to listview at footer
        if (listOfModels.size() < SPManager.ListPageSize) {
            listView.removeFooterView(btnLoadMore);
        } else
            listView.addFooterView(btnLoadMore);
        adapter = new MyOrderListViewAdapter(this);

        listView.setAdapter(adapter);
        listView.requestLayout();
    }


    ProgressDialog pDialog;

    // Flag for current page
    int current_page = 0;

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(
                    MyOrderActivity.this);
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

                    ArrayList<ParseObject> nextPageModels = ParseOperation.GetAllOrders(current_page);
                    MyOrderActivity.listOfModels.addAll(nextPageModels);


                    // Remove the load button if you have all the records.
                    if (nextPageModels.size() < SPManager.ListPageSize) {
                        listView.removeFooterView(btnLoadMore);
                    }

                    // get listview current position - used to maintain scroll position
                    int currentPosition = listView.getFirstVisiblePosition();

                    // Appending new data to menuItems ArrayList
                    adapter = new MyOrderListViewAdapter(
                            MyOrderActivity.this);
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


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


    }
}
