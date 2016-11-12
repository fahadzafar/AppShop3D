package shop3d.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.stripe.model.Charge;
import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.GLSLShader;
import com.threed.jpct.IRenderHook;
import com.threed.jpct.ITextureEffect;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.ImageDisplay.FragmentDisplayDialogue;
import shop3d.activity.login.FragmentLoginDialogue;
import shop3d.activity.modifiers.ColorModManager;
import shop3d.ambilwarna.AmbilWarnaDialog;
import shop3d.beans.SMVSharedModel;
import shop3d.parse.ParseOperation;
import shop3d.util.ColorHelper;
import shop3d.util.Helper;
import shop3d.util.MathString;
import shop3d.util.SPManager;

/**
 * @author EgonOlsen
 */
public class DetailedViewModelActivity extends AppCompatActivity implements OnScaleGestureListener, IRenderHook, View.OnClickListener {

    public static boolean RESETACTIVITY = false;

    // Enum for tracking the state of model screen capture.
    public enum ScreenCaptureState {
        UNUSED, CAPTURE_EXIT_1, CAPTURE_ONLY_1,
    }

    ScreenCaptureState CaptureImage = ScreenCaptureState.UNUSED;
    int captureCounter = 0;
    final int captureCounterLimit = 100;


    // Camera movement speed for all 4 directions, left right up down
    int camSpeed = 5;

    // This is the selection index of the material that is being modified by the user.
    // The color dialogue uses it to change texture on scroll of the color hue.
    //public static int flatSelectedModifierIndex = -1;

    // Button btnCamUp, btnCamDown, btnCamLeft, btnCamRight;
    LinearLayout btnCamUp, btnCamDown, btnCamLeft, btnCamRight;


    // Used to handle pause and resume...
    private static DetailedViewModelActivity master = null;

    private ScaleGestureDetector gestureDec = null;

    private GLSurfaceView mGLView;
    private MyRenderer renderer = null;
    private FrameBuffer fb = null;
    private World world = null;
    public static RGBColor back = new RGBColor(50, 50, 100);
    public RGBColor PrevBackColor;

    public static void SetBackColor(int color) {
        back.setTo(Helper.getRColor(color), Helper.getGColor(color), Helper.getBColor(color), 0xff);
    }


    Camera cam;
    private float touchTurn = 0;
    private float touchTurnUp = 0;

    private float xpos = -1;
    private float ypos = -1;

    private Texture font = null;

    public static Object3D thing = null;

    //private Object3D plane;
    private Light light;

    public static ParseObject actualObject;

    private GLSLShader shader = null;

    // public static float scale = 0.05f;
    public static float thingScale = 1;

    //public static ArrayList<String> flatModTextureNames;
    //ArrayList<String> flatModDisplayNames;
    //int lastSelectedColor = 0xffffff;


    // Stores the original model skin color, for a reset anytime
    //  ArrayList<Integer> colorOriginal;

    // Stores the user selected colors, for when the user wants to save
    // the model.
    //  ArrayList<Integer> colorCurrent;

    // -------------- Buttons for the extra right side options

    Button btnBackgroundColor, btnShare, btnSaveModel, btnCancel, btnReset, btnColorChart;
    Button btn_D_GETSET_RTS, btn_D_MAKE_VISIBLE, btn_D_EXTRA;
    CheckBox btnOnOffModifier, btnOnOffCam;
    int BotLayoutVisibility = 0;
    // ------------------------------------

    ParseObject userMadeModel;


    LinearLayout loModifier;
    LinearLayout ll_Camera_ExtraOptions;
    ProgressDialog ringProgressDialog;

    public void LayoutButtonEnableStatus(boolean status, boolean launchProgressDialogue) {


        loModifier.setEnabled(status);
        ll_Camera_ExtraOptions.setEnabled(status);
        btnOnOffModifier.setEnabled(status);
        btnOnOffCam.setEnabled(status);
/*
        if (launchProgressDialogue) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ringProgressDialog = ProgressDialog.show(DetailedViewModelActivity.this, "Please wait ...",	"Downloading Model ...", true);
                    ringProgressDialog.setCancelable(true);
                    ringProgressDialog.show();

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                }
            }.execute();

        } else {
            ringProgressDialog.dismiss();
        }*/

    }

    public void findAttachModButtons() {
        // Find the layout where the buttons should be added
        loModifier = (LinearLayout) findViewById(R.id.dmv_modifier_layout);

        for (int i = 0; i < com.MaterialCount; i++) {
            Button b1 = new Button(this);
            b1.setId(i);
            b1.setOnClickListener(this);

            loModifier.addView(b1);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            param.gravity = Gravity.CENTER;
            param.topMargin = 16;

            b1.setLayoutParams(param);

            // b1.setPadding(16, 0, 16, 0);

            b1.setTextColor(Color.WHITE);
            b1.setBackgroundResource(R.drawable.ic_normal_btn_bg);
            b1.setGravity(Gravity.CENTER);
            b1.setText(com.MFlatColorDisplayNames.get(i));
        }
// Now attach the camera buttons
        btnCamUp = (LinearLayout) findViewById(R.id.dmv_btn_camera_up);
        btnCamDown = (LinearLayout) findViewById(R.id.dmv_btn_camera_down);
        btnCamLeft = (LinearLayout) findViewById(R.id.dmv_btn_camera_left);
        btnCamRight = (LinearLayout) findViewById(R.id.dmv_btn_camera_right);

        btnCamUp.setOnClickListener(this);
        btnCamLeft.setOnClickListener(this);
        btnCamRight.setOnClickListener(this);
        btnCamDown.setOnClickListener(this);


        btnColorChart = (Button) findViewById(R.id.dmv_btn_colorChart);
        btnColorChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentDisplayDialogue.TapTozoom = false;
                FragmentDisplayDialogue.SELECTEDARRAY = 1;
                FragmentDisplayDialogue overlay = new FragmentDisplayDialogue();
                overlay.show(fm, "Color Chart");
            }
        });

        btnReset = (Button) findViewById(R.id.dmv_btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the are you sure dialogue
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                com.Init();
                                if (SPManager.PassToSMV.HasParent == true) {
                                    com.ResetObjectTexturesFromChild(SPManager.PassToSMV.ModelChild);
                                }
                                com.ResetObjectTextures();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailedViewModelActivity.this);
                builder.setMessage("Are you sure you want to reset colors to default ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });

        btn_D_MAKE_VISIBLE = (Button) findViewById(R.id.dmv_btn_d_DEBUG_MAKE_VISIBLE);
        btn_D_EXTRA = (Button) findViewById(R.id.dmv_btn_d_DEBUG_EXTRA);

        btn_D_GETSET_RTS = (Button) findViewById(R.id.dmv_btn_d_SET_RTS);

        if (SPManager.IsPowerUser()) {
            btn_D_GETSET_RTS.setVisibility(View.VISIBLE);
            btn_D_MAKE_VISIBLE.setVisibility(View.VISIBLE);
            btn_D_EXTRA.setVisibility(View.VISIBLE);

            btn_D_EXTRA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actualObject.put("visible", false);
                    try {
                        actualObject.save();
                        Helper.ShowDialogue("Success: ", " Model is live now", getApplicationContext());
                    } catch (ParseException e) {
                        Helper.ShowDialogue("Error: ", e.getMessage(), getApplicationContext());
                    }

                }

            });
            btn_D_MAKE_VISIBLE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actualObject.put("visible", true);
                    try {
                        actualObject.save();
                        Helper.ShowDialogue("Success: ", " Model is live now", getApplicationContext());
                    } catch (ParseException e) {
                        Helper.ShowDialogue("Error: ", e.getMessage(), getApplicationContext());
                    }
                }

            });


            btn_D_GETSET_RTS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Save the model rotation/translation here.
                    actualObject.put("visible", true);
                    actualObject.put("model_rot", MathString.MatToString(thing.getRotationMatrix()));
                    actualObject.put("model_trans", MathString.MatToString(thing.getRotationMatrix()).toString());
                    actualObject.put("model_scale", thing.getScale() + "");


                    actualObject.put("cam_dir_vec", MathString.VecToString(cam.getDirection()));
                    actualObject.put("cam_up_vec", MathString.VecToString(cam.getUpVector()));
                    actualObject.put("cam_pos_vec", MathString.VecToString(cam.getPosition()));

                    SimpleVector colBk = new SimpleVector(back.getRed(), back.getGreen(), back.getBlue());
                    actualObject.put("bk_color_vec", MathString.VecToString(colBk));

                    try {
                        actualObject.save();
                        Helper.ShowDialogue("Success: ", " Saved", getApplicationContext());
                    } catch (ParseException e) {
                        Helper.ShowDialogue("Error: ", e.getMessage(), getApplicationContext());

                    }
                }

            });

        }


        // Modifier values, sho / hide.
        btnOnOffModifier = (CheckBox) findViewById(R.id.dmv_btn_on_off_modifiers);
        btnOnOffModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loModifier.getVisibility() == View.VISIBLE) {
                    loModifier.setVisibility(View.GONE);
                } else if (loModifier.getVisibility() == View.GONE) {
                    loModifier.setVisibility(View.VISIBLE);
                }

            }
        });

        // On Off for camera view options.
        ll_Camera_ExtraOptions = (LinearLayout) findViewById(R.id.dmv_camera_layout);
        btnOnOffCam = (CheckBox) findViewById(R.id.dmv_btn_on_off_cam);
        btnOnOffCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_Camera_ExtraOptions.getVisibility() == View.VISIBLE) {
                    BotLayoutVisibility = View.GONE;
                    ll_Camera_ExtraOptions.setVisibility(View.GONE);
                } else if (ll_Camera_ExtraOptions.getVisibility() == View.GONE) {
                    ll_Camera_ExtraOptions.setVisibility(View.VISIBLE);
                    BotLayoutVisibility = View.VISIBLE;
                }

            }
        });


        btnBackgroundColor = (Button) findViewById(R.id.dmv_btn_change_background);
        btnBackgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBackgroundDialog();
            }
        });

        btnShare = (Button) findViewById(R.id.dmv_btn_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RESETACTIVITY = false;
                CaptureImage = ScreenCaptureState.CAPTURE_ONLY_1;


            }
        });

        btnSaveModel = (Button) findViewById(R.id.dmv_btn_save);
        btnSaveModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveModel();

            }
        });

        btnCancel = (Button) findViewById(R.id.dmv_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Show the are you sure dialogue
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailedViewModelActivity.this);
                builder.setMessage("Are you sure you want to exit from here ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });

    }

    void SaveModel() {
        // If new user, he must register first.
        if (SPManager.current_user == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentLoginDialogue overlay = new FragmentLoginDialogue();
            FragmentLoginDialogue.CallerActivity = "DetailedViewModelActivity";
            overlay.show(fm, "register/sign-in");
            return;
        }

        if (SPManager.PassToSMV.HasParent) {
            Helper.ShowDialogue("Note: ", "Will be saved as a new object", getApplicationContext());
        }
        // Display the title dialogue.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New Model Title");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Save Skin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                input.clearFocus();

                final String mText = input.getText().toString();
                if (mText.equals("") == false && mText.length() > 3 && (Helper.CountCharInString(mText, ' ') <= (mText.length() - 3))
                        && (mText.length() < 50)) {

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog = new ProgressDialog(
                                            DetailedViewModelActivity.this);
                                    pDialog.setMessage("Saving, please wait..");
                                    pDialog.setIndeterminate(true);
                                    pDialog.setCancelable(false);
                                    pDialog.show();
                                }
                            });

                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            userMadeModel = new ParseObject("Model_UserMade");
                            userMadeModel.put("title", mText);
                            userMadeModel.put("user_id", SPManager.current_user);
                            userMadeModel.put("original_model_id", actualObject);
                            userMadeModel.put("has_texture", false);
                            userMadeModel.put("visible", true);

                            // Correct the order to original order of saved materials.
                            // com.RestoreToOriginalMaterialOrder();
                            userMadeModel.put("mtl_color", com.GetCurrentColorsForSaving());

                            //   renderer.hasToCreateBuffer = true;
                            // Set the default position.
                            //renderer.SetDefaultModelPosColor(false);

                            // Setting this true will take a screenshot of the model in the onDraw call
                            // Then the activity will exit.
                            CaptureImage = ScreenCaptureState.CAPTURE_EXIT_1;
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.dismiss();
                                    Helper.ShowDialogue("Saving Model Skin,  ", "please wait.", getApplicationContext());
                                }
                            });
                        }
                    }.execute();


                } else {

                    Helper.ShowDialogue("Error: ", "Model name incorrect. Size should be greater than 3 and less than 50 characters.", getApplicationContext());

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }


    Bitmap SavePNG(GL10 gl) {


        int x = 0;
        int y = 0;
        Bitmap captureBitmap;
        int w = mGLView.getWidth();
        int h = mGLView.getHeight();
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (Exception e) {
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);


    }

    SimpleVector getVectorColor(int ARGB) {
        float r = ((ARGB >> 16) & 0xFF);
        float g = ((ARGB >> 8) & 0xFF);
        float b = (ARGB & 0xFF);
        SimpleVector sv = new SimpleVector(r / 255, g / 255, b / 255);
        return sv;
    }

    void FixBottomVisibility() {
        if (BotLayoutVisibility == View.VISIBLE)
            ll_Camera_ExtraOptions.setVisibility(View.VISIBLE);
        else
            ll_Camera_ExtraOptions.setVisibility(View.GONE);
    }

    void openMaterialColorDialog() {

        int lastSelectedColor = com.GetSelectedMColor();
        AmbilWarnaDialog.isBackgroundColor = false;
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(DetailedViewModelActivity.this, lastSelectedColor, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                com.MakeAndSetTexture(color);
                FixBottomVisibility();


            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                com.MakeAndSetTexture(com.RevertColor);
                FixBottomVisibility();
            }
        });

        dialog.getDialog().setCanceledOnTouchOutside(false);
        Window window = dialog.getDialog().getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.getDialog().getWindow().setBackgroundDrawableResource(R.color.translucent_black);
        dialog.getDialog().getWindow().getAttributes().verticalMargin = 5F;

        dialog.show();
    }


    void openBackgroundDialog() {

        AmbilWarnaDialog.isBackgroundColor = true;
        PrevBackColor = back;
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(DetailedViewModelActivity.this, PrevBackColor.getARGB(), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                SetBackColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                back = PrevBackColor;
            }
        });

        dialog.getDialog().getWindow().getAttributes().verticalMargin = 5F;
        dialog.show();
    }

    public static ColorModManager com = null;


    protected void onCreate(Bundle savedInstanceState) {
        Logger.log("onCreate");
        Logger.setLogLevel(Logger.LL_ONLY_ERRORS);
        back = new RGBColor(50, 50, 100);
        if (master != null) {
            copy(master);
        }

        super.onCreate(savedInstanceState);
        //mGLView = new GLSurfaceView(getApplication());


        setContentView(R.layout.activity_detailed_model_view); //or whatever the layout you want to use
        actualObject = SPManager.PassToSMV.ModelParent; // SingleModelView.actualObject;

        com = new ColorModManager(actualObject);
        com.Init();
        if (SPManager.PassToSMV.HasParent == true) {
            com.ResetObjectTexturesFromChild(SPManager.PassToSMV.ModelChild);
        }


        findAttachModButtons();

        // LayoutButtonEnableStatus(false, true);

        mGLView = (GLSurfaceView) findViewById(R.id.graphics_glsurfaceview1);
        gestureDec = new ScaleGestureDetector(this.getApplicationContext(), this);


        mGLView.setEGLContextClientVersion(2);
        renderer = new MyRenderer();
        mGLView.setRenderer(renderer);

        //  LayoutButtonEnableStatus(true , false);


    }

    @Override
    protected void onPause() {
        Logger.log("onPause");
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        Logger.log("onResume");
        super.onResume();
        mGLView.onResume();


        if (RESETACTIVITY) {
            master = null;
            actualObject = SPManager.PassToSMV.ModelParent;
            com = new ColorModManager(actualObject);
            com.Init();
            RESETACTIVITY = false;
        }
        if (SPManager.PassToSMV.HasParent == true) {
            com.ResetObjectTexturesFromChild(SPManager.PassToSMV.ModelChild);
        } else {
            //RESETACTIVITY = true;
        }

        // com.ResetObjectTextures();
        // actualObject = SPManager.PassToSMV.ModelParent; // SingleModelView.actualObject;
        //  com = new ColorModManager(actualObject);
        //  com.Init();

    }

    @Override
    protected void onStop() {
        Logger.log("onStop");
        super.onStop();
    }

    private void copy(Object src) {
        try {
            Logger.log("Copying data from master Activity!");
            Field[] fs = src.getClass().getDeclaredFields();
            for (Field f : fs) {
                f.setAccessible(true);
                f.set(this, f.get(src));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean onTouchEvent(MotionEvent me) {

        gestureDec.onTouchEvent(me);

        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            xpos = me.getX();
            ypos = me.getY();
            return true;
        }

        if (me.getAction() == MotionEvent.ACTION_UP) {
            xpos = -1;
            ypos = -1;
            touchTurn = 0;
            touchTurnUp = 0;
            return true;
        }

        if (me.getAction() == MotionEvent.ACTION_MOVE) {
            float xd = me.getX() - xpos;
            float yd = me.getY() - ypos;

            xpos = me.getX();
            ypos = me.getY();

            touchTurn = xd / -100f;
            touchTurnUp = yd / -100f;
            return true;
        }


        try {
            Thread.sleep(15);
        } catch (Exception e) {
            // No need for this...
        }

        return super.onTouchEvent(me);
    }

    protected boolean isFullscreenOpaque() {
        return true;
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.dmv_btn_camera_down: {
                cam.moveCamera(Camera.CAMERA_MOVEUP, camSpeed);
                break;
            }
            case R.id.dmv_btn_camera_up: {
                cam.moveCamera(Camera.CAMERA_MOVEDOWN, camSpeed);
                break;
            }
            case R.id.dmv_btn_camera_right: {
                cam.moveCamera(Camera.CAMERA_MOVELEFT, camSpeed);
                break;
            }
            case R.id.dmv_btn_camera_left: {
                cam.moveCamera(Camera.CAMERA_MOVERIGHT, camSpeed);
                break;
            }

            default: {
                // flatSelectedModifierIndex = view.getId();
                com.UserSelectedMIndex = view.getId();

                // Set the bottom options to be invisible.
                ll_Camera_ExtraOptions.setVisibility(View.GONE);


                com.SetCurrentColAsRevertColor();
                openMaterialColorDialog();
            }


        }
    }

    @Override
    public void beforeRendering(int i) {

    }

    @Override
    public void afterRendering(int i) {


    }

    @Override
    public void setCurrentObject3D(Object3D object3D) {

    }

    @Override
    public void setCurrentShader(GLSLShader glslShader) {

    }

    @Override
    public void setTransparency(float v) {

    }

    @Override
    public void onDispose() {

    }

    @Override
    public boolean repeatRendering() {
        return false;
    }


    class MyRenderer implements GLSurfaceView.Renderer {

        private boolean hasToCreateBuffer = false;
        private GL10 lastInstance = null;
        private int w = 0;
        private int h = 0;
        private int fps = 0;
        private int lfps = 0;
        AssetManager assMan;
        InputStream is;

        private long time = System.currentTimeMillis();

        public MyRenderer() {
            Texture.defaultToMipmapping(true);
            Texture.defaultTo4bpp(true);
        }

        public void addObj() {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog = new ProgressDialog(
                                    DetailedViewModelActivity.this);
                            pDialog.setMessage("Downloading and setting up model, please wait a few seconds ...");
                            pDialog.setIndeterminate(true);
                            pDialog.setCancelable(false);
                            pDialog.show();
                        }
                    });
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                }
            }.execute();

            try {

                ParseFile objFile = (ParseFile) actualObject.get("serialized_obj_mtl");

                byte[] compressedData = objFile.getData();
                byte[] ObjData = Helper.decompress(compressedData);

                ByteArrayInputStream bis = new ByteArrayInputStream(ObjData);
                thing = Object3D.mergeAll(Loader.loadSerializedObject(bis));
                thing.setCulling(false);

                thing.setCollisionMode(Object3D.COLLISION_CHECK_NONE);
                //  thing = Object3D.mergeAll(Loader.loadOBJ(getResources().getAssets().open(DetailedViewModelActivity.ModelFilename + ".obj"),
                //        getResources().getAssets().open(DetailedViewModelActivity.ModelFilename + "_mtl"), 20));
                //thing.build();
                //thing.setShader(shader);
                thing.setSpecularLighting(true);
            } catch (Exception er) {
                Helper.ShowDialogue("Not enough RAM", "Cannot display this model", getApplicationContext());
                pDialog.dismiss();
                finish();
            }
            world.addObject(thing);


           // Helper.ShowDialogue("Success: ", "Loading complete", getApplicationContext());
            com.ResetObjectTextures();




        }


        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Resources res = getResources();

            if (master == null) {
                fb = new FrameBuffer(width, height);

                world = new World();

                shader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.vertexshader_offset)), Loader.loadTextFile(res.openRawResource(R.raw.fragmentshader_offset)));


                TextureManager tm = TextureManager.getInstance();

                // int total = tm.getTextureCount();

                // Texture tex1 = new Texture(Helper.GetColorBitmap(new SimpleVector(.5, 0, 0), 16, 16));

                // tm.replaceTexture("__obj-Color:75/115/8", tex1);
                //   thing.setTexture();


                //world.addObject(plane);

                light = new Light(world);
                light.enable();

                light.setIntensity(90, 90, 90);
                light.setPosition(SimpleVector.create(-10, -50, -100));

                world.setAmbientLight(90, 90, 90);

                cam = world.getCamera();


                addObj();


                cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
                cam.lookAt(thing.getTransformedCenter());

                SetDefaultModelPosColor(true);
                MemoryHelper.compact();
                world.compileAllObjects();
               // if (master == null) {
                //    Logger.log("Saving master Activity!");
                    master = DetailedViewModelActivity.this;
               // }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                       // Helper.ShowDialogue("Download success: ", " Getting ready to display, a few more seconds please", getApplicationContext());
                    }
                });

            } else {
                if (lastInstance != gl) {
                    Logger.log("Setting buffer creation flag...");
                    this.hasToCreateBuffer = true;
                    w = width;
                    h = height;
                }
            }
            lastInstance = gl;

            // --------------- Add camera stuff here
        }

        public void SetDefaultModelPosColor(boolean backColorAsWell) {
            String transforms = null;
            transforms = actualObject.getString("model_rot");
            if (transforms != null) {
                thing.setRotationMatrix(MathString.StringToMat(transforms));
            }
            transforms = actualObject.getString("model_trans");
            if (transforms != null) {
                thing.setTranslationMatrix(MathString.StringToMat(transforms));
            }
            transforms = actualObject.getString("model_scale");
            if (transforms != null) {
                thing.setScale(Float.parseFloat(transforms));
            }
            // --
            String dir = actualObject.getString("cam_dir_vec");
            String up = actualObject.getString("cam_up_vec");
            if (dir != null && up != null) {
                cam.setOrientation(MathString.StringToVec(dir), MathString.StringToVec(up));
            }
            String pos = actualObject.getString("cam_pos_vec");
            if (pos != null) {
                // cam.setPositionToCenter(thing);
                cam.setPosition(MathString.StringToVec(pos));
            }

            // Set the background color as well. It is not needed if the model is reset
            // to capture image before saving. that is why this boolean
            // is present.
            if (backColorAsWell) {
                String backColor = actualObject.getString("bk_color_vec");
                if (backColor != null) {
                    // cam.setPositionToCenter(thing);
                    SimpleVector bkColor = MathString.StringToVec(backColor);
                    back.setTo((int) bkColor.x, (int) bkColor.y, (int) bkColor.z, 0xff);
                }
            }
            // ---------------------

        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Logger.log("onSurfaceCreated");
        }


        public void onDrawFrame(GL10 gl) {

            // Increase the counter if the image has to be taken.
            // Checking the counter to be 0 makes sure the UI is turned off.
            if (CaptureImage == ScreenCaptureState.CAPTURE_EXIT_1) {
                captureCounter++;
            }
            if (CaptureImage == ScreenCaptureState.CAPTURE_ONLY_1) {
                captureCounter++;
            }

            // Enable user interaction only if capture image algorithm is not in process.
            if (this.hasToCreateBuffer) {
                Logger.log("Recreating buffer...");
                hasToCreateBuffer = false;
                fb = new FrameBuffer(w, h);
            }

            if (touchTurn != 0 && captureCounter == 0) {
                //plane.rotateY(touchTurn);
                thing.rotateY(touchTurn);
                touchTurn = 0;
            }

            if (touchTurnUp != 0 && captureCounter == 0) {
                //plane.rotateX(touchTurnUp);
                thing.rotateX(touchTurnUp);
                touchTurnUp = 0;

            }

            //shader.setUniform("heightScale", scale);

            fb.clear(back);
            world.renderScene(fb);
            world.draw(fb);
            //blitNumber(lfps, 5, 5);
            fb.display();

            if (System.currentTimeMillis() - time >= 1000) {
                lfps = fps;
                fps = 0;
                time = System.currentTimeMillis();
            }
            fps++;

            if (thing != null && thingScale > 0 && captureCounter == 0)
                thing.setScale(thingScale);


            // Capture image before saving logic.
            if (captureCounter == captureCounterLimit) {

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pDialog = new ProgressDialog(
                                        DetailedViewModelActivity.this);
                                pDialog.setMessage("Capturing model image, please wait..");
                                pDialog.setIndeterminate(true);
                                pDialog.setCancelable(false);
                                pDialog.show();
                            }
                        });
                        // Showing progress dialog before sending http request

                    }

                    @Override
                    protected Void doInBackground(Void... voids) {


                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {


                    }
                }.execute();

                Bitmap captureBitmap = SavePNG(gl);


                // Save file ptr inside the Model_UserMade object
                try {

//                    Helper.ShowDialogue("Success: ", " Model can be viewed in \"My Models\"", getApplicationContext());

                    // If you need to exit after the save, then exit, otherwise just reset
                    // the camera capture flag.
                    if (CaptureImage == ScreenCaptureState.CAPTURE_EXIT_1) {

                        // Convert bitmap to byte array
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        captureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        userMadeModel.put("icon_0", Helper.UploadParseFile(byteArray, false));
                        userMadeModel.save();


                        CaptureImage = ScreenCaptureState.UNUSED;
                        captureCounter = 0;

                        // Set the new model to be shown in the singlemodelview activity
                        // for easy buy
                        SPManager.PassToSMV = new SMVSharedModel(actualObject, userMadeModel, 1);
                        Helper.LaunchActivity(getApplicationContext(),
                                SingleModelView.class);

                        finish();
                    } else if (CaptureImage == ScreenCaptureState.CAPTURE_ONLY_1) {
                        try {
                            captureBitmap = Helper.AddWatermark(getApplicationContext(), captureBitmap, 3);
                        }catch(Exception er) {
                                System.out.println(er.getMessage());
                            }
                        shareBitmap(captureBitmap, "screenshop3d");
                        CaptureImage = ScreenCaptureState.UNUSED;
                        captureCounter = 0;
                    }

                    CaptureImage = ScreenCaptureState.UNUSED;
                    captureCounter = 0;

                } catch (Exception er) {
                    CaptureImage = ScreenCaptureState.UNUSED;
                    captureCounter = 0;

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                    }
                });


                // exit
            }


        }

        private void blitNumber(int number, int x, int y) {
            if (font != null) {
                String sNum = Integer.toString(number);

                for (int i = 0; i < sNum.length(); i++) {
                    char cNum = sNum.charAt(i);
                    int iNum = cNum - 48;
                    fb.blit(font, iNum * 5, 0, x, y, 5, 9, 5, 9, 10, true, null);
                    x += 5;
                }
            }
        }
    }

    ProgressDialog pDialog;
    File shareFile;

    private void shareBitmap(final Bitmap bitmap, final String fileName) {
        try {
            shareFile = new File(getApplicationContext().getCacheDir(), fileName + ".png");
            FileOutputStream fOut = new FileOutputStream(shareFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            shareFile.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
            intent.setType("image/png");
            startActivity(intent);
            pDialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onScale(ScaleGestureDetector detector) {

        float curr = detector.getCurrentSpan();
        float prev = detector.getPreviousSpan();

        if (curr == prev)
            return true;

        if (curr > prev)
            DetailedViewModelActivity.thingScale += 0.01f;
        else
            DetailedViewModelActivity.thingScale -= 0.01f;

/*
        div /= 5000;

		scale += div;

		if (scale > 0.063f) {
			scale = 0.063f;
		}
		if (scale < 0) {
			scale = 0;
		}
*/
        //   HelloShader.thingScale += 0.01f;
        return true;
    }

    public boolean onScaleBegin(ScaleGestureDetector detector) {
        // TODO Auto-generated method stub
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector detector) {
        // TODO Auto-generated method stub
    }

    /**
     * Merges the height map into the alpha channel of the normal map.
     *
     * @author EgonOlsen
     */
    private static class AlphaMerger implements ITextureEffect {

        private int[] alpha = null;

        public AlphaMerger(int[] alpha) {
            this.alpha = alpha;
        }

        public void apply(int[] arg0, int[] arg1) {
            int end = arg1.length;
            for (int i = 0; i < end; i++) {
                arg0[i] = arg1[i] & 0x00ffffff | alpha[i];
            }
        }

        public boolean containsAlpha() {
            return true;
        }

        public void init(Texture arg0) {
            // TODO Auto-generated method stub
        }
    }

    /**
     * Extracts the alpha channel from a texture.
     *
     * @author EgonOlsen
     */
    private static class TexelGrabber implements ITextureEffect {

        private int[] alpha = null;

        public void apply(int[] arg0, int[] arg1) {
            alpha = new int[arg1.length];
            int end = arg1.length;
            for (int i = 0; i < end; i++) {
                alpha[i] = (arg1[i] << 24);
            }
        }

        public int[] getAlpha() {
            return alpha;
        }

        public boolean containsAlpha() {
            return true;
        }

        public void init(Texture arg0) {
            // TODO Auto-generated method stub
        }
    }
}
