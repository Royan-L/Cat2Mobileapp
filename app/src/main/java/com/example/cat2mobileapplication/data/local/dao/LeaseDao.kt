package com.example.cat2mobileapplication.data.local.dao

import androidx.room.*
import com.example.cat2mobileapplication.data.model.Lease
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaseDao {

    @Query("SELECT * FROM leases ORDER BY startDate DESC")
    fun getAllLeases(): Flow<List<Lease>>

    @Query("SELECT * FROM leases WHERE id = :id")
    suspend fun getLeaseById(id: String): Lease?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLease(lease: Lease)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeases(leases: List<Lease>)

    @Update
    suspend fun updateLease(lease: Lease)

    @Delete
    suspend fun deleteLease(lease: Lease)

    @Query("DELETE FROM leases WHERE id = :id")
    suspend fun deleteLeaseById(id: String)
}
