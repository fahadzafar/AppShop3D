package shop3d.util;

import android.view.View;
import android.widget.ImageView;

import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by Fahad on 11/9/2015.
 */
public class ImageDownloader {
/*
    public static void SetImageInView(final ParseObject model, String iconNumber, final ImageView iv) {
        if (ImageCache.IsPresent(model.getObjectId(), iconNumber)) {
            iv.setImageBitmap(ImageCache.Get(model.getObjectId(), iconNumber));
        } else {
            iv.setImageBitmap(null);
            DownloadNonBlockingImage(iconNumber, model,
                    iv);
        }
    }

    public static void SetImageHere(String iconNumber, final ImageView holdersImageView, final ParseObject model) {
        holdersImageView.setImageBitmap(null);
        if (ImageCache.IsPresent(model.getObjectId(), iconNumber)) {
            holdersImageView.setImageBitmap(ImageCache.Get(model.getObjectId(), iconNumber));
        } else {
            DownloadNonBlockingImage(iconNumber, model,
                    holdersImageView);
        }


    }



    /*

        if (ImageCache.IsPresent(fillModel.getObjectId(), "icon_0")) {
            holder.icon
                    .setImageBitmap(ImageCache.Get(fillModel.getObjectId(), "icon_0"));
        } else {
            DownloadNonBlockingImage(fillModel,
                    (ImageView) convertView
                            .findViewById(R.id.itemIcon),
                    position);
        }




    // Downloads the image that is associated with the question.
    public static void DownloadNonBlockingImage(final String iconNumber, final ParseObject model,
                                                final ImageView rView) {
        final ParseFile question_image = (ParseFile) model.get(iconNumber);
        question_image.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, com.parse.ParseException e) {
                if (e == null) {
                    // Check to see if the image was downloaded earlier
                    ImageCache.Put(model.getObjectId(), iconNumber,
                            data);
                    rView.setImageBitmap(ImageCache.Get(model.getObjectId(), iconNumber));

                } else {
                    // something went wrong
                }
            }
        });

    }*/
}
