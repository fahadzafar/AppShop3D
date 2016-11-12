package shop3d.activity.ImageDisplay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseObject;

import shop3d.activity.AccountActivity;
import shop3d.activity.CheckoutActivity;
import shop3d.activity.FullScreenImageActivity;
import shop3d.activity.login.FragmentLoginDialogue;
import shop3d.activity.shipping.FragmentShippingDialogue;
import shop3d.parse.ParseOperation;
import shop3d.parse.ParseOperation_RegisterLogin;
import shop3d.util.ATAGS;
import shop3d.util.Helper;
import shop3d.util.SPManager;

import activity.shop3d.org.shop3d.R;


/**
 * A simple counterpart for tab1 layout...
 */
public class FragmentDisplayDialogue_1 extends Fragment {


    int hiw[] = {
            R.drawable.hiw_0,
            R.drawable.hiw_1,
            R.drawable.hiw_2,
            R.drawable.hiw_3,
            R.drawable.hiw_4,
            R.drawable.hiw_5,
            R.drawable.hiw_6,
            R.drawable.hiw_7,
            R.drawable.hiw_8,
            R.drawable.cc_0,
            R.drawable.cc_1,
            R.drawable.cc_2,
            R.drawable.cc_3,
            R.drawable.cc_4,
    };

    int cc[] = {
            R.drawable.hiw_1,
            R.drawable.hiw_2,
            R.drawable.hiw_3,
            R.drawable.hiw_4,
            R.drawable.hiw_5,
            R.drawable.hiw_6,
            R.drawable.hiw_7,
            R.drawable.hiw_8,
            R.drawable.cc_0,
            R.drawable.cc_1,
            R.drawable.cc_2,
            R.drawable.cc_3,
            R.drawable.cc_4,

    };


    int currIndex;
    Button BtnLeft, BtnRight, BtnClose;
    ImageView iv;
    Context con;
    public static Bitmap bmVisible;

    int arr[] = null;


    TextView tvTapToView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        switch (FragmentDisplayDialogue.SELECTEDARRAY) {
            case 0: // How it works
                arr = hiw;
                break;
            case 1: // Color chart
                arr = cc;
                break;
        }

        currIndex = 0;
        View view = inflater.inflate(R.layout.fragment_display_dialogue_1, container, false);
        BtnLeft = (Button) view.findViewById(R.id.frag_display_BtnLeft);
        BtnRight = (Button) view.findViewById(R.id.frag_display_BtnRight);
        BtnClose = (Button) view.findViewById(R.id.frag_display_close);
        tvTapToView = (TextView) view.findViewById(R.id.frag_tv_tap_to_zoom);
        iv = (ImageView) view.findViewById(R.id.frag_display_itemIcon);
        con = view.getContext();


        BtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDisplayDialogue.dialog.dismiss();
            }
        });

        changeBtnImgIndex(0);
        BtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBtnImgIndex(-1);
            }
        });

        BtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBtnImgIndex(1);
            }
        });

        if (FragmentDisplayDialogue.TapTozoom) {
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent in = new Intent(con,
                            FullScreenImageActivity.class);
                    in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, ATAGS.TAG_ACTIVITY_FRAGMENT_DISPLAY);

                    startActivity(in);
                }
            });
            tvTapToView.setVisibility(View.VISIBLE);
        } else {
            tvTapToView.setVisibility(View.GONE);
        }


        return view;
    }


    void changeBtnImgIndex(int difference) {

        int newIndex = currIndex + difference;
        if (newIndex >= 0 &&
                newIndex < arr.length) {
            bmVisible = BitmapFactory.decodeResource(getResources(), arr[newIndex]);
            iv.setImageBitmap(bmVisible);
            currIndex = newIndex;
        }

    }
}