package com.example.survirun;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.survirun.data.FriendRoom;

import java.util.List;

@Dao
public interface FriendDao {
    @Query("SELECT * FROM friendroom")
    List<FriendRoom> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(FriendRoom... friendRoom);

    @Update
    void update(FriendRoom... friendRoom);
}
