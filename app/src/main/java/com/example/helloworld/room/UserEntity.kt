package com.example.helloworld.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "password") val password:String
)
