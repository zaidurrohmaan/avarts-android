package com.zaidu.avarts.data.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromFloatList(value: List<Float>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toFloatList(value: String): List<Float> {
        if (value.isEmpty()) {
            return emptyList()
        }
        return value.split(",").map { it.toFloat() }
    }
}