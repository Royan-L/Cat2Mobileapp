package com.example.cat2mobileapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cat2mobileapplication.data.local.dao.LeaseDao
import com.example.cat2mobileapplication.data.local.dao.PropertyDao
import com.example.cat2mobileapplication.data.local.dao.RentPaymentDao
import com.example.cat2mobileapplication.data.local.dao.TenantDao
import com.example.cat2mobileapplication.data.model.Lease
import com.example.cat2mobileapplication.data.model.LeaseStatus
import com.example.cat2mobileapplication.data.model.PaymentStatus
import com.example.cat2mobileapplication.data.model.Property
import com.example.cat2mobileapplication.data.model.PropertyStatus
import com.example.cat2mobileapplication.data.model.PropertyType
import com.example.cat2mobileapplication.data.model.RentPayment
import com.example.cat2mobileapplication.data.model.Tenant
import com.example.cat2mobileapplication.data.model.TenantStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.Volatile

@Database(
    entities = [
        Property::class,
        Tenant::class,
        Lease::class,
        RentPayment::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun tenantDao(): TenantDao
    abstract fun leaseDao(): LeaseDao
    abstract fun rentPaymentDao(): RentPaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "real_estate_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val properties = listOf(
                Property(
                    id = "p1",
                    title = "Sunset Heights Apt 4B",
                    address = "123 Ocean Drive, Miami, FL",
                    type = PropertyType.APARTMENT,
                    monthlyRent = 2400.0,
                    bedrooms = 2,
                    bathrooms = 2,
                    status = PropertyStatus.RENTED
                ),
                Property(
                    id = "p2",
                    title = "Maplewood Suburban House",
                    address = "456 Oak Avenue, Austin, TX",
                    type = PropertyType.HOUSE,
                    monthlyRent = 3200.0,
                    bedrooms = 4,
                    bathrooms = 3,
                    status = PropertyStatus.RENTED
                ),
                Property(
                    id = "p3",
                    title = "Downtown Luxury Condo",
                    address = "789 Broadway, New York, NY",
                    type = PropertyType.CONDO,
                    monthlyRent = 4500.0,
                    bedrooms = 1,
                    bathrooms = 1,
                    status = PropertyStatus.AVAILABLE
                ),
                Property(
                    id = "p4",
                    title = "Silicon Valley Studio",
                    address = "101 Tech Way, San Jose, CA",
                    type = PropertyType.STUDIO,
                    monthlyRent = 2100.0,
                    bedrooms = 1,
                    bathrooms = 1,
                    status = PropertyStatus.AVAILABLE
                )
            )

            val tenants = listOf(
                Tenant(
                    id = "t1",
                    name = "Alice Johnson",
                    email = "alice.j@example.com",
                    phone = "+1 (555) 019-2834",
                    propertyId = "p1",
                    leaseStart = "2024-01-01",
                    leaseEnd = "2025-01-01",
                    status = TenantStatus.ACTIVE
                ),
                Tenant(
                    id = "t2",
                    name = "Bob Smith",
                    email = "bob.smith@example.com",
                    phone = "+1 (555) 048-5920",
                    propertyId = "p2",
                    leaseStart = "2023-06-15",
                    leaseEnd = "2025-06-15",
                    status = TenantStatus.ACTIVE
                ),
                Tenant(
                    id = "t3",
                    name = "Charlie Davis",
                    email = "charlie.d@example.com",
                    phone = "+1 (555) 839-2011",
                    propertyId = null,
                    leaseStart = "",
                    leaseEnd = "",
                    status = TenantStatus.PENDING
                )
            )

            val rents = listOf(
                RentPayment(
                    id = "r1",
                    tenantId = "t1",
                    tenantName = "Alice Johnson",
                    propertyTitle = "Sunset Heights Apt 4B",
                    amount = 2400.0,
                    dueDate = "2025-02-01",
                    paidDate = "2025-02-01",
                    status = PaymentStatus.PAID
                ),
                RentPayment(
                    id = "r2",
                    tenantId = "t2",
                    tenantName = "Bob Smith",
                    propertyTitle = "Maplewood Suburban House",
                    amount = 3200.0,
                    dueDate = "2025-02-01",
                    paidDate = null,
                    status = PaymentStatus.OVERDUE
                ),
                RentPayment(
                    id = "r3",
                    tenantId = "t1",
                    tenantName = "Alice Johnson",
                    propertyTitle = "Sunset Heights Apt 4B",
                    amount = 2400.0,
                    dueDate = "2025-03-01",
                    paidDate = null,
                    status = PaymentStatus.PENDING
                )
            )

            val leases = listOf(
                Lease(
                    id = "l1",
                    propertyId = "p1",
                    propertyTitle = "Sunset Heights Apt 4B",
                    tenantId = "t1",
                    tenantName = "Alice Johnson",
                    startDate = "2024-01-01",
                    endDate = "2025-01-01",
                    monthlyRent = 2400.0,
                    securityDeposit = 2400.0,
                    status = LeaseStatus.ACTIVE
                ),
                Lease(
                    id = "l2",
                    propertyId = "p2",
                    propertyTitle = "Maplewood Suburban House",
                    tenantId = "t2",
                    tenantName = "Bob Smith",
                    startDate = "2023-06-15",
                    endDate = "2025-06-15",
                    monthlyRent = 3200.0,
                    securityDeposit = 3200.0,
                    status = LeaseStatus.ACTIVE
                )
            )

            db.propertyDao().insertProperties(properties)
            db.tenantDao().insertTenants(tenants)
            db.rentPaymentDao().insertRentPayments(rents)
            db.leaseDao().insertLeases(leases)
        }
    }
}
