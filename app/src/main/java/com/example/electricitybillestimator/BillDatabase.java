package com.example.electricitybillestimator;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {BillRecord.class}, version = 1)
public abstract class BillDatabase extends RoomDatabase {

    private static BillDatabase instance;

    public abstract BillDao billDao();

    public static synchronized BillDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            BillDatabase.class, "bill_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // For assignment, okay. Avoid in real apps.
                    .build();
        }
        return instance;
    }
}
