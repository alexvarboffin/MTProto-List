package com.walhalla.mtprotolist.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.walhalla.mtprotolist.entity.MtprotoProxy;

import java.util.List;

@Dao
public interface MtprotoProxyDao {
//    @Query("SELECT * FROM MtprotoProxy")
//    List<MtprotoProxy> getAllProxies();

    @Query("SELECT * FROM MtprotoProxy")
    LiveData<List<MtprotoProxy>> getAllProxies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MtprotoProxy proxy);

//    @Insert
//    void insert(MtprotoProxy proxy);

//    @Delete
//    void delete(MtprotoProxy proxy);

    @Query("DELETE FROM mtprotoproxy WHERE _id = :a")
    void deleteContactById(Long a);
}
