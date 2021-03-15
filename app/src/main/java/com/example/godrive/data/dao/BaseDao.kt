package com.example.godrive.data.dao

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: List<T>): List<Long>

    @Delete
    fun delete(t: T)

    @Delete
    fun delete(t: List<T>)

    @Update
    fun update(t: T)

    @Update
    fun update(t: List<T>)
}