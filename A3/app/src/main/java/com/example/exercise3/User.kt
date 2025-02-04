package com.example.exercise3

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "username") val username: String,
//    @ColumnInfo(name = "password") val hashedPassword: String,
    @ColumnInfo(name = "image") val imagePath: String
)