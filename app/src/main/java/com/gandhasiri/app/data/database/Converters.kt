package com.gandhasiri.app.data.database

import androidx.room.TypeConverter
import com.gandhasiri.app.data.entities.HealthStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return Gson().toJson(list ?: emptyList<String>())
    }

    @TypeConverter
    fun fromHealthStatus(value: String?): HealthStatus {
        return value?.let { HealthStatus.valueOf(it) } ?: HealthStatus.GOOD
    }

    @TypeConverter
    fun toHealthStatus(status: HealthStatus?): String {
        return status?.name ?: HealthStatus.GOOD.name
    }
}
