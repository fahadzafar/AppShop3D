package shop3d.beans;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Fahad on 1/22/2016.
 */
public class SMVSharedModel {

    public ParseObject ModelParent;
    public ParseObject ModelChild;
    public int TotalIcons;
    public boolean HasParent = false;

    public SMVSharedModel(ParseObject mP, ParseObject mC, int total){
        ModelParent = mP;
        ModelChild = mC;
        TotalIcons = total;
        HasParent = true;
    }
    public SMVSharedModel(ParseObject mP){
        ModelParent = ModelChild = mP;
        TotalIcons = ModelParent.getInt("total_icons");
        HasParent = false;
    }
    public String GetFavoriteClassName(){
        if (HasParent)
            return ModelParent.getClassName();
        else
            return ModelChild.getClassName();
    }
}
