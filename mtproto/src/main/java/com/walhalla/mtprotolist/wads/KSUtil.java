package com.walhalla.mtprotolist.wads;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.walhalla.ui.DLog;

import java.util.HashSet;
import java.util.Set;

public class KSUtil {

    private static final String BLOCKED_ITEMS_KEY = "blocked_items_";


    private static KSUtil instance;
    private final SharedPreferences sharedPreferences;


    public Set<Integer> getBlockedItems() {
        return blockedItems;
    }

    private final Set<Integer> blockedItems = new HashSet<>();

    // Приватный конструктор, чтобы предотвратить создание экземпляров класса
    private KSUtil(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Метод для получения единственного экземпляра класса с синхронизацией
    public static synchronized KSUtil getInstance(Context context) {
        if (instance == null) {
            instance = new KSUtil(context);
        }
        return instance;
    }

    public void initialize(Set<Integer> data0) {

        Set<String> stringSet = new HashSet<>();
        for (Integer i : data0) {
            if (i != null) {
                stringSet.add(String.valueOf(i));
            }
        }
        Set<String> tmp = sharedPreferences.getStringSet(BLOCKED_ITEMS_KEY, stringSet);
        for (String s : tmp) {
            try {
                blockedItems.add(Integer.valueOf(s));
            } catch (Exception e) {
                DLog.handleException(e);
            }
        }
    }

    public void unlockItem(int position) {
        blockedItems.remove(position);
        saveUnlockedItems();

    }

    public boolean isItemLocked(int position) {
        return blockedItems.contains(position);
    }

    public void saveUnlockedItems() {
        Set<String> stringSet = new HashSet<>();
        for (Integer item : blockedItems) {
            if (item != null) {
                stringSet.add(String.valueOf(item));
            }
        }
        sharedPreferences.edit().putStringSet(BLOCKED_ITEMS_KEY, stringSet).apply();
    }
}
