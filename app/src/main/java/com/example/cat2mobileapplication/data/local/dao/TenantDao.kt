package com.example.cat2mobileapplication.data.local.dao

import androidx.room.*
import com.example.cat2mobileapplication.data.model.Tenant
import kotlinx.coroutines.flow.Flow

@Dao
interface TenantDao {

    @Query("SELECT * FROM tenants ORDER BY name ASC")
    fun getAllTenants(): Flow<List<Tenant>>

    @Query("SELECT * FROM tenants WHERE id = :id")
    suspend fun getTenantById(id: String): Tenant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTenant(tenant: Tenant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTenants(tenants: List<Tenant>)

    @Update
    suspend fun updateTenant(tenant: Tenant)

    @Delete
    suspend fun deleteTenant(tenant: Tenant)

    @Query("DELETE FROM tenants WHERE id = :id")
    suspend fun deleteTenantById(id: String)
}
