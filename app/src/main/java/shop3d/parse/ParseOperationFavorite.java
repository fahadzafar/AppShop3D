package shop3d.parse;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import shop3d.util.Printable;

/**
 * Created by Fahad on 12/23/2015.
 */
public class ParseOperationFavorite {

    public static ParseObject GetUserMadeModel(String id) {
        ParseObject returned = null;
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Model_UserMade");
            query.whereEqualTo("objectId", id);

            returned = query.getFirst();

        } catch (Exception er) {
            int y = 0;
        }
        return returned;
    }

    public static ParseObject GetOriginalModel(String id) {
        ParseObject returned = null;
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Model");
            query.whereEqualTo("objectId", id);

            returned = query.getFirst();
            return returned;
        } catch (Exception er) {
            int y = 0;
        }
        return returned;
    }
}
