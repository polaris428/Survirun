package com.example.survirun;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.survirun.data.FriendRoom;

@Database(entities = {FriendRoom.class}, version = 1)
public abstract class FriendDB extends RoomDatabase {
    private static FriendDB INSTANCE = null;

    public abstract FriendDao friendDao();

    public static FriendDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), FriendDB.class, "friend.db").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
