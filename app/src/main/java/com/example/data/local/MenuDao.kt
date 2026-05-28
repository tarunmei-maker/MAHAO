package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MenuItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Query("SELECT * FROM menu_items ORDER BY category, name")
    fun getAllMenuItems(): Flow<List<MenuItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItem>)

    @Update
    suspend fun updateMenuItem(item: MenuItem)

    @Delete
    suspend fun deleteMenuItem(item: MenuItem)

    @Query("SELECT COUNT(*) FROM menu_items")
    suspend fun getMenuItemCount(): Int
}
