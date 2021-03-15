package com.example.godrive.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.godrive.data.models.Person

@Dao
interface PersonDao : BaseDao<Person> {
    @Transaction
    @Query("SELECT * FROM person")
    fun selectAll(): List<Person>

    @Query("DELETE FROM person")
    fun clearTable()
}