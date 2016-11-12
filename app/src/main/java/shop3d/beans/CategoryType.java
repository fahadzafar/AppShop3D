package shop3d.beans;

import com.parse.ParseObject;

/**
 * Created by Fahad on 11/11/2015.
 */
public class CategoryType {

    public String Id;
    public String Name;
    public int Count;
    public ParseObject PCatObject;

    public CategoryType(ParseObject cat) {
        PCatObject = cat;
        Id = cat.getObjectId();
        Name = cat.getString("name");
        Count = cat.getInt("model_count");
    }

    public CategoryType(String id, String name, int count) {
        PCatObject = null;
        Id = id;
        Name = name;
        Count = count;
    }
}
