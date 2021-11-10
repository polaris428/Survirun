package com.example.survirun.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FriendRoom {
    @PrimaryKey
    @NonNull
    public String email;

    @ColumnInfo(name = "profile")
    public String profile;

    @ColumnInfo(name = "name")
    public String name;
}
