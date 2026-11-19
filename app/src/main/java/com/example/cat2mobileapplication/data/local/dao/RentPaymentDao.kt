package com.example.cat2mobileapplication.data.local.dao

import androidx.room.*
import com.example.cat2mobileapplication.data.model.PaymentStatus
import com.example.cat2mobileapplication.data.model.RentPayment
import kotlinx.coroutines.flow.Flow

@Dao
interface RentPaymentDao {

    @Query("SELECT * FROM rent_payments ORDER BY dueDate DESC")
    fun getAllRentPayments(): Flow<List<RentPayment>>

    @Query("SELECT * FROM rent_payments WHERE id = :id")
    suspend fun getRentPaymentById(id: String): RentPayment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentPayment(rentPayment: RentPayment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentPayments(rentPayments: List<RentPayment>)

    @Update
    suspend fun updateRentPayment(rentPayment: RentPayment)

    @Query("UPDATE rent_payments SET status = :status, paidDate = :paidDate WHERE id = :id")
    suspend fun updateRentStatus(id: String, status: PaymentStatus, paidDate: String?)

    @Delete
    suspend fun deleteRentPayment(rentPayment: RentPayment)

    @Query("DELETE FROM rent_payments WHERE id = :id")
    suspend fun deleteRentPaymentById(id: String)
}
