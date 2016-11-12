package shop3d.ambilwarna;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.DetailedViewModelActivity;

public class AmbilWarnaDialog {
    public interface OnAmbilWarnaListener {
        void onCancel(AmbilWarnaDialog dialog);

        void onOk(AmbilWarnaDialog dialog, int color);
    }

    final AlertDialog dialog;
    private final boolean supportsAlpha;
    final OnAmbilWarnaListener listener;
    final View viewHue;
    final AmbilWarnaSquare viewSatVal;
    final ImageView viewCursor;
    final ImageView viewAlphaCursor;
    final View viewOldColor;
    final View viewNewColor;
    final View viewAlphaOverlay;
    final ImageView viewTarget;
    final ImageView viewAlphaCheckered;
    final ViewGroup viewContainer;
    final float[] currentColorHsv = new float[3];
    int alpha;

    static int prevIndex = 0;
    final View viewPrev0, viewPrev1, viewPrev2, viewPrev3, viewPrev4, viewPrev5;
    static ArrayList<Integer> prevColors = null;

    /**
     * Create an AmbilWarnaDialog.
     *
     * @param context  activity context
     * @param color    current color
     * @param listener an OnAmbilWarnaListener, allowing you to get back error or OK
     */
    public static boolean isBackgroundColor = false;

    public AmbilWarnaDialog(final Context context, int color, OnAmbilWarnaListener listener) {
        this(context, color, false, listener);

    }



    public void SetAllPrevColor(int color) {
        viewPrev0.setBackgroundColor(color);
        viewPrev1.setBackgroundColor(color);
        viewPrev2.setBackgroundColor(color);
        viewPrev3.setBackgroundColor(color);
        viewPrev4.setBackgroundColor(color);
        viewPrev5.setBackgroundColor(color);

    }

    int fixColorAlpha(final int argb) {
        int finalColor = 0xff;
        finalColor = finalColor << 24 | (argb & 0x00ffffff);
        return finalColor;
    }

    public void addColorListener(View thisView) {
        thisView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int finalColor = fixColorAlpha(((ColorDrawable) view.getBackground()).getColor());
                viewNewColor.setBackgroundColor(finalColor);

                if (isBackgroundColor == false)
                    DetailedViewModelActivity.com.MakeAndSetTexture(finalColor);
                else
                    DetailedViewModelActivity.SetBackColor(finalColor);
            }
        });
    }

    public void SetPrevColor(int color, int index) {
        switch (index) {

            case 0:
                viewPrev0.setBackgroundColor(color);
                break;
            case 1:
                viewPrev1.setBackgroundColor(color);
                break;
            case 2:
                viewPrev2.setBackgroundColor(color);
                break;
            case 3:
                viewPrev3.setBackgroundColor(color);
                break;
            case 4:
                viewPrev4.setBackgroundColor(color);
                break;
            case 5:
                viewPrev5.setBackgroundColor(color);
                break;
        }
    }

    public AmbilWarnaDialog(final Context context, int color, boolean supportsAlpha, OnAmbilWarnaListener listener) {
         this.supportsAlpha = supportsAlpha;
        this.listener = listener;
        if (!supportsAlpha) { // remove alpha if not supported
            color = color | 0xff000000;
        }

        Color.colorToHSV(color, currentColorHsv);
        alpha = Color.alpha(color);

        final View view = LayoutInflater.from(context).inflate(R.layout.ambilwarna_dialog, null);

        viewHue = view.findViewById(R.id.ambilwarna_viewHue);
        viewSatVal = (AmbilWarnaSquare) view.findViewById(R.id.ambilwarna_viewSatBri);
        viewCursor = (ImageView) view.findViewById(R.id.ambilwarna_cursor);
        viewOldColor = view.findViewById(R.id.ambilwarna_oldColor);
        viewNewColor = view.findViewById(R.id.ambilwarna_newColor);
        viewTarget = (ImageView) view.findViewById(R.id.ambilwarna_target);
        viewContainer = (ViewGroup) view.findViewById(R.id.ambilwarna_viewContainer);
        viewAlphaOverlay = view.findViewById(R.id.ambilwarna_overlay);
        viewAlphaCursor = (ImageView) view.findViewById(R.id.ambilwarna_alphaCursor);
        viewAlphaCheckered = (ImageView) view.findViewById(R.id.ambilwarna_alphaCheckered);

        if (prevColors == null)
            prevColors = new ArrayList<Integer>();
        viewPrev0 = view.findViewById(R.id.ambilwarna_prev0);
        viewPrev1 = view.findViewById(R.id.ambilwarna_prev1);
        viewPrev2 = view.findViewById(R.id.ambilwarna_prev2);
        viewPrev3 = view.findViewById(R.id.ambilwarna_prev3);
        viewPrev4 = view.findViewById(R.id.ambilwarna_prev4);
        viewPrev5 = view.findViewById(R.id.ambilwarna_prev5);

        addColorListener(viewPrev0);
        addColorListener(viewPrev1);
        addColorListener(viewPrev2);
        addColorListener(viewPrev3);
        addColorListener(viewPrev4);
        addColorListener(viewPrev5);

// ARGB
        //  SetAllPrevColor(0xffff0000);

        { // hide/show alpha
            viewAlphaOverlay.setVisibility(supportsAlpha ? View.VISIBLE : View.GONE);
            viewAlphaCursor.setVisibility(supportsAlpha ? View.VISIBLE : View.GONE);
            viewAlphaCheckered.setVisibility(supportsAlpha ? View.VISIBLE : View.GONE);
        }

        viewSatVal.setHue(getHue());
        if (prevColors != null) {
            if (prevColors.size() != 0) {
                viewOldColor.setBackgroundColor(prevColors.get(0));
            }
        } else {
            viewOldColor.setBackgroundColor(color);
        }

        viewNewColor.setBackgroundColor(color);

        // Update all prev Colors
        int i = 0;
        if (prevColors.size() > 6) {
            ArrayList<Integer> prunedList = new ArrayList<Integer>();
            for (i = 0; i < 6; i++) {
                prunedList.add(0, prevColors.get(6 - i - 1));
            }
            prevColors = prunedList;
        }

        for (i = 0; i < prevColors.size(); i++) {
            SetPrevColor(prevColors.get(i), i);
        }
        for (; i < 6; i++) {
            SetPrevColor(0xff000000, i);
        }


        viewHue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > viewHue.getMeasuredHeight()) {
                        y = viewHue.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
                    }
                    float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
                    if (hue == 360.f) hue = 0.f;
                    setHue(hue);

                    // update view
                    viewSatVal.setHue(getHue());
                    moveCursor();
                    viewNewColor.setBackgroundColor(getColor());
                    if (isBackgroundColor == false)
                        DetailedViewModelActivity.com.MakeAndSetTexture(getColor());
                    else
                        DetailedViewModelActivity.SetBackColor(getColor());
                    updateAlphaView();


                    return true;
                }
                return false;
            }
        });

        if (supportsAlpha) viewAlphaCheckered.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_MOVE)
                        || (event.getAction() == MotionEvent.ACTION_DOWN)
                        || (event.getAction() == MotionEvent.ACTION_UP)) {

                    float y = event.getY();
                    if (y < 0.f) {
                        y = 0.f;
                    }
                    if (y > viewAlphaCheckered.getMeasuredHeight()) {
                        y = viewAlphaCheckered.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
                    }
                    final int a = Math.round(255.f - ((255.f / viewAlphaCheckered.getMeasuredHeight()) * y));
                    AmbilWarnaDialog.this.setAlpha(a);

                    // update view
                    moveAlphaCursor();
                    int col = AmbilWarnaDialog.this.getColor();
                    int c = a << 24 | col & 0x00ffffff;
                    viewNewColor.setBackgroundColor(c);
                    return true;
                }
                return false;
            }
        });
        viewSatVal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float x = event.getX(); // touch event are in dp units.
                    float y = event.getY();

                    if (x < 0.f) x = 0.f;
                    if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
                    if (y < 0.f) y = 0.f;
                    if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

                    setSat(1.f / viewSatVal.getMeasuredWidth() * x);
                    setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

                    // update view
                    moveTarget();

                    viewNewColor.setBackgroundColor(getColor());

                    if (isBackgroundColor == false)
                        DetailedViewModelActivity.com.MakeAndSetTexture(getColor());
                    else
                        DetailedViewModelActivity.SetBackColor(getColor());
                    return true;
                }
                return false;
            }
        });


        dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.NewDialog))



                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (AmbilWarnaDialog.this.listener != null) {
                            int finalColor = fixColorAlpha(((ColorDrawable) viewNewColor.getBackground()).getColor());
                            AmbilWarnaDialog.this.listener.onOk(AmbilWarnaDialog.this, finalColor);
                            prevColors.add(0, finalColor);
                            prevIndex++;

                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (AmbilWarnaDialog.this.listener != null) {
                            AmbilWarnaDialog.this.listener.onCancel(AmbilWarnaDialog.this);
                        }
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    // if back button is used, call back our listener.
                    @Override
                    public void onCancel(DialogInterface paramDialogInterface) {
                        if (AmbilWarnaDialog.this.listener != null) {
                            AmbilWarnaDialog.this.listener.onCancel(AmbilWarnaDialog.this);
                        }

                    }
                })

                .create();


        // kill all padding from the dialog window
        dialog.setView(view, 0, 0, 0, 0);


        // move cursor & target on first draw
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                moveCursor();
                if (AmbilWarnaDialog.this.supportsAlpha) moveAlphaCursor();
                moveTarget();
                if (AmbilWarnaDialog.this.supportsAlpha) updateAlphaView();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    protected void moveCursor() {
        float y = viewHue.getMeasuredHeight() - (getHue() * viewHue.getMeasuredHeight() / 360.f);
        if (y == viewHue.getMeasuredHeight()) y = 0.f;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (viewHue.getLeft() - Math.floor(viewCursor.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewHue.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        viewCursor.setLayoutParams(layoutParams);
    }

    protected void moveTarget() {
        float x = getSat() * viewSatVal.getMeasuredWidth();
        float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
        layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        viewTarget.setLayoutParams(layoutParams);
    }

    protected void moveAlphaCursor() {
        final int measuredHeight = this.viewAlphaCheckered.getMeasuredHeight();
        float y = measuredHeight - ((this.getAlpha() * measuredHeight) / 255.f);
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.viewAlphaCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (this.viewAlphaCheckered.getLeft() - Math.floor(this.viewAlphaCursor.getMeasuredWidth() / 2) - this.viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) ((this.viewAlphaCheckered.getTop() + y) - Math.floor(this.viewAlphaCursor.getMeasuredHeight() / 2) - this.viewContainer.getPaddingTop());

        this.viewAlphaCursor.setLayoutParams(layoutParams);
    }

    private int getColor() {
        final int argb = Color.HSVToColor(currentColorHsv);
        return alpha << 24 | (argb & 0x00ffffff);
    }

    private float getHue() {
        return currentColorHsv[0];
    }

    private float getAlpha() {
        return this.alpha;
    }

    private float getSat() {
        return currentColorHsv[1];
    }

    private float getVal() {
        return currentColorHsv[2];
    }

    private void setHue(float hue) {
        currentColorHsv[0] = hue;
    }

    private void setSat(float sat) {
        currentColorHsv[1] = sat;
    }

    private void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    private void setVal(float val) {
        currentColorHsv[2] = val;
    }

    public void show() {
        dialog.show();
        /// coded by murtaza


        LinearLayout.LayoutParams layout_param=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_param.setMargins(0,0,5,0);

        Button positive_btn=dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive_btn.setTextColor(Color.WHITE);
        positive_btn.setTextSize(12);
        positive_btn.setBackgroundResource(R.drawable.background_btn_state);
        positive_btn.setLayoutParams(layout_param);

        Button negative_btn=dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negative_btn.setTextColor(Color.WHITE);
        negative_btn.setTextSize(12);
        negative_btn.setBackgroundResource(R.drawable.background_btn_state);
        negative_btn.setLayoutParams(layout_param);
// code end by murtaza
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    private void updateAlphaView() {
        final GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                Color.HSVToColor(currentColorHsv), 0x0
        });
        viewAlphaOverlay.setBackgroundDrawable(gd);
    }
}
