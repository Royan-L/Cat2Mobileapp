package com.example.cat2mobileapplication.data.local.dao

import androidx.room.*
import com.example.cat2mobileapplication.data.model.Property
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Query("SELECT * FROM properties ORDER BY title ASC")
    fun getAllProperties(): Flow<List<Property>>

    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getPropertyById(id: String): Property?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: Property)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<Property>)

    @Update
    suspend fun updateProperty(property: Property)

    @Delete
    suspend fun deleteProperty(property: Property)

    @Query("DELETE FROM properties WHERE id = :id")
    suspend fun deletePropertyById(id: String)
}
