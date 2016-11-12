package shop3d.activity.ImageDisplay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import shop3d.util.Helper;

import activity.shop3d.org.shop3d.R;

/**
 * Fragment dialog displaying tab host...
 */
public class FragmentDisplayDialogue extends DialogFragment {
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    public static FragmentManager FM;
    public static String CallerActivity;
    public static int SELECTEDARRAY = 0;
    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------
    public static Dialog dialog;
    public static boolean TapTozoom = true;
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        dialog = super.onCreateDialog(savedInstanceState);
        FM = getChildFragmentManager();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    public static void DisplayMessage(final String title, final String message, final Context con) {
        ((Activity) con).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Helper.ShowDialogue(title, message, con.getApplicationContext());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_dialogue, container);

        // tab slider
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        return view;
    }


    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    /**
     * Used for tab paging...
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                // find first fragment...
                FragmentDisplayDialogue_1 ft1 = new FragmentDisplayDialogue_1();
                return ft1;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "How it Works";
            }
            return null;
        }
    }
}