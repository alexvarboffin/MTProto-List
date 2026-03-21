//package com.walhalla.mtprotolist.presenter;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.walhalla.mtprotolist.Config;
//import com.walhalla.mtprotolist.adapter.MtprotoProxyAdapter;
//import com.walhalla.mtprotolist.entity.MtprotoProxy;
//import com.walhalla.ui.DLog;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class MProtoFragmentPresenter {
//
//
//    public interface MProtoFragmentView {
//        void handleProxy(MtprotoProxy proxy);
//    }
//
//    private static final String PREF_NAME = "MyPrefs";
//    private static final String PREF_KEY_LOCKED_ITEMS = "lockedItems";
//
//    private final Context mContext;
//
//    private final SharedPreferences mSharedPreferences;
//    private Set<String> mLockedItems;
//
//    private final int[] mLockedNumbers = new int[]{4, 6, 7, 9};
//
//
//    MProtoFragmentView mView;
//
//    public MProtoFragmentPresenter(Context context, MProtoFragmentView p0) {
//        mContext = context;
//        mView = p0;
//        mSharedPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//
//        // Получаем множество строк из настроек
//        mLockedItems = mSharedPreferences.getStringSet(PREF_KEY_LOCKED_ITEMS, new HashSet<>());
//
////        mListView.setOnItemClickListener((parent, view, position, id) -> {
////            int itemNumber = position + 1; // Список начинается с 0, а элементы с 1
////            if (mLockedItems.contains(String.valueOf(itemNumber))) {
////                unlockItem(itemNumber);
////            } else {
////                Toast.makeText(mContext, "Элемент уже разблокирован", Toast.LENGTH_SHORT).show();
////            }
////        });
//
//        updateList();
//    }
//
//    private void unlockItem(int itemNumber) {
//        mLockedItems.remove(String.valueOf(itemNumber));
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        editor.putStringSet(PREF_KEY_LOCKED_ITEMS, mLockedItems);
//        editor.apply();
//        updateList();
//        Toast.makeText(mContext, "Элемент разблокирован", Toast.LENGTH_SHORT).show();
//    }
//
//    private void updateList() {
//        ArrayList<String> items = new ArrayList<>();
//        for (int i = 1; i <= mLockedNumbers.length; i++) {
//            if (!mLockedItems.contains(String.valueOf(i))) {
//                items.add("Item " + i);
//            }
//        }
//        mAdapter.clear();
//        mAdapter.addAll(items);
//        mAdapter.notifyDataSetChanged();
//    }
//
//    public void unlockItem(MtprotoProxy data, int adapterPosition) {
//        unlockItem(adapterPosition);
//        getViewState().displayVideoBanner();
//        mView.handleProxy(data);
//    }
//
//
//}
//
