package com.walhalla.mtprotolist.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.walhalla.ui.DLog;


public class LocalDatabaseRepo {

    private static AppDatabase database;
    static final Migration MIGRATION_1_2 = new Migration(1, 43) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Поскольку мы не изменяли таблицу, здесь больше ничего не нужно делать.
            DLog.d("===================================");
        }
    };

    //private CategoryDao dao;

    private static final Object LOCK = new Object();

    private static final RoomDatabase.Callback dbCallback = new RoomDatabase.Callback() {

//                    @Override
//                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
//                        super.onCreate(db);
//
//                        Executors.newSingleThreadExecutor().execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                getInstance().getDatabase().questionDao().init();
//                            }
//                        });
//
//                    }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    public synchronized static AppDatabase getDatabase(Context context, String dbName) {
        if (database == null) {
            synchronized (LOCK) {
                if (database == null) {
                    database = Room.databaseBuilder(context, AppDatabase.class,
                            dbName
                            //"electroclash.db" //+ (char) 0x200B
                    )
                            //@@@.createFromAsset("info/info.ttf")
                            //.openHelperFactory(new AssetSQLiteOpenHelperFactory())
                            //.allowMainThreadQueries()
                            .addMigrations(MIGRATION_1_2)
                            .addCallback(dbCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return database;
    }

//    public List<Question> getStoreInfo(Context context, String searchStrLowerCase) {
//        if (dao == null) {
//            dao = LocalDatabaseRepo.getDatabase(context).getKeywordsDao();
//        }
//        return dao.getKeywords(searchStrLowerCase + "%");
//    }

//    public String loadJson(Context context) {
//        String json = null;
//        try {
//            InputStream inputStream = context.getAssets().open(
//                    "database/en.json");
//            int size = inputStream.available();
//            byte[] buffer = new byte[size];
//            int o = inputStream.read(buffer);
//            inputStream.close();
//            json = new String(buffer, "UTF-8");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }
//
//    public List<Question> queryShortList(Context context) {
//        Gson gson = new GsonBuilder().create();
//        String content = loadJson(context);
//        Type listType = new TypeToken<ArrayList<QWrapper>>() {
//        }.getType();
//        List<Question> lll = new ArrayList<>();
//        List<QWrapper> list = gson.fromJson(content, listType);
//        for (QWrapper q : list) {
//            lll.add(new Question(q.getId(), q.getQuestion(), q.getIcon(), q.getAnswer(), q.getDescription(),
//                    q.getQRbVariants(), 0));
//        }
//        return lll;
//    }

}
