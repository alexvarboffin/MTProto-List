package com.walhalla;

//Deprecated use UnifiedNativeAd
//import com.google.android.gms.ads.formats.NativeAppInstallAd;
//import com.google.android.gms.ads.formats.NativeContentAd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.NativeAdOptions;

import com.google.android.gms.ads.nativead.NativeAd;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class NativeHelper {

    private static final int DEFAULT_NUMBER_OF_ADS = 5;
    private final String id;

    //публичный метод для инициализации и первой загрузки
    public synchronized void prefetchAds(Context context) {
        mContext = new WeakReference<>(context);
        setupAds(context);
        fetchAd(); //загружаем первый блок
    }

    public interface Callback {
        void onAdCountChanged(List<NativeAd> mNativeAds);

        void onAdCountChanged(NativeAd o);

    }

    private final List<NativeAd> mNativeAds = new ArrayList<>();
    private final List<Callback> mAdNativeListeners = new ArrayList<>();

    //максимальное число предварительно загруженных рекламных блоков
    private final int NUMBER_OF_ADS;
    private final int MAX_FETCH_ATTEMPT; //лимит попыток загрузки рекламы;
    //     List of native ads that have been successfully loaded.
    //private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    private final SparseArray<NativeAd> adMapAtIndex = new SparseArray<>(); //NativeAd

    private int mNoOfFetchedAds;
    private int mFetchFailCount;
    private WeakReference<Context> mContext = new WeakReference<>(null);
    private String admobReleaseUnitId;

    // The AdLoader used to load ads.
    private AdLoader adLoader;

    NativeHelper(int NUMBER_OF_ADS, int MAX_FETCH_ATTEMPT, String id) {
        this.NUMBER_OF_ADS = NUMBER_OF_ADS;
        this.MAX_FETCH_ATTEMPT = MAX_FETCH_ATTEMPT;
        this.id = id;
    }

//    NativeHelper() {
//        this.NUMBER_OF_ADS = DEFAULT_NUMBER_OF_ADS;
//        this.MAX_FETCH_ATTEMPT = 3;
//    }

    public synchronized void addListener(@Nullable Callback listener) {
        mAdNativeListeners.add(listener);
    }

    private synchronized void setupAds(final Context context) {

//        final LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService
//                (Context.LAYOUT_INFLATER_SERVICE);

        adLoader = new AdLoader.Builder(context, id)
                .forNativeAd(nativeAd -> {
//                        NativeTemplateStyle styles = new
//                                NativeTemplateStyle.Builder()
//                                //.withMainBackgroundColor(background)
//                                .build();
//                        TemplateView template = findViewById(R.id.my_template);
//                        TemplateView template = inflater.inflate(R.layout.new_layout,null);
//                        TemplateView template = new TemplateView(context);
//                        template.setStyles(styles);
//                        template.bind(var0);
                    onAdFetched(nativeAd);
                })
//                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
//                    @Override
//                    public void onUnifiedNativeAdLoaded(@NonNull UnifiedNativeAd unifiedNativeAd) {
//
//                    }
//                })
                .withAdListener(new AdListener() {


                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mFetchFailCount++; //инкрементим кол-во неудачных загрузок
                        ensurePrefetchAmount(); //проверяем, что предварительно загружено
                        // достаточное количество рекламы
                    }
                })
                .withNativeAdOptions(new com.google.android.gms.ads.nativead.NativeAdOptions.Builder().build())
                //.withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();


// Load the Native ads.
//        adLoader.loadAds(
//                new AdRequest.Builder()
//                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                        .build(), NUMBER_OF_ADS);
    }

    private synchronized void onAdFetched(NativeAd ad) {
        if (canUseThisAd(ad)) {
            mNativeAds.add(ad);

            mNoOfFetchedAds++; //@ инкрементим счетчик загруженной рекламы
        }
        //сбрасываем счетчик неудачных загрузок
        mFetchFailCount = 0;
        ensurePrefetchAmount();
        // оповещаем подписчиков об изменении количества  загруженной рекламы
        //if (!adLoader.isLoading()) {
        notifyObserversOfAdSizeChange(ad);
        //}
    }

    /**
     * NativeAd
     */
    public synchronized NativeAd getAdForIndex(final int index) {
        //пытаемся получить рекламный блок по индексу из маппинга
        NativeAd adNative = adMapAtIndex.get(index);
        //если в маппинге по указанному индексу ничего не нашлось, и хотя бы один блок рекламы уже был загружен
        if (adNative == null && mNativeAds.size() > 0) {
            //забираем первый загруженный блок из коллекции
            adNative = mNativeAds.remove(0);
            //и пишем его в маппинг по указанному индексу, для будущих запросов
            if (adNative != null) {
                adMapAtIndex.put(index, adNative);
            }
        }
        // проверяем, достаточно ли рекламы загружено
        ensurePrefetchAmount();
        return adNative;
    }

    //оповещение всех подписчиков о изменении количества загруженной рекламы
    private void notifyObserversOfAdSizeChange(NativeAd nativeAd) {
        for (Callback listener : mAdNativeListeners) {
            listener.onAdCountChanged(nativeAd);
        }
    }


    //проверяем, достаточно ли рекламы загрузили, и если нет, то загружаем еще,
    //в случае превышения числа неудачных попыток ничего не делаем
    private synchronized void ensurePrefetchAmount() {
        if (mNativeAds.size() < NUMBER_OF_ADS && (mFetchFailCount < MAX_FETCH_ATTEMPT)) {
            fetchAd();
        }
//        else {
//            for (Callback listener : mAdNativeListeners) {
//                listener.onAdCountChanged(mNativeAds);
//            }
//        }
    }

    //валидация полученной с сервера рекламы,  пример
    private boolean canUseThisAd(NativeAd nativeAd) {
        if (nativeAd != null) {
            CharSequence header, body;
            header = nativeAd.getHeadline();
            body = nativeAd.getBody();
            //проверяем, подходит ли нам эта реклама :)
            return !TextUtils.isEmpty(header) && !TextUtils.isEmpty(body);
        }

        return false;
    }

    @SuppressLint("MissingPermission")
    private synchronized void fetchAd() {
        Context context = mContext.get();
        if (context != null) {
            AdRequest aaa = new AdRequest.Builder()
                    //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            adLoader.loadAd(aaa);

        } else {
            mFetchFailCount++;
            //DLog.d("Контекст пустой, считаем за неудачную попытку");
        }
    }
}