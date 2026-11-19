package com.example.cat2mobileapplication.data.local

import androidx.room.TypeConverter
import com.example.cat2mobileapplication.data.model.LeaseStatus
import com.example.cat2mobileapplication.data.model.PaymentStatus
import com.example.cat2mobileapplication.data.model.PropertyStatus
import com.example.cat2mobileapplication.data.model.PropertyType
import com.example.cat2mobileapplication.data.model.TenantStatus

class Converters {

    @TypeConverter
    fun fromPropertyType(type: PropertyType): String = type.name

    @TypeConverter
    fun toPropertyType(value: String): PropertyType = enumValueOf(value)

    @TypeConverter
    fun fromPropertyStatus(status: PropertyStatus): String = status.name

    @TypeConverter
    fun toPropertyStatus(value: String): PropertyStatus = enumValueOf(value)

    @TypeConverter
    fun fromTenantStatus(status: TenantStatus): String = status.name

    @TypeConverter
    fun toTenantStatus(value: String): TenantStatus = enumValueOf(value)

    @TypeConverter
    fun fromLeaseStatus(status: LeaseStatus): String = status.name

    @TypeConverter
    fun toLeaseStatus(value: String): LeaseStatus = enumValueOf(value)

    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus): String = status.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = enumValueOf(value)
}
