package shop3d.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.SaveCallback;

import activity.shop3d.org.shop3d.R;
import shop3d.util.Helper;
import shop3d.util.SPManager;

public class FeedbackActivity extends AppCompatActivity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        Button bn = (Button) findViewById(R.id.about_btn_back);
        bn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SubmitUserSuggestion();
            }
        });

        bn = (Button) findViewById(R.id.about_btn_userRecommendedCharacter);
        bn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SubmitUserCharacter();
            }
        });
    }

    // Submit the user suggestion/comments/feedback.
    public void SubmitUserSuggestion() {

        EditText ev = (EditText) findViewById(R.id.feedback_userdata);
        String data = ev.getText().toString();
        if (!data.equals("")) {
            ParseObject suggestion = new ParseObject("Feedback");
            suggestion.put("userId", SPManager.current_user);
            suggestion.put("feedbackText", data);

            suggestion.saveInBackground(new SaveCallback() {
                public void done(com.parse.ParseException e) {
                    Helper.ShowDialogue("Feedback submitted,", " Thanks",
                            getApplicationContext());
                }
            });
        }
        this.finish();
    }

    public void SubmitUserCharacter() {

        EditText evName = (EditText) findViewById(R.id.feedback_userCharName);
        EditText evLore = (EditText) findViewById(R.id.feedback_userCharLore);

        String dataLore = evLore.getText().toString();
        String dataName = evName.getText().toString();
        if (dataLore.equals("") != true && dataName.equals("") != true) {
            ParseObject suggestion = new ParseObject("NewCharacters");
            suggestion.put("userId", SPManager.current_user);
            suggestion.put("title", dataName);
            suggestion.put("description", dataLore);

            suggestion.saveInBackground(new SaveCallback() {
                public void done(com.parse.ParseException e) {
                    Helper.ShowDialogue("Character suggestion accepted,", " Thanks",
                            getApplicationContext());
                    finish();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Suggest a Name and Lore for a character, Are you Simon ?",
                    Toast.LENGTH_LONG).show();

        }
    }
}
