package eating.well.recipe.keeper.app.data.database.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

data class MethodPointEntity(val title: String, val content: String) : Serializable
object MethodPointEntityListConverter {
    @TypeConverter
    fun fromString(value: String): List<MethodPointEntity> {
        val type = object : TypeToken<LinkedHashMap<String, String>>() {}.type
        val map: LinkedHashMap<String, String> = Gson().fromJson(value, type)
        val methodList = mutableListOf<MethodPointEntity>()
        for ((key, v) in map) {
            methodList.add(MethodPointEntity(title = key, content = v))
        }
        return methodList
    }

    @TypeConverter
    fun fromList(list: List<MethodPointEntity>): String {
        val map: LinkedHashMap<String, String> = LinkedHashMap()
        list.forEach { map[it.title] = it.content }
        val gson = Gson()
        val type = object : TypeToken<LinkedHashMap<String, String>>() {}.type
        return gson.toJson(map, type)
    }
}

/*
class DataConverter {

    @TypeConverter
    fun fromCountryLangList(value: List<MethodPointEntity>): String {
        val gson = Gson()
        val type = object : TypeToken<List<MethodPointEntity>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCountryLangList(value: String): List<MethodPointEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<MethodPointEntity>>() {}.type
        return gson.fromJson(value, type)
    }

}*/
