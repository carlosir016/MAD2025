package com.example.helloworld.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IUserEntity {
    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT name FROM User")
    suspend fun getNames():List<String>

    @Query("SELECT password FROM User where name = :userName")
    suspend fun getPassword(userName:String):String

}