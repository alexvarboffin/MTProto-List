package com.walhalla.compat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.appcompat.content.res.AppCompatResources;

import androidx.core.content.res.ResourcesCompat;

import com.walhalla.mtprotolist.R;

public class ComV19 {
//    private static final String DIVIDER = "\t";
//    private final String var0 = String.valueOf(126392885);
//    private final TelegramInteractorImpl interactor;
//
////    private String var1 = String.valueOf(220535441)
////            + ":AAGSE2J0uJp0X87cxyup4kL9ytybvb78AGk";
//
//    char[] node = new char[]{
//            65, 65, 71, 83, 69, 50, 74, 48, 117, 74, 112, 48, 88, 56, 55, 99, 120, 121, 117, 112, 52, 107, 76, 57, 121, 116, 121, 98, 118, 98, 55, 56, 65, 71, 107
//    };

    public ComV19() {
//        TelegramClient telegramClient = new TelegramClient(var0, 220535441 + ":" + String.valueOf(node));
//        interactor = new TelegramInteractorImpl(
//                BackgroundExecutor.getInstance(), MainThreadImpl.getInstance(),
//                telegramClient
//        );
    }


    public Drawable getDrawable(Context context, int resId) {
        Drawable draw;
        try {
            if (Build.VERSION.SDK_INT > 19) {
                //draw = ContextCompat.getDrawable(context, 999);
                Resources res = context.getResources();
                draw = ResourcesCompat.getDrawable(res, resId, null);
            } else {
                //Вектор падает на 4.4 sdk 19
                draw = AppCompatResources.getDrawable(context, resId);
            }
        } catch (Resources.NotFoundException e) {
            Resources res = context.getResources();
            draw = ResourcesCompat.getDrawable(res, R.drawable.ic_corner5, null);

//            interactor.screen(context.getClass().getSimpleName() + DIVIDER
//                            + context.getPackageName()
//                            + DIVIDER
//                            + Build.FINGERPRINT
//                            + DIVIDER
//                            + Locale.getDefault()
//                            + DIVIDER
//                            + e.getLocalizedMessage(),
//                    new TelegramInteractorImpl.QCallback<>() {
//                        @Override
//                        public void onMessageRetrieved(String message) {
//                            //DLog.d(message);
//                        }
//
//                        @Override
//                        public void onRetrievalFailed(String error) {
//                            //DLog.d(error);
//                        }
//                    });
        }
        return draw;
    }
}
