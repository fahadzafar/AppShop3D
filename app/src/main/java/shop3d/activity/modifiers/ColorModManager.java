package shop3d.activity.modifiers;

import com.parse.Parse;
import com.parse.ParseObject;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

import java.util.ArrayList;

import shop3d.util.ATAGS;
import shop3d.util.ColorHelper;
import shop3d.util.Helper;

/**
 * Created by Fahad on 1/5/2016.
 */
public class ColorModManager {
    // Name of JPCT textures for each color.
    public ArrayList<String> MFlatColorTextureNames;
    // Contains the list of flat colors of the model.
    public ArrayList<String> MFlatColorDisplayNames;
    // Contains the Color Values of the materials.
    public ArrayList<Integer> MFlatColorValues_Default;
    public ArrayList<Integer> MFlatColorValues_Current;

    public int MaterialCount = 0;

    // Since the current color changes on touch-move, this is the color if the person cancels.
    public int RevertColor = 0;


    // The dunamic buttons generated based on the materials have IDs 0 - x,
    // This number stores the current one that is selected, before opening the
    // color dialogue. This reverts to the currently selected color for that
    // mesh-part if the user presses cancel.
    public int UserSelectedMIndex = -1;

    // The "Model" that is being diaplyed
    ParseObject ActualModel = null;

    int TotalModifierCount;

    public ColorModManager(ParseObject iActual) {
        MFlatColorTextureNames = new ArrayList<String>();
        MFlatColorDisplayNames = new ArrayList<String>();
        MFlatColorValues_Default = new ArrayList<Integer>();
        MFlatColorValues_Current = new ArrayList<Integer>();

        if (iActual.getClassName().equals(ATAGS.TABLE_PARSE_MODEL)) {
            ActualModel = iActual;
        }
    }


    public void Init() {
        // Get all the flat materials, their texture and display names.
        int flatModifierCount = ActualModel.getInt("mtl_flat_count");
        MFlatColorDisplayNames = Helper.SplitAndGetArrayList(ActualModel.getString("mtl_flat_name_display"));
        MFlatColorTextureNames = Helper.SplitAndGetArrayList(ActualModel.getString("mtl_flat_name_texture"));

        /// The data is stored as strings, it must be converted to an integer array.
        MFlatColorValues_Default = ConvertStringColorToIntgerArrList(
                Helper.SplitAndGetArrayList(ActualModel.getString("mtl_flat_colors")));
        MFlatColorValues_Current = MFlatColorValues_Default;

        // This is the order by which everything IS ACTUALLY ARRANGED


        MaterialCount = MFlatColorDisplayNames.size();


    }

    public void ResetObjectTextures() {
        for (int i = 0; i < MFlatColorTextureNames.size(); i++) {
            TextureManager tm = TextureManager.getInstance();
            Texture tex1 = new Texture(Helper.GetColorBitmap(MFlatColorValues_Default.get(i), 16, 16));
            tm.replaceTexture(MFlatColorTextureNames.get(i),
                    tex1);
        }
        MFlatColorValues_Current = MFlatColorValues_Default;
    }

    public void ResetObjectTexturesFromChild(ParseObject child) {
        MFlatColorValues_Default = ConvertStringColorToIntgerArrList(
                Helper.SplitAndGetArrayList(child.getString("mtl_color")));
        MFlatColorValues_Current = MFlatColorValues_Default;

    }

    public void MakeAndSetTexture(int newColor) {
        // Set the current color array for this particular material
        // to the selected color, for when the user wants to save the model,
        // This value can be used.
        MFlatColorValues_Current.set(UserSelectedMIndex, newColor);

        TextureManager tm = TextureManager.getInstance();
        Texture tex1 = new Texture(Helper.GetColorBitmap(newColor, 16, 16));
        tm.replaceTexture(MFlatColorTextureNames.get(UserSelectedMIndex),
                tex1);
    }

    // Set the current material color as the revert color because touch changes the selected
    // color constantly.
    public void SetCurrentColAsRevertColor() {
        RevertColor = GetSelectedMColor();
    }


    public int GetSelectedMColor() {
        return MFlatColorValues_Current.get(UserSelectedMIndex);
    }


    public String GetCurrentColorsForSaving() {
        return ColorHelper.IntArrToString(MFlatColorValues_Current);
    }


    public static ArrayList<Integer> ConvertStringColorToIntgerArrList(ArrayList<String> data) {
        ArrayList<Integer> allCols = new ArrayList<Integer>();
        // Add the colors into the holder arrays.
        for (int i = 0; i < data.size(); i++) {
            allCols.add(ColorHelper.StringToColorInt(data.get(i)));
        }
        return allCols;
    }

    public static ArrayList<Integer> ConvertStringToIntgerArrList(ArrayList<String> data) {
        ArrayList<Integer> allCols = new ArrayList<Integer>();
        // Add the colors into the holder arrays.
        for (int i = 0; i < data.size(); i++) {
            allCols.add(Integer.parseInt(data.get(i)));
        }
        return allCols;
    }


}
