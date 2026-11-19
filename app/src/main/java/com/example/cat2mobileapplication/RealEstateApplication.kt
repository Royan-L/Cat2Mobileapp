package com.example.cat2mobileapplication

import android.app.Application
import com.example.cat2mobileapplication.data.local.AppDatabase
import com.example.cat2mobileapplication.data.repository.RealEstateRepository

class RealEstateApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val repository: RealEstateRepository by lazy {
        RealEstateRepository(
            propertyDao = database.propertyDao(),
            tenantDao = database.tenantDao(),
            rentPaymentDao = database.rentPaymentDao(),
            leaseDao = database.leaseDao()
        )
    }
}
