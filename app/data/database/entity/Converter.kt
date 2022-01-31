package com.android.app.data.database.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type


class DataConverter : Serializable {
    @TypeConverter // note this annotation
    fun fromOptionValuesList(optionValues: List<MethodPointEntity?>?): String? {
        if (optionValues == null) {
            return null
        }
        val gson = Gson()
        val type: Type =
            object : TypeToken<List<MethodPointEntity?>?>() {}.getType()
        return gson.toJson(optionValues, type)
    }

    @TypeConverter // note this annotation
    fun toOptionValuesList(optionValuesString: String?): List<MethodPointEntity>? {
        if (optionValuesString == null) {
            return null
        }
        val gson = Gson()
        val type: Type =
            object : TypeToken<List<MethodPointEntity?>?>() {}.getType()
        return gson.fromJson(optionValuesString, type)
    }
}
