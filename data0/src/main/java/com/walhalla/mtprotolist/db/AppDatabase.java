package com.walhalla.mtprotolist.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.walhalla.mtprotolist.entity.MtprotoProxy;

@Database(entities = {MtprotoProxy.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MtprotoProxyDao mtprotoProxyDao();
}
