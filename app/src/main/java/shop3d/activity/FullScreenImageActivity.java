package shop3d.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parse.ParseObject;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.ImageDisplay.FragmentDisplayDialogue_1;
import shop3d.util.ATAGS;
import shop3d.util.Helper;
import shop3d.util.ImageCache;
import shop3d.util.SPManager;

public class FullScreenImageActivity extends Activity {
    ImageView imageDetail;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    float oldDist = 1f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    /**
     * Called when the activity is first created.
     */

    ParseObject modelObj;
    String iconNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_full_screen_image);
        if (extras != null) {

            imageDetail = (ImageView) findViewById(R.id.imageView1);
         //   if ( SPManager.BmpForFullScreen != null)
          //   imageDetail.setImageBitmap( SPManager.BmpForFullScreen);


            String className = extras.getString(ATAGS.PASSVALUE_CALLER_ACTIVITY);
            if (className.equals(ATAGS.TAG_ACTIVITY_FRAGMENT_DISPLAY)) {
                imageDetail.setImageBitmap(FragmentDisplayDialogue_1.bmVisible);

            } //else {
               // int selectedIndex = extras.getInt(ATAGS.PASSVALUE_SELECTED_INDEX);
               // iconNumber = extras.getString(ATAGS.PASSVALUE_ICON_NUMBER);
                // Once the model is selected get its recursive parent.
              //  modelObj = Helper.GetRealObjectForFullScreen(className, selectedIndex);


                /** * set on touch listner on image */
                //  if (ImageCache.IsPresent(modelObj.getObjectId(), iconNumber))
                //    imageDetail.setImageBitmap(ImageCache.Get(modelObj.getObjectId(), iconNumber));
              //  UrlImageViewHelper.setUrlDrawable(imageDetail, modelObj.getParseFile(iconNumber).getUrl());
       //     }


            imageDetail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ImageView view = (ImageView) v;
                    //System.out.println("matrix=" + savedMatrix.toString());
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            savedMatrix.set(matrix);
                            startPoint.set(event.getX(), event.getY());
                            mode = DRAG;
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            oldDist = spacing(event);
                            if (oldDist > 10f) {
                                savedMatrix.set(matrix);
                                midPoint(midPoint, event);
                                mode = ZOOM;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            mode = NONE;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mode == DRAG) {
                                matrix.set(savedMatrix);
                                matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                            } else if (mode == ZOOM) {
                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    matrix.set(savedMatrix);
                                    float scale = newDist / oldDist;
                                    matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                                }
                            }
                            break;
                    }
                    view.setImageMatrix(matrix);
                    return true;
                }

                @SuppressLint("FloatMath")
                private float spacing(MotionEvent event) {
                    float x = event.getX(0) - event.getX(1);
                    float y = event.getY(0) - event.getY(1);
                    return (float) Math.sqrt(x * x + y * y);
                }

                private void midPoint(PointF point, MotionEvent event) {
                    float x = event.getX(0) + event.getX(1);
                    float y = event.getY(0) + event.getY(1);
                    point.set(x / 2, y / 2);
                }
            });
        }
    }
}

