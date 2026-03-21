package com.walhalla.mtprotolist.presenter;

import android.content.Context;

import com.walhalla.mtprotolist.db.AppDatabase;
import com.walhalla.mtprotolist.db.LocalDatabaseRepo;
import com.walhalla.mtprotolist.entity.MtprotoProxy;
import com.walhalla.mtprotolist.db.MtprotoProxyDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * MtprotoProxyListViewModel
 */

public class MtprotoRepository {
    //private final MtprotoProxyView view;

    private final MtprotoProxyDao dao;

    private final ExecutorService databaseWriteExecutor;

    public MtprotoRepository(Context context) {
        AppDatabase database = LocalDatabaseRepo.getDatabase(context, "mtproto");
        dao = database.mtprotoProxyDao();
        databaseWriteExecutor = Executors.newSingleThreadExecutor();
        //this.handler = handler;
        //this.view = view;
        //loadData();
    }





//    public void insertProxy(MtprotoProxy proxy) {
//        dao.insertProxy(proxy);
//    }



    public void insertProxy(MtprotoProxy proxy) {
        databaseWriteExecutor.execute(() -> {
            dao.insert(proxy);
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    //@@@
//                }
//            });
        });
    }

    public void deleteProxy(MtprotoProxy proxy) {
        databaseWriteExecutor.execute(() -> dao.deleteContactById(proxy._id));
    }
}

