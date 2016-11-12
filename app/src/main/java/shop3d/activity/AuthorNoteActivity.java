package shop3d.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.shipping.FragmentShippingDialogue;
import shop3d.adapters.AccountListViewAdapter;
import shop3d.adapters.AuthorNotesListViewAdapter;
import shop3d.parse.ParseOperation;
import shop3d.util.Printable;

public class AuthorNoteActivity extends AppCompatActivity {

    static ListView listView;
    private AuthorNotesListViewAdapter adapter;

    public static ArrayList<ParseObject> listOfModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authornotes);



        listView = (ListView) findViewById(R.id.account_listViewAuthorMsg);

        adapter = new AuthorNotesListViewAdapter(this);
        listOfModels = new ArrayList<ParseObject>();
        listView.setAdapter(adapter);
        RefreshModelListFirstTime();

    }

    public static void SetAuthorNotes(){
        ArrayList<ParseObject> allNotes =
                ParseOperation.GetAllAuthorNotes();
        listOfModels.clear();
        listOfModels.addAll(allNotes);
    }
    public static void RefreshModelListFirstTime() {
        SetAuthorNotes();
        RefreshListView();
    }

    public static void RefreshListView() {
        listView.invalidateViews();
        listView.requestLayout();
    }

}
