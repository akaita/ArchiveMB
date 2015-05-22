package com.akaita.fda;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

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

    private SetImage(){

    }

    public static void setImage(Context context, final ImageView imageView, String url) {
        switch (method){
            case ASYNCTASK:
                Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        context.getResources().getResourcePackageName(R.mipmap.ic_launcher) + '/' +
                        context.getResources().getResourceTypeName(R.mipmap.ic_launcher) + '/' +
                        context.getResources().getResourceEntryName(R.mipmap.ic_launcher) );
                imageView.setImageURI(imageUri);
                new DownloadImageTask(imageView).execute(url);
                break;
            case CACHE:
                Picasso.with(context).load(url)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .resize(150,
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
                Picasso.with(context).setIndicatorsEnabled(true);
                break;
        }
    }

    public static void setMethod(Method newMethod) {
        method = newMethod;
    }

    public static Method getMethod() {
        return method;
    }
}
