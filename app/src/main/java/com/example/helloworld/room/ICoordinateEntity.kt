package com.example.helloworld.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ICoordinateEntity {
    @Insert
    suspend fun insert (coord: CoordinateEntity)

    @Query ("SELECT name FROM Coordinate")
    suspend fun getNames(): List<String>

    @Query("SELECT * FROM Coordinate")
    suspend fun getAll():List<CoordinateEntity>
}