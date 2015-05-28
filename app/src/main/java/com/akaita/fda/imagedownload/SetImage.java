package com.akaita.fda.imagedownload;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.akaita.fda.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by mikel on 21/05/2015.
 */
public class SetImage {
    public enum Method {
        ASYNCTASK,
        CACHE
    }

    private static Method method = Method.CACHE;
    private static boolean indicatorEnabled = false;

    private SetImage(){

    }

    public static void setImage(Context context, final ImageView imageView, String url) {
        switch (method){
            case ASYNCTASK:
                Log.d(getMethod().toString(), "Asynctack method, downloading image: " + url);
                Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        context.getResources().getResourcePackageName(R.drawable.image_download_placeholder) + '/' +
                        context.getResources().getResourceTypeName(R.drawable.image_download_placeholder) + '/' +
                        context.getResources().getResourceEntryName(R.drawable.image_download_placeholder) );
                imageView.setImageURI(imageUri);
                new DownloadImageTask(imageView).execute(url);
                break;
            case CACHE:
                Log.d(getMethod().toString(), "Cached method, downloading image: " + url);
                Picasso.with(context).load(url)
                        .placeholder(R.drawable.image_download_placeholder)
                        .error(R.drawable.image_download_error)
                        .resize(context.getResources().getInteger(R.integer.caches_artist_thumb_big),
                                0)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                imageView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
                Picasso.with(context).setIndicatorsEnabled(indicatorEnabled);
                break;
        }
    }

    public static void setMethod(Method newMethod) {
        method = newMethod;
    }

    public static Method getMethod() {
        return method;
    }

    public static void setCacheIndicatorEnabled(boolean enable) {
        indicatorEnabled = enable;
    }

    public static boolean isCacheIndicatorEnabled() {
        return indicatorEnabled;
    }

}
