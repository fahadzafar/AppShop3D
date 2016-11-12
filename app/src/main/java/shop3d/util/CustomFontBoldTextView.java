package shop3d.util;

/**
 * Created by Murtaza on 1/5/16.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontBoldTextView extends TextView{






        public CustomFontBoldTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        public CustomFontBoldTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public CustomFontBoldTextView(Context context) {
            super(context);
            init();
        }

        private void init() {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "Raleway_Bold.ttf");
            setTypeface(tf);
        }


}
