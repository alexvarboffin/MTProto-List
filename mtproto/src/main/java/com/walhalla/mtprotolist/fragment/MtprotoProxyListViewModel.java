package com.walhalla.mtprotolist.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.walhalla.mtprotolist.entity.MtprotoProxy;


import java.util.List;

public class MtprotoProxyListViewModel extends AndroidViewModel
//        ViewModel
{

//@@00    private final com.walhalla.mtprotolist.presenter.MtprotoRepository repository;
    //@@@private final LiveData<List<MtprotoProxy>> proxyList;

    // Добавьте LiveData и методы для полей ввода прокси (host, port, и т. д.)

    private final MutableLiveData<Boolean> proxyAddedEvent = new MutableLiveData<>();

    public MtprotoProxyListViewModel(@NonNull Application application) {
        super(application);
        //@@00      repository = new MtprotoRepository(application);
        //@@@ proxyList = repository.getAllProxies();
    }


    // Добавьте методы для получения и установки значений полей ввода

    public LiveData<Boolean> getProxyAddedEvent() {
        return proxyAddedEvent;
    }

    public void onAddProxyClicked(MtprotoProxy proxy) {
        //@@00     repository.insertProxy(proxy);
        proxyAddedEvent.setValue(true);//setValue(T) method to update the LiveData object from the main thread
        //proxyAddedEvent.postValue(true);//work thread
    }

    public void onDeleteProxyClicked(MtprotoProxy proxy) {
        //@@00   repository.deleteProxy(proxy);// Удалите прокси из базы данных
    }

}
