package com.walhalla.mtprotolist.presenter;

import android.content.Context;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.walhalla.mtprotolist.db.AppDatabase;
import com.walhalla.mtprotolist.db.LocalDatabaseRepo;
import com.walhalla.mtprotolist.db.MtprotoProxyDao;
import com.walhalla.mtprotolist.entity.MtprotoProxy;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyListPresenter {
    private final MtprotoProxyDao dao;

    private final ExecutorService databaseWriteExecutor;
    private final LiveData<List<MtprotoProxy>> allProxies;
    private final MtprotoProxyView view;
    private final Handler handler;

    public ProxyListPresenter(Context context, MtprotoProxyView view,/*,  MtprotoProxyDao dao,*/ Handler handler) {
        AppDatabase database = LocalDatabaseRepo.getDatabase(context, "mtproto");
        dao = database.mtprotoProxyDao();
        allProxies = dao.getAllProxies();
        databaseWriteExecutor = Executors.newSingleThreadExecutor();
        this.handler = handler;
        this.view = view;
        loadData();
    }

    private void loadData() {
        LiveData<List<MtprotoProxy>> proxies = dao.getAllProxies();
        proxies.observeForever(view::showProxies);
    }

    public void delete(MtprotoProxy data) {
        databaseWriteExecutor.execute(() ->

        {
            dao.deleteContactById(data._id);
            handler.post(this::loadData);
        });

    }
}
