package com.readandlearn.japanese.RoomDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Word.class}, exportSchema = false, version = 1)
public abstract class WordDatabase extends RoomDatabase{

    private static final String DB_NAME = "usersWords";
    private static WordDatabase instance;

    public static synchronized WordDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), WordDatabase.class, DB_NAME)
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract WordDao wordDao();
}
