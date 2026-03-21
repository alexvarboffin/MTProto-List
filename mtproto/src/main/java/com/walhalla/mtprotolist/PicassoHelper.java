package com.walhalla.mtprotolist;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.walhalla.ui.DLog;

import static android.content.Context.ACTIVITY_SERVICE;

public class PicassoHelper {

    private static PicassoHelper singleton;

    private Picasso mPicasso;

    private PicassoHelper() {}


    public static PicassoHelper getInstance(Context context) {
        if (singleton == null) {
            singleton = new PicassoHelper();
            Picasso.setSingletonInstance(singleton.getCustomPicasso(context));
        }
        return singleton;
    }


    private Picasso getCustomPicasso(Context context) {
        Picasso.Builder builder = new Picasso.Builder(context);
        //set 12% of available app memory for image cache
        builder.memoryCache(new LruCache(getBytesForMemCache(12, context)));
        //set request transformer
        Picasso.RequestTransformer requestTransformer = new Picasso.RequestTransformer() {
            @Override
            public Request transformRequest(Request request) {
                Log.d("image request", request.toString());
                return request;
            }
        };
        builder.requestTransformer(requestTransformer);
        return builder.build();
    }

    private int getBytesForMemCache(int percent, Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        double availableMemory = mi.availMem;

        return (int) (percent * availableMemory / 100);
    }


//    public void attachImages(String path, ViewGroup container, Context context) {
//        Picasso.with(context).load(data.get(i).icon)
//                .error(R.mipmap.ic_launcher)
//                .into(personViewHolder.personPhoto);
//    }

    public void loadImage(String icon, ImageView view, Context context) {
        DLog.d(icon);
        Picasso.get().load(icon)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.ic_unknown)


                //.fit().centerCrop()

                .resize(800, 400) // Width and Height
                .centerCrop() // Image scaling type
                .onlyScaleDown()

                //.fit()
                //.resize(500, 500).centerInside()

                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        //Try again online if cache failed
                        Picasso.get().load(icon)
                                .error(R.drawable.ic_unknown)

                                //.fit().centerCrop()
                                .resize(800, 400) // Width and Height
                                .centerCrop() // Image scaling type
                                .onlyScaleDown()


                                .into(view, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        DLog.handleException(e);
                                    }
                                });
                    }
                });
    }

    public void attachImagesEx(String path, LinearLayout container, Context context, String index) {
        if (path != null && !path.isEmpty()) {

            //Multiple
            final String[] strings = path.split("\\|");
            for (String href : strings) {

//                href = String.format(Locale.CANADA, "%1$s.jpg", href);
//                M.d( "attachImages: " + href);

                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                //imageView.setBackgroundColor(Color.GRAY);
                imageView.setContentDescription("@null");
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(0, 8, 0, 8);
//                params.addRule(RelativeLayout.BELOW, R.id.ButtonRecalculate);
//                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                container.addView(imageView, params);

                this.loadImage(href, imageView, context);
            }
        }
    }

    public void loadEmptyImage(ImageView view, Context context) {
        Picasso.get().load(R.drawable.ic_unknown)
                //.networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.ic_unknown)


                //.fit().centerCrop()

                .resize(800, 400) // Width and Height
                .centerCrop() // Image scaling type
                .onlyScaleDown()

                //.fit()
                //.resize(500, 500).centerInside()

                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        //Try again online if cache failed
                        Picasso.get().load(R.drawable.ic_unknown)
                                .error(R.drawable.ic_unknown)

                                //.fit().centerCrop()
                                .resize(800, 400) // Width and Height
                                .centerCrop() // Image scaling type
                                .onlyScaleDown()


                                .into(view, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        DLog.handleException(e);
                                    }
                                });
                    }
                });
    }

    public void rawCountryFlag(final String icon, ImageView view) {
//        DLog.d(icon);
        Picasso.get().load(icon)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.ic_unknown)

                //.fit().centerCrop()

//                    .resize(800, 400) // Width and Height
//                    .centerCrop() // Image scaling type
//                    .onlyScaleDown()

                //.fit()
                //.resize(500, 500).centerInside()

                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        DLog.handleException(e);
                        //Try again online if cache failed
                        Picasso.get().load(icon)
                                .error(R.drawable.ic_unknown)
                                //.fit().centerCrop()
//                                    .resize(800, 400) // Width and Height
//                                    .centerCrop() // Image scaling type
//                                    .onlyScaleDown()
                                .into(view, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e1) {
                                        DLog.handleException(e1);
                                    }
                                });
                    }
                });

    }
}